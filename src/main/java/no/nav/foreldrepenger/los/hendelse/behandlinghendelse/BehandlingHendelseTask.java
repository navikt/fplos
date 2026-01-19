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
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingTjeneste;
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
    private final KriterieUtleder kriterieUtleder;
    private final ReservasjonRepository reservasjonRepository;
    private final ReservasjonUtleder reservasjonUtleder;
    private final OppgaveGrunnlagUtleder oppgaveGrunnlagUtleder;

    @Inject
    BehandlingHendelseTask(FpsakBehandlingKlient fpsakKlient,
                           FptilbakeBehandlingKlient fptilbakeKlient,
                           BehandlingTjeneste behandlingTjeneste,
                           OppgaveRepository oppgaveRepository,
                           ReservasjonRepository reservasjonRepository,
                           KriterieUtleder kriterieUtleder,
                           ReservasjonUtleder reservasjonUtleder,
                           OppgaveGrunnlagUtleder oppgaveGrunnlagUtleder) {
        this.fpsakKlient = fpsakKlient;
        this.fptilbakeKlient = fptilbakeKlient;
        this.behandlingTjeneste = behandlingTjeneste;
        this.oppgaveRepository = oppgaveRepository;
        this.kriterieUtleder = kriterieUtleder;
        this.reservasjonRepository = reservasjonRepository;
        this.reservasjonUtleder = reservasjonUtleder;
        this.oppgaveGrunnlagUtleder = oppgaveGrunnlagUtleder;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var behandlingUuid = UUID.fromString(prosessTaskData.getPropertyValue(BEHANDLING_UUID));
        var kilde = Kildesystem.valueOf(prosessTaskData.getPropertyValue(KILDE));

        var dto = hentDto(behandlingUuid, kilde);
        var oppgaveGrunnlag = oppgaveGrunnlagUtleder.lagGrunnlag(dto);

        var eksisterendeOppgave = finnEksisterendeOppgave(oppgaveGrunnlag.behandlingUuid());
        eksisterendeOppgave.ifPresent(o -> LOG.info("Funnet eksisterende oppgave {} for behandling {}", o.getId(), oppgaveGrunnlag.behandlingUuid()));

        var skalLageOppgave = skalLageOppgave(oppgaveGrunnlag);
        if (skalLageOppgave) {
            LOG.info("Oppretter oppgave for behandling {}", oppgaveGrunnlag.behandlingUuid());
            var oppgave = opprettOppgave(oppgaveGrunnlag);
            opprettReservasjon(oppgave, eksisterendeOppgave, oppgaveGrunnlag);
        }

        eksisterendeOppgave.ifPresent(o -> {
            LOG.info("Avslutter eksisterende oppgave {} for behandling {}", o.getId(), oppgaveGrunnlag.behandlingUuid());
            o.avsluttOppgave();
            if (o.harAktivReservasjon()) {
                avsluttReservasjon(o.getReservasjon());
            }
        });

        behandlingTjeneste.safeLagreBehandling(dto, switch (kilde) {
            case FPSAK -> Fagsystem.FPSAK;
            case FPTILBAKE -> Fagsystem.FPTILBAKE;
        });
    }

    private void avsluttReservasjon(Reservasjon reservasjon) {
        reservasjon.setReservertTil(LocalDateTime.now().minusSeconds(1));
        reservasjonRepository.lagre(reservasjon);
    }

    private void opprettReservasjon(Oppgave oppgave, Optional<Oppgave> eksisterendeOppgave, OppgaveGrunnlag oppgaveGrunnlag) {
        var reservasjon = reservasjonUtleder.utledReservasjon(oppgave, eksisterendeOppgave, oppgaveGrunnlag);
        reservasjon.ifPresent(r -> {
            LOG.info("Opprettet reservasjon for oppgave {}", oppgave.getId());
            reservasjonRepository.lagre(r);
        });
    }

    private Oppgave opprettOppgave(OppgaveGrunnlag oppgaveGrunnlag) {
        var kriterier = kriterieUtleder.utledKriterier(oppgaveGrunnlag);
        LOG.info("Utledet kriterier {} for oppgave til behandling {}", kriterier, oppgaveGrunnlag.behandlingUuid());

        var oppgaveEgenskaper = kriterier.stream()
            .map(k -> new OppgaveEgenskap.Builder().medAndreKriterierType(k)
                .medSisteSaksbehandlerForTotrinn(k == AndreKriterierType.TIL_BESLUTTER ? oppgaveGrunnlag.ansvarligSaksbehandlerIdent() : null)
                .build())
            .collect(Collectors.toSet());

        var oppgave = Oppgave.builder()
            .medSystem(Fagsystem.FPSAK)
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
            .build();

        oppgaveRepository.opprettOppgave(oppgave);
        return oppgave;
    }

    private boolean skalLageOppgave(OppgaveGrunnlag oppgaveGrunnlag) {
        var erPåVent = oppgaveGrunnlag.aksjonspunkt().stream()
            .filter(aksjonspunkt -> aksjonspunkt.status() == Aksjonspunktstatus.OPPRETTET)
            .anyMatch(a -> a.type() == AksjonspunktType.PÅ_VENT);
        var underBehandlingStatus = Set.of(BehandlingStatus.OPPRETTET, BehandlingStatus.UTREDES,
            BehandlingStatus.FATTER_VEDTAK).contains(oppgaveGrunnlag.behandlingStatus());
        var harOpprettetAksjonspunkt = oppgaveGrunnlag.aksjonspunkt().stream().anyMatch(a -> a.status().equals(Aksjonspunktstatus.OPPRETTET));
        return !erPåVent && underBehandlingStatus && harOpprettetAksjonspunkt;
    }

    private Optional<Oppgave> finnEksisterendeOppgave(UUID behandlingUuid) {
        return oppgaveRepository.hentAktivOppgave(new BehandlingId(behandlingUuid));
    }

    private LosBehandlingDto hentDto(UUID behandlingUuid, Kildesystem kilde) {
        return kilde.equals(Kildesystem.FPSAK) ? fpsakKlient.hentLosBehandlingDto(behandlingUuid) : fptilbakeKlient.hentLosBehandlingDto(
            behandlingUuid);
    }
}
