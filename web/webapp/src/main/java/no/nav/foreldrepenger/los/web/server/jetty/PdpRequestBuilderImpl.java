package no.nav.foreldrepenger.los.web.server.jetty;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import no.nav.abac.common.xacml.CommonAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling;
import no.nav.vedtak.sikkerhet.abac.PdpKlient;
import no.nav.vedtak.sikkerhet.abac.PdpRequest;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;

import java.util.ArrayList;

/**
 * Implementasjon av PDP request for denne applikasjonen.
 */
@ApplicationScoped
@Alternative
@Priority(2)
// HACK - FORDI DENNE KOLLIDERER MED DUMMYREQUESTBUILDER FRA FELLES-SIKKERHET-TESTUTILITIES NÅR VI KJØRER JETTY
public class PdpRequestBuilderImpl implements PdpRequestBuilder {

    public static final String ABAC_DOMAIN = "foreldrepenger";

    @Override
    public PdpRequest lagPdpRequest(AbacAttributtSamling attributter) {
        PdpRequest pdpRequest = new PdpRequest();
        pdpRequest.put(CommonAttributter.SUBJECT_FELLES_ENHETIDLISTE, new ArrayList<>(attributter.getVerdier(FplosAbacAttributtType.OPPGAVESTYRING_ENHET)));
        pdpRequest.put(CommonAttributter.RESOURCE_FELLES_DOMENE, ABAC_DOMAIN);
        pdpRequest.put(PdpKlient.ENVIRONMENT_AUTH_TOKEN, attributter.getIdToken());
        pdpRequest.put(CommonAttributter.XACML_1_0_ACTION_ACTION_ID, attributter.getActionType().getEksternKode());
        pdpRequest.put(CommonAttributter.RESOURCE_FELLES_RESOURCE_TYPE, attributter.getResource().getEksternKode());
        return pdpRequest;
    }

}