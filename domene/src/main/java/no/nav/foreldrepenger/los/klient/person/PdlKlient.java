package no.nav.foreldrepenger.los.klient.person;

import no.nav.vedtak.felles.integrasjon.person.AbstractPersonKlient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@RestClientConfig(tokenConfig = TokenFlow.ADAPTIVE_ADD_CONSUMER, endpointProperty = "pdl.base.url", endpointDefault = "http://pdl-api.pdl/graphql",
    scopesProperty = "pdl.scopes", scopesDefault = "api://prod-fss.pdl.pdl-api/.default")
public class PdlKlient extends AbstractPersonKlient {

    public PdlKlient() {
        super();
    }
}
