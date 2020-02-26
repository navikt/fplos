package no.nav.fplos.kafkatjenester;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.oppgave.TilbakekrevingOppgave;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.kafkatjenester.eventresultat.EventResultat;
import no.nav.fplos.kafkatjenester.eventresultat.FpsakEventMapper;
import no.nav.vedtak.felles.integrasjon.kafka.TilbakebetalingBehandlingProsessEventDto;

@ApplicationScoped
public class TilbakekrevingEventHåndterer implements EventHåndterer<TilbakebetalingBehandlingProsessEventDto> {

    private static final Logger LOG = LoggerFactory.getLogger(TilbakekrevingEventHåndterer.class);

    private OppgaveRepository oppgaveRepository;

    @Inject
    public TilbakekrevingEventHåndterer(OppgaveRepository oppgaveRepository) {
        this.oppgaveRepository = oppgaveRepository;
    }

    TilbakekrevingEventHåndterer() {
        //CDI
    }

    @Override
    public void håndterEvent(TilbakebetalingBehandlingProsessEventDto dto) {
        //TODO: bruk dto.getId() når den er tilgjengelig
        UUID eksternId = UUID.randomUUID();//dto.getId();
        //EksternIdentifikator eksternId = getEksternIdentifikatorRespository().finnEllerOpprettEksternId(dto.getFagsystem(), eksternRefId);

        List<OppgaveEventLogg> pastOppgaveEvents = oppgaveRepository.hentEventerForEksternId(eksternId);

        //TODO: Dersom annen info skal populeres i Oppgave (frister, status o.l.) eller OppgaveEgenskaper så behøver vi et restkall mot fptilbake
        //BehandlingFptilbake behandling = fptilbakeBehandlingRestKlient.getBehandling(eksternRefId);
        //TODO: Kall til fptilbake for aksjonspunkter
        List<Aksjonspunkt> aksjonspunkt = new ArrayList<>();

        //TODO: Behøver vi egen eventmapper for tilbakekreving?
        EventResultat event = FpsakEventMapper.signifikantEventFra(aksjonspunkt, pastOppgaveEvents, dto.getBehandlendeEnhet());

        switch (event) {
            case LUKK_OPPGAVE:
                // Dersom oppgaven som skal avsluttes ikke blir identifisert, vil man her ende opp med en
                // eksternreferanseidentifikator som ikke refereres i noen oppgave ettersom der er gjort kall til
                // finnEllerOpprettEksternId for eksternId. Det er dog kanskje ikke feil at denne iden blir opprettet
                // slik at man har et spor av hendelsen.
                LOG.info("Lukker oppgave med eksternRefId {} ", eksternId.toString());
                avsluttOppgaveForEksternId(eksternId);
                loggEvent(eksternId, OppgaveEventType.LUKKET, null, dto.getBehandlendeEnhet(), null);
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(eksternId, pastOppgaveEvents, dto.getBehandlendeEnhet());
                Oppgave oppgave = opprettTilbakekrevingOppgave(eksternId, dto);
                LOG.info("Oppgave {} opprettet og populert med informasjon fra FPTILBAKE for eksternId {}", oppgave.getId(), eksternId);
                loggEvent(oppgave.getEksternId(), OppgaveEventType.OPPRETTET, null, dto.getBehandlendeEnhet());
                //opprettOppgaveEgenskaper(behandling, oppgave);
                break;
            case GJENÅPNE_OPPGAVE:
                //Oppgave gjenåpnetOppgave = gjenåpneOppgaveForEksternId(dto);
                Oppgave gjenåpnetOppgave = oppgaveRepository.gjenåpneOppgaveForEksternId(eksternId);
                LOG.info("Gjenåpnet oppgave for eksternId {}", eksternId);
                loggEvent(gjenåpnetOppgave.getEksternId(), OppgaveEventType.GJENAPNET, null, dto.getBehandlendeEnhet());
                //opprettOppgaveEgenskaper(behandling, gjenåpnetOppgave);
                break;
        }

    }

    private void avsluttOppgaveHvisÅpen(UUID eksternId, List<OppgaveEventLogg> oppgaveEventLogger, String behandlendeEnhet) {
        if (!oppgaveEventLogger.isEmpty() && oppgaveEventLogger.get(0).getEventType().erÅpningsevent()) {
            loggEvent(eksternId, OppgaveEventType.LUKKET, null, behandlendeEnhet);
            oppgaveRepository.avsluttOppgaveForEksternId(eksternId);
        }
    }

    private TilbakekrevingOppgave opprettTilbakekrevingOppgave(UUID eksternId, TilbakebetalingBehandlingProsessEventDto dto) {
        return oppgaveRepository.opprettTilbakekrevingEgenskaper(TilbakekrevingOppgave.tbuilder()
                .medBelop(dto.getFeilutbetaltBeløp())
                .medSystem(dto.getFagsystem().name())
                .medFagsakSaksnummer(Long.valueOf(dto.getSaksnummer()))
                .medAktorId(Long.valueOf(dto.getAktørId()))
                .medBehandlendeEnhet(dto.getBehandlendeEnhet())
                .medBehandlingType(BehandlingType.fraKode(dto.getBehandlingTypeKode()))
                .medFagsakYtelseType(FagsakYtelseType.fraKode(dto.getYtelseTypeKode()))
                .medAktiv(true)
                .medBehandlingOpprettet(dto.getOpprettetBehandling())
                .medUtfortFraAdmin(false)
                .medEksternId(eksternId)
                .build());
    }

    private void avsluttOppgaveForEksternId(UUID externId) {
        oppgaveRepository.avsluttOppgaveForEksternId(externId);
    }

    private void loggEvent(UUID eksternId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet) {
        oppgaveRepository.lagre(new OppgaveEventLogg(eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet));
    }

    protected void loggEvent(UUID eksternId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet, LocalDateTime frist) {
        oppgaveRepository.lagre(new OppgaveEventLogg(eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet, frist));
    }
}
