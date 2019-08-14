package no.nav.vedtak.felles.integrasjon.organisasjonressursenhet.klient;

import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.HentEnhetListeRessursIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.HentEnhetListeUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.meldinger.WSHentEnhetListeRequest;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.meldinger.WSHentEnhetListeResponse;


public interface OrganisasjonRessursEnhetConsumer {

    WSHentEnhetListeResponse hentEnhetListe(WSHentEnhetListeRequest request)
            throws HentEnhetListeUgyldigInput, HentEnhetListeRessursIkkeFunnet;

}
