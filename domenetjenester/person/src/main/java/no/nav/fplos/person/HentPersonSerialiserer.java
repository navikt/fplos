package no.nav.fplos.person;

import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

import javax.xml.bind.JAXBException;

class HentPersonSerialiserer {

    private HentPersonSerialiserer() {
        throw new IllegalAccessError("Skal ikke instansieres");
    }

    static String serialiserKjerneinformasjon(HentPersonResponse kjerneinformasjon) {
        try {
            final no.nav.tjeneste.virksomhet.person.v3.HentPersonResponse response = new no.nav.tjeneste.virksomhet.person.v3.HentPersonResponse();
            response.setResponse(kjerneinformasjon);

            return JaxbHelper.marshalJaxb(no.nav.tjeneste.virksomhet.person.v3.HentPersonResponse.class, response);
        } catch (JAXBException e) {
            throw FeilFactory.create(Feilene.class).kunneIkkeSerialisereHentPersonResponse(e).toException();
        }
    }

    interface Feilene extends DeklarerteFeil {
        @TekniskFeil(feilkode = "FP-630159", feilmelding = "Kunne ikke serialisere HentPersonResponse", logLevel = LogLevel.ERROR)
        Feil kunneIkkeSerialisereHentPersonResponse(Exception cause);
    }
}
