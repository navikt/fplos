package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.oppgave.TilbakekrevingOppgave;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.fplos.kafkatjenester.eventresultat.EventResultat;
import no.nav.fplos.kafkatjenester.eventresultat.TilbakekrevingEventMapper;
import no.nav.vedtak.felles.integrasjon.kafka.TilbakebetalingBehandlingProsessEventDto;
import no.nav.vedtak.felles.jpa.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Transaction
public class TilbakekrevingEventHandler extends FpEventHandler<TilbakebetalingBehandlingProsessEventDto> {
    private static final Logger log = LoggerFactory.getLogger(TilbakekrevingEventHandler.class);
    private OppgaveEgenskapHandler oppgaveEgenskapHandler;

    public TilbakekrevingEventHandler() {
    }

    @Inject
    public TilbakekrevingEventHandler(OppgaveRepository oppgaveRepository, OppgaveEgenskapHandler oppgaveEgenskapHandler) {
        super(oppgaveRepository);
        this.oppgaveEgenskapHandler = oppgaveEgenskapHandler;
    }

    @Override
    public void prosesser(TilbakebetalingBehandlingProsessEventDto bpeDto){
        prosesser(bpeDto, null,false);
    }

    public void prosesserFraAdmin(TilbakebetalingBehandlingProsessEventDto bpeDto, Reservasjon reservasjon){
        prosesser(bpeDto, reservasjon, true);
    }

    private void prosesser(TilbakebetalingBehandlingProsessEventDto bpeDto, Reservasjon reservasjon, boolean prosesserFraAdmin) {
        var id = bpeDto.getEksternId();
        List<OppgaveEventLogg> oppgaveEvents = getOppgaveRepository().hentEventerForEksternId(bpeDto.getEksternId());
        OppgaveEgenskapFinner egenskapFinner = new TilbakekrevingOppgaveEgenskapFinner(bpeDto.getAksjonspunktKoderMedStatusListe(),
                bpeDto.getAnsvarligSaksbehandlerIdent());

        EventResultat event = TilbakekrevingEventMapper.tilbakekrevingEventFra(bpeDto);
        if (event == EventResultat.OPPRETT_OPPGAVE
                && !oppgaveEvents.isEmpty()
                && oppgaveEvents.get(0).getEventType().erÅpningsevent()) {
            event = EventResultat.GJENÅPNE_OPPGAVE;
        }

        switch (event) {
            case LUKK_OPPGAVE_MANUELT_VENT:
                log.info("Lukker oppgave, satt manuelt på vent");
                avsluttOppgaveForEksternId(id);
                loggEvent(id, OppgaveEventType.MANU_VENT, null, bpeDto.getBehandlendeEnhet(), null);
                break;
            case LUKK_OPPGAVE:
                log.info("Lukker oppgave med eksternRefId {} ", id.toString());
                avsluttOppgaveForEksternId(id);
                loggEvent(id, OppgaveEventType.LUKKET, null, bpeDto.getBehandlendeEnhet(), null);
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(id, oppgaveEvents, bpeDto.getBehandlendeEnhet());
                Oppgave oppgave = opprettTilbakekrevingOppgave(id, bpeDto, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, oppgave.getId());
                log.info("Oppgave {} opprettet og populert med informasjon fra FPTILBAKE for eksternId {}", oppgave.getId(), id);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
                loggEvent(oppgave.getEksternId(), OppgaveEventType.OPPRETTET, null, bpeDto.getBehandlendeEnhet());
                break;
            case GJENÅPNE_OPPGAVE:
                TilbakekrevingOppgave gjenåpnetOppgave = getOppgaveRepository().gjenåpneTilbakekrevingOppgave(id);
                oppdaterOppgaveInformasjon(gjenåpnetOppgave, bpeDto);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(gjenåpnetOppgave, egenskapFinner);
                log.info("Gjenåpnet oppgave for eksternId {}", id);
                loggEvent(gjenåpnetOppgave.getEksternId(), OppgaveEventType.GJENAPNET, null, bpeDto.getBehandlendeEnhet());
                break;
        }
    }

    private static EventResultat eventFra(TilbakebetalingBehandlingProsessEventDto bpeDto) {
        return bpeDto.getAksjonspunktKoderMedStatusListe().containsValue("OPPR")
                ? EventResultat.OPPRETT_OPPGAVE
                : EventResultat.LUKK_OPPGAVE;
    }

    private void avsluttOppgaveHvisÅpen(UUID eksternId, List<OppgaveEventLogg> oppgaveEventLogger, String behandlendeEnhet) {
        if (!oppgaveEventLogger.isEmpty() && oppgaveEventLogger.get(0).getEventType().erÅpningsevent()) {
            loggEvent(eksternId, OppgaveEventType.LUKKET, null, behandlendeEnhet);
            getOppgaveRepository().avsluttOppgaveForEksternId(eksternId);
        }
    }

    private void oppdaterOppgaveInformasjon(TilbakekrevingOppgave gjenåpnetOppgave, TilbakebetalingBehandlingProsessEventDto bpeDto) {
        TilbakekrevingOppgave tmp = oppgaveFra(bpeDto, false);
        gjenåpnetOppgave.avstemMed(tmp);
        tmp = null;
        getOppgaveRepository().lagre(gjenåpnetOppgave);
    }

    private TilbakekrevingOppgave opprettTilbakekrevingOppgave(UUID eksternId, TilbakebetalingBehandlingProsessEventDto bpeDto, boolean prosesserFraAdmin) {
        return getOppgaveRepository().opprettTilbakekrevingEgenskaper(oppgaveFra(bpeDto, prosesserFraAdmin));
    }

    private TilbakekrevingOppgave oppgaveFra(TilbakebetalingBehandlingProsessEventDto dto, boolean prosesserFraAdmin) {
        return TilbakekrevingOppgave.tbuilder()
                .medBelop(dto.getFeilutbetaltBeløp())
                .medFeilutbetalingStart(dto.getFørsteFeilutbetaling().atStartOfDay())
                .medSystem(dto.getFagsystem().name())
                .medFagsakSaksnummer(Long.valueOf(dto.getSaksnummer()))
                .medAktorId(Long.valueOf(dto.getAktørId()))
                .medBehandlendeEnhet(dto.getBehandlendeEnhet())
                .medBehandlingType(BehandlingType.fraKode(dto.getBehandlingTypeKode()))
                .medFagsakYtelseType(FagsakYtelseType.fraKode(dto.getYtelseTypeKode()))
                .medAktiv(true)
                .medBehandlingOpprettet(dto.getOpprettetBehandling())
                .medUtfortFraAdmin(prosesserFraAdmin)
                .medEksternId(dto.getEksternId())
                .build();
    }
}
