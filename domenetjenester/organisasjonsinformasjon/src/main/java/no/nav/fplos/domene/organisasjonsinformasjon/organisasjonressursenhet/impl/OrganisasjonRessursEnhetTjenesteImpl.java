package no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet.impl;

import no.nav.foreldrepenger.loslager.aktør.OrganisasjonsEnhet;
import no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet.OrganisasjonRessursEnhetTjeneste;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.HentEnhetListeRessursIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.HentEnhetListeUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.informasjon.WSEnhet;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.meldinger.WSHentEnhetListeRequest;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.meldinger.WSHentEnhetListeResponse;
import no.nav.vedtak.felles.integrasjon.organisasjonressursenhet.klient.OrganisasjonRessursEnhetConsumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrganisasjonRessursEnhetTjenesteImpl implements OrganisasjonRessursEnhetTjeneste {

    private static final String TJENESTE = "OrganisasjonRessursEnhet";

    private OrganisasjonRessursEnhetConsumer organisasjonRessursEnhetConsumer;

    public OrganisasjonRessursEnhetTjenesteImpl() {
        // CDI
    }

    @Inject
    public OrganisasjonRessursEnhetTjenesteImpl(OrganisasjonRessursEnhetConsumer organisasjonRessursEnhetConsumer) {
        this.organisasjonRessursEnhetConsumer = organisasjonRessursEnhetConsumer;
    }

    @Override
    public List<OrganisasjonsEnhet> hentEnhetListe(String ressursId) {
        Optional<WSHentEnhetListeResponse> response = hentEnhetForRessursIdListe(ressursId);
        return mapEnhetListeToOrganisasjonsEnhet(response);
    }

    private Optional<WSHentEnhetListeResponse> hentEnhetForRessursIdListe(String ressursId) {
        WSHentEnhetListeRequest request = new WSHentEnhetListeRequest();
        request.setRessursId(ressursId);
        try {
            return Optional.of(organisasjonRessursEnhetConsumer.hentEnhetListe(request));
        } catch (HentEnhetListeUgyldigInput e) {
            OrganisasjonRessursEnhetTjenesteFeil.FACTORY.ugyldigInput(TJENESTE, e);
        } catch (HentEnhetListeRessursIkkeFunnet e) {
            OrganisasjonRessursEnhetTjenesteFeil.FACTORY.ikkeFunnet(TJENESTE, e);
        } catch (Exception e) {
            OrganisasjonRessursEnhetTjenesteFeil.FACTORY.feil(TJENESTE, e);
        }
        return Optional.empty();
    }

    private List<OrganisasjonsEnhet> mapEnhetListeToOrganisasjonsEnhet(Optional<WSHentEnhetListeResponse> response) {
        List<OrganisasjonsEnhet> alleEnheter = new ArrayList<>();

        if(response.isPresent()) {
            WSHentEnhetListeResponse enhetListeFraResponse = response.get();
            alleEnheter.addAll(enhetListeFraResponse.getEnhetListe().stream()
                    .map(this::mapEnhetTilOrganisasjonsEnhet)
                    .collect(Collectors.toList()));
        }

        return alleEnheter;
    }

    private OrganisasjonsEnhet mapEnhetTilOrganisasjonsEnhet (WSEnhet enhet) {
        return new OrganisasjonsEnhet(enhet.getEnhetId(), enhet.getNavn());
    }
}
