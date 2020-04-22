package no.nav.foreldrepenger.los.web.server.jetty;

import static no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType.SUBJECT_FELLES_ENHETIDLISTE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_DOMENE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_RESOURCE_TYPE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.XACML10_ACTION_ACTION_ID;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling;
import no.nav.vedtak.sikkerhet.abac.PdpKlient;
import no.nav.vedtak.sikkerhet.abac.PdpRequest;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

@Dependent
@Alternative
@Priority(2)
public class PdpRequestBuilderImpl implements PdpRequestBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdpRequestBuilderImpl.class);

    public static final String ABAC_DOMAIN = "foreldrepenger";

    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient;
    private OppgaveTjeneste oppgaveTjeneste;

    @Inject
    public PdpRequestBuilderImpl(ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient,
                                 OppgaveTjeneste oppgaveTjeneste) {
        this.foreldrepengerBehandlingRestKlient = foreldrepengerBehandlingRestKlient;
        this.oppgaveTjeneste = oppgaveTjeneste;
    }

    @Override
    public PdpRequest lagPdpRequest(AbacAttributtSamling attributter) {
        PdpRequest pdpRequest = new PdpRequest();
        pdpRequest.put(SUBJECT_FELLES_ENHETIDLISTE, new ArrayList<>(attributter.getVerdier(FplosAbacAttributtType.OPPGAVESTYRING_ENHET)));
        pdpRequest.put(RESOURCE_FELLES_DOMENE, ABAC_DOMAIN);
        pdpRequest.put(PdpKlient.ENVIRONMENT_AUTH_TOKEN, attributter.getIdToken());
        pdpRequest.put(XACML10_ACTION_ACTION_ID, attributter.getActionType().getEksternKode());
        pdpRequest.put(RESOURCE_FELLES_RESOURCE_TYPE, attributter.getResource());

        Set<Long> oppgaveIdList = attributter.getVerdier(FplosAbacAttributtType.OPPGAVE_ID);
        if (oppgaveIdList.size() > 0) {
            var oppgave = oppgaveTjeneste.hentOppgave(oppgaveIdList.iterator().next());
            var behandlingId = oppgave.getBehandlingId();
            leggTilAttributterForBehandling(pdpRequest, behandlingId);
        }
        Set<UUID> behandlingIdList = attributter.getVerdier(StandardAbacAttributtType.BEHANDLING_UUID);
        if (behandlingIdList.size() > 0) {
            var behandlingId = new BehandlingId(behandlingIdList.iterator().next());
            leggTilAttributterForBehandling(pdpRequest, behandlingId);
        }

        return pdpRequest;
    }

    private void leggTilAttributterForBehandling(PdpRequest pdpRequest, BehandlingId behandlingId) {
        LOGGER.info("Legger til abac attributter for behandling " + behandlingId);

        var dto = foreldrepengerBehandlingRestKlient.hentPipdataForBehandling(behandlingId);
        pdpRequest.put(FplosAbacAttributtType.RESOURCE_FORELDREPENGER_SAK_SAKSSTATUS, dto.getFagsakStatus());
        pdpRequest.put(FplosAbacAttributtType.RESOURCE_FORELDREPENGER_SAK_BEHANDLINGSSTATUS, dto.getBehandlingStatus());
        pdpRequest.put(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, dto.getAktørIder());
    }

}
