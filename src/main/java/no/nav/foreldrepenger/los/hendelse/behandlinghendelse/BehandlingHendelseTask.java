package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.AksjonspunktType;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.beskyttelsesbehov.Beskyttelsesbehov;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Behandling;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.vedtak.felles.prosesstask.api.CommonTaskProperties;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.Kildesystem;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;

@Dependent
@ProsessTask(value = "håndter.behandlinghendelse", firstDelay = 10, thenDelay = 10)
public class BehandlingHendelseTask implements ProsessTaskHandler {

    static final String BEHANDLING_UUID = CommonTaskProperties.BEHANDLING_UUID;
    public static final String KILDE = "kildesystem";
    private static final Logger LOG = LoggerFactory.getLogger(BehandlingHendelseTask.class);

    private final FpsakBehandlingKlient fpsakKlient;
    private final FptilbakeBehandlingKlient fptilbakeKlient;

    private final BehandlingTjeneste behandlingTjeneste;

    private final OppgaveRepository oppgaveRepository;
    private final ReservasjonRepository reservasjonRepository;
    private final Beskyttelsesbehov beskyttelsesbehov;

    @Inject
    BehandlingHendelseTask(FpsakBehandlingKlient fpsakKlient,
                           FptilbakeBehandlingKlient fptilbakeKlient,
                           BehandlingTjeneste behandlingTjeneste,
                           OppgaveRepository oppgaveRepository,
                           ReservasjonRepository reservasjonRepository,
                           Beskyttelsesbehov beskyttelsesbehov) {
        this.fpsakKlient = fpsakKlient;
        this.fptilbakeKlient = fptilbakeKlient;
        this.behandlingTjeneste = behandlingTjeneste;
        this.oppgaveRepository = oppgaveRepository;
        this.reservasjonRepository = reservasjonRepository;
        this.beskyttelsesbehov = beskyttelsesbehov;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var behandlingUuid = UUID.fromString(prosessTaskData.getPropertyValue(BEHANDLING_UUID));
        var kilde = switch (Kildesystem.valueOf(prosessTaskData.getPropertyValue(KILDE))) {
            case FPSAK -> Fagsystem.FPSAK;
            case FPTILBAKE -> Fagsystem.FPTILBAKE;
        };

        // Hent eksterne data
        var dto = hentDto(behandlingUuid, kilde);
        var egenskaper = hentFagsakEgenskaper(dto, kilde);
        var beskyttelseKriterier = beskyttelsesbehov.getBeskyttelsesKriterier(new Saksnummer(dto.saksnummer()));

        // Hent eksisterende oppgave og behandling
        var eksisterendeOppgave = oppgaveRepository.hentAktivOppgave(new BehandlingId(behandlingUuid));
        var eksisterendeBehandling = oppgaveRepository.finnBehandling(behandlingUuid);

        // Bygg grunnlag for videre logikk
        var oppgaveGrunnlag = OppgaveGrunnlagUtleder.lagGrunnlag(dto, egenskaper);
        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, beskyttelseKriterier);

        var skalLageOppgave = skalLageOppgave(oppgaveGrunnlag);
        if (skalLageOppgave) {
            LOG.info("Oppretter oppgave for behandling {}", oppgaveGrunnlag.behandlingUuid());
            var oppgave = opprettOppgave(oppgaveGrunnlag, kilde, kriterier);
            opprettReservasjon(oppgave, eksisterendeOppgave, eksisterendeBehandling, oppgaveGrunnlag);
        }

        eksisterendeOppgave.ifPresent(o -> {
            LOG.info("Avslutter eksisterende oppgave {} for behandling {}", o.getId(), oppgaveGrunnlag.behandlingUuid());
            o.avsluttOppgave();
            if (o.harAktivReservasjon()) {
                avsluttReservasjon(o.getReservasjon());
            }
        });

