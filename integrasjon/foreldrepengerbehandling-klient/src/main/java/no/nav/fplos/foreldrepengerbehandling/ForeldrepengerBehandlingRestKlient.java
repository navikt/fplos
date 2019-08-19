package no.nav.fplos.foreldrepengerbehandling;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.fplos.foreldrepengerbehandling.dto.KontrollerFaktaDataDto;
import no.nav.fplos.foreldrepengerbehandling.dto.KontrollerFaktaPeriodeDto;
import no.nav.fplos.foreldrepengerbehandling.dto.SokefeltDto;
import no.nav.fplos.foreldrepengerbehandling.dto.aksjonspunkt.AksjonspunktDto;
import no.nav.fplos.foreldrepengerbehandling.dto.behandling.ResourceLink;
import no.nav.fplos.foreldrepengerbehandling.dto.behandling.UtvidetBehandlingDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.fplos.foreldrepengerbehandling.dto.inntektarbeidytelse.InntektArbeidYtelseDto;
import no.nav.fplos.foreldrepengerbehandling.dto.ytelsefordeling.YtelseFordelingDto;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;
import no.nav.vedtak.sikkerhet.loginmodule.ContainerLogin;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ForeldrepengerBehandlingRestKlient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForeldrepengerBehandlingRestKlient.class);
    private static final String AKSJONSPUNKTER_LINK = "aksjonspunkter";
    private static final String INNTEKT_ARBEID_YTELSE_LINK = "inntekt-arbeid-ytelse";
    private static final String YTELSEFORDELING_LINK = "ytelsefordeling";
    private static final String UTTAK_KONTROLLER_FAKTA_PERIODER_LINK = "uttak-kontroller-fakta-perioder";

    private static final String FPSAK_FAGSAK_FNR = "/fpsak/api/fagsak/sok";
    private static final String FPSAK_BEHANDLINGER = "/fpsak/api/behandlinger";
    private static final String FPSAK_FAGSAK_SAKSNUMMER = "/fpsak/api/fagsak";

    private OidcRestClient oidcRestClient;
    private String endpointFpsakRestBase;


    @Inject
    public ForeldrepengerBehandlingRestKlient(OidcRestClient oidcRestClient, @KonfigVerdi("fpsak.url") String fpsakUrl) {
        this.oidcRestClient = oidcRestClient;
        this.endpointFpsakRestBase = fpsakUrl;
    }

    public ForeldrepengerBehandlingRestKlient() { //NOSONAR: for cdi
    }

    // @TODO denne metoden skal portes til å bruke UUID
    public BehandlingFpsak getBehandling(Long behandlingId) {
        URIBuilder uriBuilder = new URIBuilder(URI.create(endpointFpsakRestBase + FPSAK_BEHANDLINGER));
        uriBuilder.setParameter("behandlingId", String.valueOf(behandlingId));
        ContainerLogin loginContext = new ContainerLogin();
        loginContext.login();

        try {
            LOGGER.info("GET " + uriBuilder.build());
            UtvidetBehandlingDto response = oidcRestClient.get(uriBuilder.build(), UtvidetBehandlingDto.class);

            BehandlingFpsak.Builder builder = BehandlingFpsak.builder()
                    .medBehandlingId(response.getId())
                    .medType(response.getType().getKode())
                    .medBehandlendeEnhet(response.getBehandlendeEnhetId())
                    .medBehandlendeEnhetNavn(response.getBehandlendeEnhetNavn())
                    .medStatus(response.getStatus().getKode())
                    .medAnsvarligSaksbehandler(response.getAnsvarligSaksbehandler())
                    .medBehandlingstidFrist(response.getBehandlingsfristTid());

            List<ResourceLink> links = response.getLinks();
            hentAksjonspunkterRest(behandlingId, builder, links);
            hentInntektRest(behandlingId, builder, links);
            hentYtelsefordelingRest(behandlingId, builder, links);
            hentUttakKontrollerFaktaPerioder(behandlingId, builder, links);
            loginContext.logout();
            return builder.build();
        } catch (URISyntaxException | IntegrasjonException e) {
            LOGGER.error("Feilet å hente behandling fra FPSAK for behandlingId: " + behandlingId, e);
            return null;
        }
    }

    private void hentAksjonspunkterRest(Long behandlingId, BehandlingFpsak.Builder builder, List<ResourceLink> links) {
        Optional<ResourceLink> aksjonspunkterLink = velgLink(links, AKSJONSPUNKTER_LINK);
        if (aksjonspunkterLink.isPresent()) {
            Optional<AksjonspunktDto[]> aksjonspunkter = hentFraResourceLink(aksjonspunkterLink.get(), AksjonspunktDto[].class);
            if (aksjonspunkter.isPresent()) {
                builder.medAksjonspunkter(Arrays.asList(aksjonspunkter.get()));
            } else {
                LOGGER.error("Feilet å hente aksjonspunkter for behandlingId " + behandlingId);
            }
        }
    }

    private void hentInntektRest(Long behandlingId, BehandlingFpsak.Builder builder, List<ResourceLink> links) {
        Optional<ResourceLink> inntekterLink = velgLink(links, INNTEKT_ARBEID_YTELSE_LINK);
        if (inntekterLink.isPresent()) {
            Optional<InntektArbeidYtelseDto> iay = hentFraResourceLink(inntekterLink.get(), InntektArbeidYtelseDto.class);
            if (iay.isPresent()) {
                builder.medHarRefusjonskrav(harRefusjonskravFra(iay.get()));
            } else {
                LOGGER.error("Feilet å hente inntekt for behandlingId " + behandlingId);
            }
        }
    }

    private void hentYtelsefordelingRest(Long behandlingId, BehandlingFpsak.Builder builder, List<ResourceLink> links) {
        Optional<ResourceLink> ytelseFordelingLink = velgLink(links, YTELSEFORDELING_LINK);
        if (ytelseFordelingLink.isPresent()) {
            Optional<YtelseFordelingDto> ytelseFordeling = hentFraResourceLink(ytelseFordelingLink.get(), YtelseFordelingDto.class);
            if (ytelseFordeling.isPresent()) {
                builder.medFørsteUttaksdag(ytelseFordeling.get().getFørsteUttaksdato());
            } else {
                LOGGER.error("Feilet å hente ytelsefordeling for behandlingId " + behandlingId);
            }
        }
    }

    private void hentUttakKontrollerFaktaPerioder(Long behandlingId, BehandlingFpsak.Builder builder, List<ResourceLink> links) {
        Optional<ResourceLink> uttakLink = velgLink(links, UTTAK_KONTROLLER_FAKTA_PERIODER_LINK);
        if (uttakLink.isPresent()) {
            Optional<KontrollerFaktaDataDto> kontrollerFaktaData = hentFraResourceLink(uttakLink.get(), KontrollerFaktaDataDto.class);
            if (kontrollerFaktaData.isPresent()) {
                builder.medHarGradering(harGraderingFra(kontrollerFaktaData.get()));
            } else {
                LOGGER.error("Feilet å hente gradering for behandlingId " + behandlingId);
            }
        }
    }

    private <T> Optional<T> hentFraResourceLink(ResourceLink resourceLink, Class<T> clazz) {
        URI uri = URI.create(endpointFpsakRestBase + resourceLink.getHref());
        return "POST".equals(resourceLink.getType().name()) ? oidcRestClient.postReturnsOptional(uri, resourceLink.getRequestPayload(), clazz)
                : oidcRestClient.getReturnsOptional(uri, clazz);
    }

    public List<FagsakDto> getFagsakFraSaksnummer(String saksnummer) {
        URIBuilder uriBuilder = new URIBuilder(URI.create(endpointFpsakRestBase + FPSAK_FAGSAK_SAKSNUMMER));
        uriBuilder.setParameter("saksnummer", saksnummer);
        ContainerLogin loginContext = new ContainerLogin();
        loginContext.login();

        try {
            FagsakDto fagsakDtos = oidcRestClient.get(uriBuilder.build(), FagsakDto.class);
            loginContext.logout();
            return Collections.singletonList(fagsakDtos);
        } catch (URISyntaxException e) {
            LOGGER.error("Feilet å hente Fagsak fra FPSAK for saksnummer: " + saksnummer, e);
            loginContext.logout();
            return Collections.emptyList();
        }
    }

    public List<FagsakDto> getFagsakFraFnr(String fnr) {
        URIBuilder uriBuilder = new URIBuilder(URI.create(endpointFpsakRestBase + FPSAK_FAGSAK_FNR));
        SokefeltDto sokefeltDto = new SokefeltDto(fnr);
        ContainerLogin loginContext = new ContainerLogin();
        loginContext.login();

        try {
            FagsakDto[] fagsakDtos = oidcRestClient.post(uriBuilder.build(), sokefeltDto, FagsakDto[].class);
            loginContext.logout();
            return Arrays.asList(fagsakDtos);
        } catch (URISyntaxException e) {
            LOGGER.error("Feilet å hente Fagsak fra FPSAK for en fødselsnummer", e);
            loginContext.logout();
            return Collections.emptyList();
        }
    }

    private static boolean harGraderingFra(KontrollerFaktaDataDto faktaDataDto) {
        return faktaDataDto.getPerioder().stream()
                .map(KontrollerFaktaPeriodeDto::getArbeidstidsprosent)
                .filter(Objects::nonNull)
                .anyMatch(a -> a.compareTo(BigDecimal.ZERO) != 0);
    }

    private static boolean harRefusjonskravFra(InntektArbeidYtelseDto inntektArbeidYtelseDto) {
        return inntektArbeidYtelseDto.getInntektsmeldinger().stream()
                .anyMatch(e -> e.getGetRefusjonBeløpPerMnd() != null && e.getGetRefusjonBeløpPerMnd().getVerdi().intValue() > 0);
    }

    private static Optional<ResourceLink> velgLink(List<ResourceLink> links, String typeLink) {
        return Optional.ofNullable(links)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(l -> l.getRel().equals(typeLink))
                .findFirst();
    }
}
