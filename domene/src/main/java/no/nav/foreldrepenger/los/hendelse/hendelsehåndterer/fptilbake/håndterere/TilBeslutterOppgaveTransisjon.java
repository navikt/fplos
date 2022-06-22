package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere;

import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.TIL_BESLUTTER;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavehendelseHåndterer.FptilbakeData;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.TilbakekrevingHendelse;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgave.TilbakekrevingOppgave;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

@ApplicationScoped
public class TilBeslutterOppgaveTransisjon implements FptilbakeOppgavetransisjonHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(TilBeslutterOppgaveTransisjon.class);
    private KøStatistikkTjeneste køStatistikk;
    private OppgaveTjeneste oppgaveTjeneste;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;

    public TilBeslutterOppgaveTransisjon() {
    }

    public TilBeslutterOppgaveTransisjon(KøStatistikkTjeneste køStatistikk, OppgaveTjeneste oppgaveTjeneste, OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer) {
        this.køStatistikk = køStatistikk;
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.OPPRETT_BESLUTTEROPPGAVE;
    }

    @Override
    public void håndter(FptilbakeData data) {
        LOG.info("TBK Oppretter beslutteroppgave.");
        var behandlingId = data.hendelse().getBehandlingId();
        var oppgave = oppgaveTjeneste.hentAktivOppgave(behandlingId).orElseThrow();
        køStatistikk.lagre(oppgave, KøOppgaveHendelse.LUKKET_OPPGAVE);
        oppgaveTjeneste.avsluttOppgaveMedEventLogg(oppgave, OppgaveEventType.LUKKET, ReservasjonKonstanter.OPPGAVE_AVSLUTTET);

        var beslutterOppgave = oppgaveTjeneste.lagre(oppgaveFra(data.hendelse()));
        var egenskapFinner = new FptilbakeOppgaveEgenskapFinner(data.hendelse().getAksjonspunkter(), data.hendelse().getAnsvarligSaksbehandler());
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(beslutterOppgave, egenskapFinner);
        køStatistikk.lagre(beslutterOppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
        var oel = OppgaveEventLogg.builder()
                .type(OppgaveEventType.OPPRETTET)
                .andreKriterierType(TIL_BESLUTTER)
                .behandlendeEnhet(beslutterOppgave.getBehandlendeEnhet())
                .behandlingId(beslutterOppgave.getBehandlingId())
                .build();
        oppgaveTjeneste.lagre(oel);
    }

    private static TilbakekrevingOppgave oppgaveFra(TilbakekrevingHendelse hendelse) {
        return TilbakekrevingOppgave.tbuilder()
                .medBeløp(hendelse.getFeilutbetaltBeløp())
                .medFeilutbetalingStart(feilutbetalingStart(hendelse))
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

    private static LocalDateTime feilutbetalingStart(TilbakekrevingHendelse hendelse) {
        return Optional.ofNullable(hendelse.getFørsteFeilutbetalingDato())
                .map(LocalDate::atStartOfDay)
                .orElse(null);
    }


}
