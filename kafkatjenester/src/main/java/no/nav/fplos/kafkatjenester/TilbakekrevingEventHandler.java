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
import java.util.UUID;

@ApplicationScoped
@Transaction
public class TilbakekrevingEventHandler extends FpEventHandler {
    private static final Logger log = LoggerFactory.getLogger(TilbakekrevingEventHandler.class);

    public TilbakekrevingEventHandler() {
    }

    @Inject
    public TilbakekrevingEventHandler(OppgaveRepository oppgaveRepository) {
        super(oppgaveRepository);
    }

    @Override
    public void prosesser(BehandlingProsessEventDto bpeDto){
        prosesser((TilbakebetalingBehandlingProsessEventDto)bpeDto, null,false);
    }

    public void prosesserFraAdmin(TilbakebetalingBehandlingProsessEventDto bpeDto, Reservasjon reservasjon){
        prosesser(bpeDto, reservasjon, true);
    }

    private void prosesser(TilbakebetalingBehandlingProsessEventDto bpeDto, Reservasjon reservasjon, boolean prosesserFraAdmin) {
        //TODO: bruk bpeDto.getId() når den er tilgjengelig
        UUID eksternId = UUID.randomUUID();//bpeDto.getId();
        //EksternIdentifikator eksternId = getEksternIdentifikatorRespository().finnEllerOpprettEksternId(bpeDto.getFagsystem(), eksternRefId);

        List<OppgaveEventLogg> pastOppgaveEvents = getOppgaveRepository().hentEventerForEksternId(eksternId);

        //TODO: Dersom annen info skal populeres i Oppgave (frister, status o.l.) eller OppgaveEgenskaper så behøver vi et restkall mot fptilbake
        //BehandlingFptilbake behandling = fptilbakeBehandlingRestKlient.getBehandling(eksternRefId);
        //TODO: Kall til fptilbake for aksjonspunkter
        List<Aksjonspunkt> aksjonspunkt = new ArrayList<>();

        //TODO: Behøver vi egen eventmapper for tilbakekreving?
        EventResultat event = prosesserFraAdmin
                ? FpsakEventMapper.signifikantEventForAdminFra(aksjonspunkt)
                :FpsakEventMapper.signifikantEventFra(aksjonspunkt, pastOppgaveEvents, bpeDto.getBehandlendeEnhet());

        switch (event) {
            case LUKK_OPPGAVE:
                // Dersom oppgaven som skal avsluttes ikke blir identifisert, vil man her ende opp med en
                // eksternreferanseidentifikator som ikke refereres i noen oppgave ettersom der er gjort kall til
                // finnEllerOpprettEksternId for eksternId. Det er dog kanskje ikke feil at denne iden blir opprettet
                // slik at man har et spor av hendelsen.
                log.info("Lukker oppgave med eksternRefId {} ", eksternId.toString());
                avsluttOppgaveForEksternId(eksternId);
                loggEvent(eksternId, OppgaveEventType.LUKKET, null, bpeDto.getBehandlendeEnhet(), null);
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(eksternId, pastOppgaveEvents, bpeDto.getBehandlendeEnhet());
                Oppgave oppgave = opprettTilbakekrevingOppgave(eksternId, bpeDto, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, oppgave.getId());
                log.info("Oppgave {} opprettet og populert med informasjon fra FPTILBAKE for eksternId {}", oppgave.getId(), eksternId);
                loggEvent(oppgave.getEksternId(), OppgaveEventType.OPPRETTET, null, bpeDto.getBehandlendeEnhet());
                //opprettOppgaveEgenskaper(behandling, oppgave);
                break;
            case GJENÅPNE_OPPGAVE:
                //Oppgave gjenåpnetOppgave = gjenåpneOppgaveForEksternId(bpeDto);
                Oppgave gjenåpnetOppgave = getOppgaveRepository().gjenåpneOppgaveForEksternId(eksternId);
                log.info("Gjenåpnet oppgave for eksternId {}", eksternId);
                loggEvent(gjenåpnetOppgave.getEksternId(), OppgaveEventType.GJENAPNET, null, bpeDto.getBehandlendeEnhet());
                //opprettOppgaveEgenskaper(behandling, gjenåpnetOppgave);
                break;
        }

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
                        .medBehandlingId(bpeDto.getBehandlingId())
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
    /*private Oppgave opprettOppgave(UUID eksternId, BehandlingProsessEventDto bpeDto, boolean prosesserFraAdmin) {
        return getOppgaveRepository().opprettOppgave(Oppgave.builder()
                .medSystem(bpeDto.getFagsystem())
                .medBehandlingId(bpeDto.getBehandlingId())
                .medFagsakSaksnummer(Long.valueOf(bpeDto.getSaksnummer()))
                .medAktorId(Long.valueOf(bpeDto.getAktørId()))
                .medBehandlendeEnhet(bpeDto.getBehandlendeEnhet())
                .medBehandlingType(BehandlingType.fraKode(bpeDto.getBehandlingTypeKode()))
                .medFagsakYtelseType(FagsakYtelseType.fraKode(bpeDto.getYtelseTypeKode()))
                .medAktiv(true).medBehandlingOpprettet(bpeDto.getOpprettetBehandling())
                //.medForsteStonadsdag(fraFpsak.getFørsteUttaksdag())
                .medUtfortFraAdmin(prosesserFraAdmin)
                //.medBehandlingsfrist(hentBehandlingstidFrist(fraFpsak.getBehandlingstidFrist()))
                //.medBehandlingStatus(getKodeverkRepository().finn(BehandlingStatus.class, fraFpsak.getStatus()))
                .medEksternId(eksternId)
                .build());
    }*/

}