        behandlingTjeneste.lagreBehandling(dto, kilde, eksisterendeBehandling, kriterier);
    }

    private void avsluttReservasjon(Reservasjon reservasjon) {
        reservasjon.setReservertTil(LocalDateTime.now().minusSeconds(1));
        reservasjonRepository.lagre(reservasjon);
    }

    private void opprettReservasjon(Oppgave oppgave, Optional<Oppgave> eksisterendeOppgave,
                                    Optional<Behandling> eksisterendeBehandling, OppgaveGrunnlag oppgaveGrunnlag) {
        var reservasjon = ReservasjonUtleder.utledReservasjon(oppgave, eksisterendeOppgave, eksisterendeBehandling, oppgaveGrunnlag);
        reservasjon.ifPresent(r -> {
            LOG.info("Opprettet reservasjon for oppgave {}", oppgave.getId());
            reservasjonRepository.lagre(r);
        });
    }

    private Oppgave opprettOppgave(OppgaveGrunnlag oppgaveGrunnlag, Fagsystem kilde, Set<AndreKriterierType> kriterier) {
        LOG.info("Utledet kriterier {} for oppgave til behandling {}", kriterier, oppgaveGrunnlag.behandlingUuid());

        var oppgaveEgenskaper = kriterier.stream()
            .map(k -> new OppgaveEgenskap.Builder().medAndreKriterierType(k)
                .medSisteSaksbehandlerForTotrinn(k == AndreKriterierType.TIL_BESLUTTER ? oppgaveGrunnlag.ansvarligSaksbehandlerIdent() : null)
                .build())
            .collect(Collectors.toSet());

        var oppgave = Oppgave.builder()
            .medSystem(kilde)
            .medSaksnummer(oppgaveGrunnlag.saksnummer())
            .medAktørId(oppgaveGrunnlag.aktørId())
            .medBehandlendeEnhet(oppgaveGrunnlag.behandlendeEnhetId())
            .medBehandlingType(oppgaveGrunnlag.behandlingstype())
            .medFagsakYtelseType(oppgaveGrunnlag.ytelse())
            .medAktiv(true)
            .medBehandlingOpprettet(oppgaveGrunnlag.opprettetTidspunkt())
            .medBehandlingId(new BehandlingId(oppgaveGrunnlag.behandlingUuid()))
            .medFørsteStønadsdag(oppgaveGrunnlag.førsteUttaksdatoForeldrepenger())
            .medBehandlingsfrist(oppgaveGrunnlag.behandlingsfrist() != null ? oppgaveGrunnlag.behandlingsfrist().atStartOfDay() : null)
            .medKriterier(oppgaveEgenskaper)
            .medFeilutbetalingBeløp(oppgaveGrunnlag.feilutbetalingBeløp())
            .medFeilutbetalingStart(oppgaveGrunnlag.feilutbetalingStart())
            .build();

        oppgaveRepository.opprettOppgave(oppgave);
        return oppgave;
    }

    private static boolean skalLageOppgave(OppgaveGrunnlag oppgaveGrunnlag) {
        var erPåVent = oppgaveGrunnlag.aksjonspunkt().stream()
            .filter(aksjonspunkt -> aksjonspunkt.status() == Aksjonspunktstatus.OPPRETTET)
            .anyMatch(a -> a.type() == AksjonspunktType.PÅ_VENT);
        var underBehandlingStatus = Set.of(BehandlingStatus.OPPRETTET, BehandlingStatus.UTREDES,
            BehandlingStatus.FATTER_VEDTAK).contains(oppgaveGrunnlag.behandlingStatus());
        var harOpprettetAksjonspunkt = oppgaveGrunnlag.aksjonspunkt().stream().anyMatch(a -> a.status().equals(Aksjonspunktstatus.OPPRETTET));
        return !erPåVent && underBehandlingStatus && harOpprettetAksjonspunkt;
    }

    private LosBehandlingDto hentDto(UUID behandlingUuid, Fagsystem kilde) {
        return kilde.equals(Fagsystem.FPSAK) ? fpsakKlient.hentLosBehandlingDto(behandlingUuid) : fptilbakeKlient.hentLosBehandlingDto(
            behandlingUuid);
    }

    private LosFagsakEgenskaperDto hentFagsakEgenskaper(LosBehandlingDto dto, Fagsystem kilde) {
        return kilde.equals(Fagsystem.FPSAK)
            ? new LosFagsakEgenskaperDto(dto.saksegenskaper())
            : fpsakKlient.hentLosFagsakEgenskaperDto(new Saksnummer(dto.saksnummer()));
    }

}
