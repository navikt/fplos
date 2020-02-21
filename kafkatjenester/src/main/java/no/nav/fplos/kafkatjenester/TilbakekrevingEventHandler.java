package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.oppgave.TilbakekrevingOppgave;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.kafkatjenester.eventresultat.EventResultat;
import no.nav.fplos.kafkatjenester.eventresultat.FpsakEventMapper;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import no.nav.vedtak.felles.integrasjon.kafka.TilbakebetalingBehandlingProsessEventDto;
import no.nav.vedtak.felles.jpa.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@Transaction
public class TilbakekrevingEventHandler extends FpEventHandler<TilbakebetalingBehandlingProsessEventDto> {
    private static final Logger log = LoggerFactory.getLogger(TilbakekrevingEventHandler.class);

    public TilbakekrevingEventHandler() {
    }

    @Inject
    public TilbakekrevingEventHandler(OppgaveRepository oppgaveRepository) {
        super(oppgaveRepository);
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

        EventResultat event = eventFra(bpeDto);
        if (event == EventResultat.OPPRETT_OPPGAVE
                && !oppgaveEvents.isEmpty()
                && oppgaveEvents.get(0).getEventType().erÅpningsevent()) {
            event = EventResultat.GJENÅPNE_OPPGAVE;
        }

        switch (event) {
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
                loggEvent(oppgave.getEksternId(), OppgaveEventType.OPPRETTET, null, bpeDto.getBehandlendeEnhet());
                break;
            case GJENÅPNE_OPPGAVE:
                Oppgave gjenåpnetOppgave = getOppgaveRepository().gjenåpneOppgaveForEksternId(id);
                log.info("Gjenåpnet oppgave for eksternId {}", id);
                loggEvent(gjenåpnetOppgave.getEksternId(), OppgaveEventType.GJENAPNET, null, bpeDto.getBehandlendeEnhet());
                break;
        }
    }

    private EventResultat eventFra(TilbakebetalingBehandlingProsessEventDto bpeDto) {
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

    private TilbakekrevingOppgave opprettTilbakekrevingOppgave(UUID eksternId, TilbakebetalingBehandlingProsessEventDto bpeDto, boolean prosesserFraAdmin) {
        TilbakekrevingOppgave oppgave =
                getOppgaveRepository().opprettTilbakekrevingEgenskaper(TilbakekrevingOppgave.tbuilder()
                        .medBelop(bpeDto.getFeilutbetaltBeløp())
                        .medSystem(bpeDto.getFagsystem().name())
                        .medFagsakSaksnummer(Long.valueOf(bpeDto.getSaksnummer()))
                        .medAktorId(Long.valueOf(bpeDto.getAktørId()))
                        .medBehandlendeEnhet(bpeDto.getBehandlendeEnhet())
                        .medBehandlingType(BehandlingType.fraKode(bpeDto.getBehandlingTypeKode()))
                        .medFagsakYtelseType(FagsakYtelseType.fraKode(bpeDto.getYtelseTypeKode()))
                        .medAktiv(true).medBehandlingOpprettet(bpeDto.getOpprettetBehandling())
                        .medUtfortFraAdmin(prosesserFraAdmin)
                        .medEksternId(eksternId)
                        .build());
        return oppgave;
    }
}
