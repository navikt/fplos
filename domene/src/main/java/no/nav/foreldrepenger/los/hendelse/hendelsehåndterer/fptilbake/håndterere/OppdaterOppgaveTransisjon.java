package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere;

import static no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter.RESERVASJON_VIDEREFØRT_NY_OPPGAVE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavehendelseHåndterer.FptilbakeData;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.TilbakekrevingHendelse;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgave.TilbakekrevingOppgave;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltreringKnytning;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

@ApplicationScoped
public class OppdaterOppgaveTransisjon implements FptilbakeOppgavetransisjonHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(OppdaterOppgaveTransisjon.class);


    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;

    private KøStatistikkTjeneste køStatistikkTjeneste;
    private OppgaveTjeneste oppgaveTjeneste;
    private ReservasjonTjeneste reservasjonTjeneste;


    public OppdaterOppgaveTransisjon() {
    }

    public OppdaterOppgaveTransisjon(OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                     KøStatistikkTjeneste køStatistikkTjeneste,
                                     OppgaveTjeneste oppgaveTjeneste,
                                     ReservasjonTjeneste reservasjonTjeneste) {
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
        this.køStatistikkTjeneste = køStatistikkTjeneste;
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.reservasjonTjeneste = reservasjonTjeneste;
    }


    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.OPPDATER_OPPGAVE;
    }

    @Override
    public void håndter(FptilbakeData data) {
        var eksisterendeOppgave = oppgaveTjeneste.hentAktivOppgave(data.hendelse().getBehandlingId())
                .orElseThrow(() -> new IllegalStateException("Fant ikke eksisterende oppgave"));
        var nyOppgave = lagOppgave(data.hendelse());
        vedlikeholdKøStatistikk(eksisterendeOppgave, nyOppgave);
        flyttReservasjon(eksisterendeOppgave, nyOppgave);
        oppgaveTjeneste.avsluttOppgaveMedEventLogg(eksisterendeOppgave, OppgaveEventType.GJENAPNET, RESERVASJON_VIDEREFØRT_NY_OPPGAVE);
    }

    private void vedlikeholdKøStatistikk(Oppgave eksisterendeOppgave, Oppgave nyOppgave) {
        // kan erstattes med query basert på oppgavebeholdning når beholdningen er bygget opp for tilbakekreving og fpsak-oppgaver
        var køKnytningerGammelOppgave = køStatistikkTjeneste.hentOppgaveFiltreringKnytningerForOppgave(eksisterendeOppgave);
        var køKnytningerNyOppgave = køStatistikkTjeneste.hentOppgaveFiltreringKnytningerForOppgave(nyOppgave);
        var knytninger = Stream.concat(køKnytningerGammelOppgave.stream(), køKnytningerNyOppgave.stream())
                .collect(Collectors.groupingBy(OppgaveFiltreringKnytning::oppgaveId,
                        Collectors.mapping(OppgaveFiltreringKnytning::oppgaveFiltreringId, Collectors.toList())));
        var utAvKø = Optional.ofNullable(knytninger.get(eksisterendeOppgave.getId())).orElse(List.of());
        var innPåKø = Optional.ofNullable(knytninger.get(nyOppgave.getId())).orElse(List.of());
        if (!utAvKø.isEmpty() || !innPåKø.isEmpty()) {
            utAvKø = new ArrayList<>(utAvKø);
            innPåKø = new ArrayList<>(innPåKø);
            utAvKø.removeAll(innPåKø);
            innPåKø.removeAll(Optional.ofNullable(knytninger.get(eksisterendeOppgave.getId())).orElse(List.of()));
        }
        LOG.info("Køstatistikk-knytninger mellom TBK oppgaveId og oppgaveFiltreringId: {}. På vei ut av køer {}, på vei inn i køer {}",
                knytninger, utAvKø, innPåKø);
        innPåKø.forEach(i -> køStatistikkTjeneste.lagre(nyOppgave, i, KøOppgaveHendelse.INN_FRA_ANNEN_KØ));
        utAvKø.forEach(i -> køStatistikkTjeneste.lagre(nyOppgave, i, KøOppgaveHendelse.UT_TIL_ANNEN_KØ));
    }

    private void flyttReservasjon(Oppgave eksisterendeOppgave, Oppgave nyOppgave) {
        var gammelReservasjon = eksisterendeOppgave.getReservasjon();
        boolean aktivReservasjon = gammelReservasjon != null && gammelReservasjon.erAktiv();
        if (aktivReservasjon) {
            var eksisterendeVarighetTil = gammelReservasjon.getReservertTil();
            var kandidatVarighetTil = LocalDateTime.now().plusHours(2);
            var nyVarighetTil = kandidatVarighetTil.isAfter(eksisterendeVarighetTil)
                    ? kandidatVarighetTil
                    : eksisterendeVarighetTil;
            var reservasjon = reservasjonTjeneste.reserverOppgaveBasertPåEksisterendeReservasjon(nyOppgave,
                    gammelReservasjon, nyVarighetTil);
            LOG.info("Oppretter ny forlenget reservasjonId {} varighet til {} reservert_av {}",
                    reservasjon.getId(), reservasjon.getReservertTil(), reservasjon.getReservertAv());
        }
    }

    private Oppgave lagOppgave(TilbakekrevingHendelse hendelse) {
        var egenskapFinner = new FptilbakeOppgaveEgenskapFinner(hendelse.getAksjonspunkter(), hendelse.getAnsvarligSaksbehandler());
        var nyOppgave = oppgaveFra(hendelse);
        oppgaveTjeneste.lagre(nyOppgave);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(nyOppgave, egenskapFinner);
        return nyOppgave;
    }

    private static TilbakekrevingOppgave oppgaveFra(TilbakekrevingHendelse hendelse) {
        var feilutbetalingStart = Optional.ofNullable(hendelse.getFørsteFeilutbetalingDato()).map(LocalDate::atStartOfDay).orElse(null);
        return TilbakekrevingOppgave.tbuilder()
                .medBeløp(hendelse.getFeilutbetaltBeløp())
                .medFeilutbetalingStart(feilutbetalingStart)
                .medHref(hendelse.getHref())
                .medSystem(hendelse.getFagsystem().name())
                .medFagsakSaksnummer(Long.valueOf(hendelse.getSaksnummer()))
                .medAktorId(new AktørId(hendelse.getAktørId()))
                .medBehandlendeEnhet(hendelse.getBehandlendeEnhet())
                .medBehandlingType(hendelse.getBehandlingType())
                .medFagsakYtelseType(hendelse.getYtelseType())
                .medAktiv(true)
                .medBehandlingOpprettet(hendelse.getBehandlingOpprettetTidspunkt())
                .medUtfortFraAdmin(false)
                .medBehandlingId(hendelse.getBehandlingId())
                .build();
    }
}
