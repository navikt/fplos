package no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet.impl;

import no.nav.foreldrepenger.loslager.aktør.OrganisasjonsEnhet;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet.OrganisasjonRessursEnhetTjeneste;
import no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet.SaksbehandlerEnhetstilgangTjeneste;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.HentEnhetListeRessursIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.HentEnhetListeUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.informasjon.WSEnhet;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.meldinger.WSHentEnhetListeRequest;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.meldinger.WSHentEnhetListeResponse;
import no.nav.vedtak.felles.integrasjon.organisasjonressursenhet.klient.OrganisasjonRessursEnhetConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrganisasjonRessursEnhetTjenesteImpl implements OrganisasjonRessursEnhetTjeneste {

    private static final Logger log = LoggerFactory.getLogger(OrganisasjonRessursEnhetTjenesteImpl.class);

    private static final String TJENESTE = "OrganisasjonRessursEnhet";

    private OrganisasjonRessursEnhetConsumer organisasjonRessursEnhetConsumer;
    private SaksbehandlerEnhetstilgangTjeneste saksbehandlerEnhetstilgangTjeneste;

    public OrganisasjonRessursEnhetTjenesteImpl() {
        // CDI
    }

    @Inject
    public OrganisasjonRessursEnhetTjenesteImpl(OrganisasjonRessursEnhetConsumer organisasjonRessursEnhetConsumer,
                                                SaksbehandlerEnhetstilgangTjeneste saksbehandlerEnhetstilgangTjeneste) {
        this.organisasjonRessursEnhetConsumer = organisasjonRessursEnhetConsumer;
        this.saksbehandlerEnhetstilgangTjeneste = saksbehandlerEnhetstilgangTjeneste;
    }

    @Override
    public List<OrganisasjonsEnhet> hentEnhetListe(String ressursId) {
        Optional<WSHentEnhetListeResponse> response = hentEnhetForRessursIdListe(ressursId);
        var norgResultat = mapEnhetListeToOrganisasjonsEnhet(response);
        testAxsys(norgResultat, ressursId);
        return norgResultat;
    }

    private void testAxsys(List<OrganisasjonsEnhet> norgEnheter, String saksbehandlerIdent) {
        var saksbehandler = new Saksbehandler(saksbehandlerIdent);
        try {
            var axsysEnheter = saksbehandlerEnhetstilgangTjeneste.hentEnheter(saksbehandler);
            boolean resultatLikt = listEqualsIgnoreOrder(norgEnheter, axsysEnheter) && norgEnheter.size() == (axsysEnheter.size());
            if (resultatLikt) {
                log.info("Axsys og Norg ga samme resultat");
            } else {
                log.info("Axsys og Norg ga ikke samme resultat. Norg: {}, Axsys: {}", logformat(norgEnheter), logformat(axsysEnheter));
            }
        } catch (Exception e) {
            log.info("Axsys feilet", e);
        }
    }

    private static String logformat(List<OrganisasjonsEnhet> enheter) {
        return enheter.stream().map(OrganisasjonsEnhet::getEnhetId).collect(Collectors.joining(","));
    }

    public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
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
