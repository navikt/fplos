package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.EksternIdentifikator;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.kafkatjenester.eventresultat.EventResultat;
import no.nav.fplos.kafkatjenester.eventresultat.FpsakEventMapper;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import no.nav.vedtak.felles.jpa.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Transaction
public class TilbakekrevingEventHandler extends FpEventHandler {
    private static final Logger log = LoggerFactory.getLogger(TilbakekrevingEventHandler.class);

    public TilbakekrevingEventHandler() {
    }

    @Inject
    public TilbakekrevingEventHandler(OppgaveRepositoryProvider oppgaveRepositoryProvider) {
        super(oppgaveRepositoryProvider);
    }

    @Override
    public void prosesser(BehandlingProsessEventDto bpeDto){
        prosesser(bpeDto, null,false);
    }

    private void prosesser(BehandlingProsessEventDto bpeDto, Reservasjon reservasjon, boolean prosesserFraAdmin) {
        //TODO: bruk bpeDto.getId() når den er tilgjengelig
        String eksternRefId = "EKSTERN";//bpeDto.getId();
        EksternIdentifikator eksternId = getEksternIdentifikatorRespository().finnEllerOpprettEksternId(bpeDto.getFagsystem(), eksternRefId);

        List<OppgaveEventLogg> pastOppgaveEvents = getOppgaveRepository().hentEventerForEksternId(eksternId.getId());

        //TODO: Kall til tilbakebetalingtjeneste for aksjonspunkter
        List<Aksjonspunkt> aksjonspunkt = new ArrayList<>();

        //TODO: Behøver vi egen eventmapper for tilbakekreving?
        EventResultat event = //prosesserFraAdmin
                //? FpsakEventMapper.signifikantEventForAdminFra(aksjonspunkt)
                //:
        FpsakEventMapper.signifikantEventFra(aksjonspunkt, pastOppgaveEvents, bpeDto.getBehandlendeEnhet());

        switch (event) {
            case LUKK_OPPGAVE:
                log.info("Lukker oppgave med eksternRefId {} ", eksternRefId);
                avsluttOppgaveForEksternId(eksternId.getId());
                loggEvent(eksternId.getId(), OppgaveEventType.LUKKET, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet(), null);
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(eksternId.getId(), pastOppgaveEvents, bpeDto.getBehandlendeEnhet());
                Oppgave oppgave = opprettOppgave(eksternId.getId(), bpeDto, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, oppgave);
                log.info("Oppgave {} opprettet og populert med informasjon fra FPTILBAKE for ekstern id {}", oppgave.getId(), eksternId);
                loggEvent(oppgave.getEksternId(), OppgaveEventType.OPPRETTET, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet());
                //opprettOppgaveEgenskaper(behandling, oppgave);
                break;
        }

    }

    private void avsluttOppgaveHvisÅpen(Long eksternId, List<OppgaveEventLogg> oppgaveEventLogger, String behandlendeEnhet) {
        if (!oppgaveEventLogger.isEmpty() && OppgaveEventType.åpningseventtyper().contains(oppgaveEventLogger.get(0).getEventType())){
            loggEvent(eksternId, OppgaveEventType.LUKKET, AndreKriterierType.UKJENT, behandlendeEnhet);
            getOppgaveRepository().avsluttOppgaveForEksternId(eksternId);
        }
    }
    private Oppgave opprettOppgave(Long eksternId, BehandlingProsessEventDto bpeDto, boolean prosesserFraAdmin) {
        return getOppgaveRepository().opprettOppgave(Oppgave.builder()
                .medSystem(bpeDto.getFagsystem())
                .medBehandlingId(bpeDto.getBehandlingId())
                .medFagsakSaksnummer(Long.valueOf(bpeDto.getSaksnummer()))
                .medAktorId(Long.valueOf(bpeDto.getAktørId()))
                .medBehandlendeEnhet(bpeDto.getBehandlendeEnhet())
                .medBehandlingType(getKodeverkRepository().finn(BehandlingType.class, bpeDto.getBehandlingTypeKode()))
                .medFagsakYtelseType(getKodeverkRepository().finn(FagsakYtelseType.class, bpeDto.getYtelseTypeKode()))
                .medAktiv(true).medBehandlingOpprettet(bpeDto.getOpprettetBehandling())
                //.medForsteStonadsdag(fraFpsak.getFørsteUttaksdag())
                .medUtfortFraAdmin(prosesserFraAdmin)
                //.medBehandlingsfrist(hentBehandlingstidFrist(fraFpsak.getBehandlingstidFrist()))
                //.medBehandlingStatus(getKodeverkRepository().finn(BehandlingStatus.class, fraFpsak.getStatus()))
                .medEksternId(eksternId)
                .build());
    }

}
