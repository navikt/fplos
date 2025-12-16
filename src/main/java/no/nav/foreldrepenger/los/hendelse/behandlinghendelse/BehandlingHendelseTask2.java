package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.util.Optional;
import java.util.UUID;

import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.tilbakekreving.TilbakekrevingHendelseHåndterer;
import no.nav.vedtak.felles.prosesstask.api.CommonTaskProperties;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.hendelser.behandling.Kildesystem;

@Dependent
@ProsessTask(value = "håndter.behandlinghendelse", firstDelay = 10, thenDelay = 10)
public class BehandlingHendelseTask2 implements ProsessTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingHendelseTask2.class);

    public static final String HENDELSE_UUID = "hendelseUuid";
    public static final String BEHANDLING_UUID = CommonTaskProperties.BEHANDLING_UUID;
    public static final String KILDE = "kildesystem";

    private final BehandlingKlient fpsakKlient;
    private final BehandlingKlient fptilbakeKlient;

    private final TilbakekrevingHendelseHåndterer tilbakekrevingHendelseHåndterer;
    private final FpsakOppgaveHendelseHåndterer fpsakOppgaveHendelseHåndterer;

    @Inject
    public BehandlingHendelseTask2(FpsakBehandlingKlient fpsakKlient,
                                   FptilbakeBehandlingKlient fptilbakeKlient,
                                   TilbakekrevingHendelseHåndterer tilbakekrevingHendelseHåndterer,
                                   FpsakOppgaveHendelseHåndterer fpsakOppgaveHendelseHåndterer) {
        this.fpsakKlient = fpsakKlient;
        this.fptilbakeKlient = fptilbakeKlient;
        this.tilbakekrevingHendelseHåndterer = tilbakekrevingHendelseHåndterer;
        this.fpsakOppgaveHendelseHåndterer = fpsakOppgaveHendelseHåndterer;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var behandlingUuid = UUID.fromString(prosessTaskData.getPropertyValue(BEHANDLING_UUID));
        var kilde = Kildesystem.valueOf(prosessTaskData.getPropertyValue(KILDE));

        var behandlingDto = hentBehandlingDto(behandlingUuid, kilde, new Saksnummer(prosessTaskData.getSaksnummer()));

        var eksisterendeOppgave = finnEksisterendeOppgave(behandlingDto.behandlingUuid());
        eksisterendeOppgave.ifPresent(Oppgave::avsluttOppgave); //NB! avslutte reservasjon??? avkorter ikke reservasjonen nå. Sjekk queries og frontend

        var skalLageOppgave = skalLageOppgave(behandlingDto);
        if (skalLageOppgave) {
            var oppgave = opprettOppgave(behandlingDto);
            opprettReservasjon(oppgave, eksisterendeOppgave);
        }

    }

    private void opprettReservasjon(Oppgave oppgave, Optional<Oppgave> eksisterendeOppgave) {
        var reservasjon = utledReservasjon(oppgave, eksisterendeOppgave);
        lagreReservasjon(reservasjon);
    }

    private Optional<Reservasjon> utledReservasjon(Oppgave oppgave, Optional<Oppgave> eksisterendeOppgave) {
        //Eks reservasjon til vanlig oppgave -> arve, logg hvis avvik fra dto?
        //Eks reservasjon til beslutter -> ikke arve, kanskje lete etter forrige beslutter
        //Eks reservasjon papir til vanlig -> ikke arve
        //
        //Beslutter tilbake til saksbehandler -> opprett reservasjon fra behandlingdto
        //Ingen eks reservasjon -> opprett reservasjon fra behandlingdto dersom revurdering manuell behandling
        //Endret enhet -> ikke viderefør
        return null;
    }

    private Oppgave opprettOppgave(Behandling behandlingDto) {
        var kriterier = utledKriterier(behandlingDto);

        return null;
    }

    private boolean skalLageOppgave(Behandling behandlingDto) {
        return behandlingDto.aksjonspunkt().stream().anyMatch(a -> a.status().equals(Aksjonspunktstatus.OPPRETTET));
    }

    private Optional<Oppgave> finnEksisterendeOppgave(UUID uuid) {
        return null;
    }

    private Behandling hentBehandlingDto(UUID behandlingUuid, Kildesystem kilde, Saksnummer saksnummer) {
        if (kilde.equals(Kildesystem.FPSAK)) {
            return mapFraFpsak(fpsakKlient.hentLosBehandlingDto(behandlingUuid));
        }
        var losFagsakEgenskaperDto = fpsakKlient.hentLosFagsakEgenskaperDto(saksnummer);
        var losBehandlingDto = fptilbakeKlient.hentLosBehandlingDto(behandlingUuid);

        return mapFraFpTilbake(losBehandlingDto, losFagsakEgenskaperDto);
    }

    private Behandling mapFraFpsak(LosBehandlingDto dto) {
        return dto;
    }

    private Behandling mapFraFpTilbake(LosBehandlingDto losBehandlingDto, LosFagsakEgenskaperDto losFagsakEgenskaperDto) {
        return null;
    }

}
