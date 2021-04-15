package no.nav.foreldrepenger.los.web.server.jetty;

import static no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType.RESOURCE_FORELDREPENGER_SAK_AKSJONSPUNKT_TYPE;
import static no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType.RESOURCE_FORELDREPENGER_SAK_ANSVARLIG_SAKSBEHANDLER;
import static no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType.RESOURCE_FORELDREPENGER_SAK_BEHANDLINGSSTATUS;
import static no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType.RESOURCE_FORELDREPENGER_SAK_SAKSSTATUS;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_DOMENE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_RESOURCE_TYPE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.XACML10_ACTION_ACTION_ID;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;

import no.nav.vedtak.sikkerhet.abac.PdpRequest;
import no.nav.vedtak.sikkerhet.pdp.XacmlRequestBuilderTjeneste;
import no.nav.vedtak.sikkerhet.pdp.xacml.XacmlAttributeSet;
import no.nav.vedtak.sikkerhet.pdp.xacml.XacmlRequestBuilder;

@Dependent
@Alternative
@Priority(2)
public class XacmlRequestBuilderTjenesteImpl implements XacmlRequestBuilderTjeneste {

    public XacmlRequestBuilderTjenesteImpl() {
    }

    @Override
    public XacmlRequestBuilder lagXacmlRequestBuilder(PdpRequest pdpRequest) {
        var xacmlBuilder = new XacmlRequestBuilder();

        var actionAttributeSet = new XacmlAttributeSet();
        actionAttributeSet.addAttribute(XACML10_ACTION_ACTION_ID, pdpRequest.getString(XACML10_ACTION_ACTION_ID));
        xacmlBuilder.addActionAttributeSet(actionAttributeSet);

        var aktørIds = pdpRequest.getListOfString(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE);

        if (aktørIds.isEmpty()) {
            populerResources(xacmlBuilder, pdpRequest, null);
        } else {
            for (var aktørId : aktørIds) {
                populerResources(xacmlBuilder, pdpRequest, aktørId);
            }
        }

        return xacmlBuilder;
    }

    private void populerResources(XacmlRequestBuilder xacmlBuilder, PdpRequest pdpRequest, String aktørId) {
        var aksjonspunktTyper = pdpRequest.getListOfString(RESOURCE_FORELDREPENGER_SAK_AKSJONSPUNKT_TYPE);
        if (aksjonspunktTyper.isEmpty()) {
            xacmlBuilder.addResourceAttributeSet(byggRessursAttributter(pdpRequest, aktørId, null));
        } else {
            for (var aksjonspunktType : aksjonspunktTyper) {
                xacmlBuilder.addResourceAttributeSet(byggRessursAttributter(pdpRequest, aktørId, aksjonspunktType));
            }
        }
    }

    private XacmlAttributeSet byggRessursAttributter(PdpRequest pdpRequest, String aktørId, String aksjonsounktType) {
        var resourceAttributeSet = new XacmlAttributeSet();
        resourceAttributeSet.addAttribute(RESOURCE_FELLES_DOMENE, pdpRequest.getString(RESOURCE_FELLES_DOMENE));
        resourceAttributeSet.addAttribute(RESOURCE_FELLES_RESOURCE_TYPE,
                pdpRequest.getString(RESOURCE_FELLES_RESOURCE_TYPE));
        setOptionalValueinAttributeSet(resourceAttributeSet, pdpRequest, RESOURCE_FORELDREPENGER_SAK_SAKSSTATUS);
        setOptionalValueinAttributeSet(resourceAttributeSet, pdpRequest, RESOURCE_FORELDREPENGER_SAK_BEHANDLINGSSTATUS);
        setOptionalValueinAttributeSet(resourceAttributeSet, pdpRequest,
                RESOURCE_FORELDREPENGER_SAK_ANSVARLIG_SAKSBEHANDLER);
        if (aktørId != null) {
            resourceAttributeSet.addAttribute(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, aktørId);
        }
        if (aksjonsounktType != null) {
            resourceAttributeSet.addAttribute(RESOURCE_FORELDREPENGER_SAK_AKSJONSPUNKT_TYPE, aksjonsounktType);
        }

        return resourceAttributeSet;
    }

    private void setOptionalValueinAttributeSet(XacmlAttributeSet resourceAttributeSet,
                                                PdpRequest pdpRequest,
                                                String key) {
        pdpRequest.getOptional(key).ifPresent(s -> resourceAttributeSet.addAttribute(key, s));
    }
}
