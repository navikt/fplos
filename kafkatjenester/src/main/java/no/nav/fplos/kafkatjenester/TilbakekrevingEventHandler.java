package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
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
import java.util.UUID;

import static no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType.*;

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

    private void prosesser(TilbakebetalingBehandlingProsessEventDto dto, Reservasjon reservasjon, boolean prosesserFraAdmin) {
        var id = dto.getEksternId();
        OppgaveHistorikk oppgaveHistorikk = new OppgaveHistorikk(getOppgaveRepository().hentOppgaveEventer(dto.getEksternId()));
        OppgaveEgenskapFinner egenskapFinner = new TilbakekrevingOppgaveEgenskapFinner(dto.getAksjonspunktKoderMedStatusListe(),
                dto.getAnsvarligSaksbehandlerIdent());
        EventResultat event = eventFra(dto, oppgaveHistorikk, egenskapFinner);

        switch (event) {
            case LUKK_OPPGAVE_MANUELT_VENT:
                log.info("Lukker oppgave, satt manuelt på vent.");
                avsluttOppgaveForEksternId(id);
                loggEvent(id, OppgaveEventType.MANU_VENT, null, dto.getBehandlendeEnhet(), null);
                break;
            case LUKK_OPPGAVE:
                log.info("Lukker oppgave med eksternRefId {}.", id.toString());
                avsluttOppgaveForEksternId(id);
                loggEvent(id, OppgaveEventType.LUKKET, null, dto.getBehandlendeEnhet(), null);
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(id, oppgaveHistorikk, dto.getBehandlendeEnhet());
                Oppgave oppgave = opprettTilbakekrevingOppgave(dto, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, oppgave.getId());
                log.info("Oppretter tilbakekrevingsoppgave {} for eksternId {}.", oppgave.getId(), id);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
                loggEvent(oppgave.getEksternId(), OppgaveEventType.OPPRETTET, null, dto.getBehandlendeEnhet());
                break;
            case OPPRETT_BESLUTTER_OPPGAVE:
                avsluttOppgaveHvisÅpen(id, oppgaveHistorikk, dto.getBehandlendeEnhet());
                Oppgave beslutterOppgave = opprettTilbakekrevingOppgave(dto, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, beslutterOppgave.getId());
                log.info("Oppretter beslutteroppgave.");
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(beslutterOppgave, egenskapFinner);
                loggEvent(id, OppgaveEventType.OPPRETTET, TIL_BESLUTTER, dto.getBehandlendeEnhet());
                break;
            case GJENÅPNE_OPPGAVE:
                TilbakekrevingOppgave gjenåpnetOppgave = getOppgaveRepository().gjenåpneTilbakekrevingOppgave(id);
                oppdaterOppgaveInformasjon(gjenåpnetOppgave, dto);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(gjenåpnetOppgave, egenskapFinner);
                log.info("Gjenåpner oppgave for eksternId {}.", id);
                loggEvent(gjenåpnetOppgave.getEksternId(), OppgaveEventType.GJENAPNET, null, dto.getBehandlendeEnhet());
                break;
        }
    }

    private EventResultat eventFra(TilbakebetalingBehandlingProsessEventDto bpeDto, OppgaveHistorikk oppgaveHistorikk,
                                   OppgaveEgenskapFinner egenskaper) {
        EventResultat event = TilbakekrevingEventMapper.tilbakekrevingEventFra(bpeDto);
        OppgaveEventLogg sisteOppgaveEvent = oppgaveHistorikk.getSisteÅpningsEvent();
        boolean erTilBeslutter = egenskaper.getAndreKriterier().contains(TIL_BESLUTTER);

        if (event == EventResultat.OPPRETT_OPPGAVE && sisteOppgaveEvent != null && sisteOppgaveEvent.getEventType().erÅpningsevent()) {
            if (erTilBeslutter && sisteOppgaveEvent.getAndreKriterierType() != TIL_BESLUTTER) {
                return EventResultat.OPPRETT_BESLUTTER_OPPGAVE;
            } else if (erTilBeslutter && sisteOppgaveEvent.getAndreKriterierType() == TIL_BESLUTTER) {
                return EventResultat.GJENÅPNE_OPPGAVE;
            } else if (!erTilBeslutter && sisteOppgaveEvent.getAndreKriterierType() == TIL_BESLUTTER) {
                return EventResultat.OPPRETT_OPPGAVE;
            } else if (sisteOppgaveEvent.getBehandlendeEnhet().equals(bpeDto.getBehandlendeEnhet())) {
                return EventResultat.GJENÅPNE_OPPGAVE;
            }
        }
        return event;
    }

    private void avsluttOppgaveHvisÅpen(UUID eksternId, OppgaveHistorikk oppgaveHistorikk, String behandlendeEnhet) {
        if (oppgaveHistorikk.erSisteEventÅpningsevent()) {
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

    private TilbakekrevingOppgave opprettTilbakekrevingOppgave(TilbakebetalingBehandlingProsessEventDto dto, boolean prosesserFraAdmin) {
        return getOppgaveRepository().opprettTilbakekrevingEgenskaper(oppgaveFra(dto, prosesserFraAdmin));
    }

    private TilbakekrevingOppgave oppgaveFra(TilbakebetalingBehandlingProsessEventDto dto, boolean prosesserFraAdmin) {
        return TilbakekrevingOppgave.tbuilder()
                .medBelop(dto.getFeilutbetaltBeløp())
                .medFeilutbetalingStart(dto.getFørsteFeilutbetaling().atStartOfDay())
                .medHref(dto.getHref())
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
