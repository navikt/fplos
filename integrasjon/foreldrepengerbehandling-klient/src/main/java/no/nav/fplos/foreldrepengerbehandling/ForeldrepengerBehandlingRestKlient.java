package no.nav.fplos.foreldrepengerbehandling;

import no.nav.fplos.foreldrepengerbehandling.dto.KontrollerFaktaDataDto;
import no.nav.fplos.foreldrepengerbehandling.dto.KontrollerFaktaPeriodeDto;
import no.nav.fplos.foreldrepengerbehandling.dto.SokefeltDto;
import no.nav.fplos.foreldrepengerbehandling.dto.aksjonspunkt.AksjonspunktDto;
import no.nav.fplos.foreldrepengerbehandling.dto.behandling.ResourceLink;
import no.nav.fplos.foreldrepengerbehandling.dto.behandling.UtvidetBehandlingDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.fplos.foreldrepengerbehandling.dto.inntektarbeidytelse.Beløp;
import no.nav.fplos.foreldrepengerbehandling.dto.inntektarbeidytelse.InntektArbeidYtelseDto;
import no.nav.fplos.foreldrepengerbehandling.dto.inntektarbeidytelse.InntektsmeldingDto;
import no.nav.fplos.foreldrepengerbehandling.dto.ytelsefordeling.YtelseFordelingDto;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;
import no.nav.vedtak.sikkerhet.loginmodule.ContainerLogin;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt.aksjonspunktFra;

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
            LOGGER.info("Slår opp i fpsak for behandlingId {} per GET-kall til {}", behandlingId, uriBuilder.build());
            UtvidetBehandlingDto response = oidcRestClient.get(uriBuilder.build(), UtvidetBehandlingDto.class);
            List<ResourceLink> links = response.getLinks();
            BehandlingFpsak.Builder builder = BehandlingFpsak.builder()
                    .medBehandlingId(response.getId())
                    .medUuid(response.getUuid())
                    .medType(response.getType().getKode())
                    .medBehandlendeEnhet(response.getBehandlendeEnhetId())
                    .medBehandlendeEnhetNavn(response.getBehandlendeEnhetNavn())
                    .medStatus(response.getStatus().getKode())
                    .medAnsvarligSaksbehandler(response.getAnsvarligSaksbehandler())
                    .medBehandlingstidFrist(response.getBehandlingsfristTid())
                    .medFørsteUttaksdag(hentFørsteUttaksdato(links))
                    .medHarRefusjonskrav(hentHarRefusjonskrav(links))
                    .medAksjonspunkter(hentAksjonspunkter(links));
            hentUttakKontrollerFaktaPerioder(behandlingId, builder, links);
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            loginContext.logout();
        }
    }

    public Optional<UUID> getBehandlingUUID(Long behandlingId) {
        URIBuilder uriBuilder = new URIBuilder(URI.create(endpointFpsakRestBase + FPSAK_BEHANDLINGER));
        uriBuilder.setParameter("behandlingId", String.valueOf(behandlingId));
        ContainerLogin loginContext = new ContainerLogin();
        loginContext.login();

        try {
            LOGGER.info("Slår opp UUID i fpsak for behandlingId {} per GET-kall til {}", behandlingId, uriBuilder.build());
            UtvidetBehandlingDto response = oidcRestClient.get(uriBuilder.build(), UtvidetBehandlingDto.class);
            return Optional.ofNullable(response.getUuid());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            loginContext.logout();
        }
    }

    private List<Aksjonspunkt> hentAksjonspunkter(List<ResourceLink> links) {
        return velgLink(links, AKSJONSPUNKTER_LINK)
                .flatMap(ap -> hentFraResourceLink(ap, AksjonspunktDto[].class))
                .map(ForeldrepengerBehandlingRestKlient::aksjonspunktFraDto)
                .orElse(Collections.emptyList());
    }

    private Boolean hentHarRefusjonskrav(List<ResourceLink> links) {
        return velgLink(links, INNTEKT_ARBEID_YTELSE_LINK)
                .flatMap(iay -> hentFraResourceLink(iay, InntektArbeidYtelseDto.class))
                .map(ForeldrepengerBehandlingRestKlient::harRefusjonskrav)
                .orElse(null); // har ikke inntektsmelding enda, kan ikke vurdere refusjonskrav
    }

    private LocalDate hentFørsteUttaksdato(List<ResourceLink> links) {
        return velgLink(links, YTELSEFORDELING_LINK)
                .flatMap(yf -> hentFraResourceLink(yf, YtelseFordelingDto.class))
                .map(YtelseFordelingDto::getFørsteUttaksdato)
                .orElse(null);
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
        return "POST".equals(resourceLink.getType().name())
                ? oidcRestClient.postReturnsOptional(uri, resourceLink.getRequestPayload(), clazz)
                : oidcRestClient.getReturnsOptional(uri, clazz);
    }

    public List<FagsakDto> getFagsakFraSaksnummer(String saksnummer) {
        URIBuilder uriBuilder = new URIBuilder(URI.create(endpointFpsakRestBase + FPSAK_FAGSAK_SAKSNUMMER));
        uriBuilder.setParameter("saksnummer", saksnummer);
        ContainerLogin loginContext = new ContainerLogin();
        loginContext.login();

        try {
            FagsakDto fagsakDtos = oidcRestClient.get(uriBuilder.build(), FagsakDto.class);
            return Collections.singletonList(fagsakDtos);
        } catch (URISyntaxException e) {
            LOGGER.error("Feilet å hente Fagsak fra FPSAK for saksnummer: " + saksnummer, e);
            return Collections.emptyList();
        } finally {
            loginContext.logout();
        }
    }

    public List<FagsakDto> getFagsakFraFnr(String fnr) {
        URIBuilder uriBuilder = new URIBuilder(URI.create(endpointFpsakRestBase + FPSAK_FAGSAK_FNR));
        SokefeltDto sokefeltDto = new SokefeltDto(fnr);
        ContainerLogin loginContext = new ContainerLogin();
        loginContext.login();

        try {
            FagsakDto[] fagsakDtos = oidcRestClient.post(uriBuilder.build(), sokefeltDto, FagsakDto[].class);
            return Arrays.asList(fagsakDtos);
        } catch (URISyntaxException e) {
            LOGGER.error("Feilet å hente Fagsak fra FPSAK for en fødselsnummer", e);
            return Collections.emptyList();
        } finally {
            loginContext.logout();
        }
    }

    private static boolean harGraderingFra(KontrollerFaktaDataDto faktaDataDto) {
        return faktaDataDto.getPerioder().stream()
                .map(KontrollerFaktaPeriodeDto::getArbeidstidsprosent)
                .filter(Objects::nonNull)
                .anyMatch(a -> a.compareTo(BigDecimal.ZERO) != 0);
    }

    private static boolean harRefusjonskrav(InntektArbeidYtelseDto inntektArbeidYtelseDto) {
        return inntektArbeidYtelseDto.getInntektsmeldinger().stream()
                .map(InntektsmeldingDto::getGetRefusjonBeløpPerMnd)
                .filter(Objects::nonNull)
                .anyMatch(refusjonsbeløp -> refusjonsbeløp.compareTo(Beløp.ZERO) > 0);
    }

    private static List<Aksjonspunkt> aksjonspunktFraDto(AksjonspunktDto[] aksjonspunktDtos) {
        List<Aksjonspunkt> liste = new ArrayList<>();
        for (AksjonspunktDto aksjonspunktDto : aksjonspunktDtos) {
            liste.add(aksjonspunktFra(aksjonspunktDto));
        }
        return liste;
    }

    private static Optional<ResourceLink> velgLink(List<ResourceLink> links, String typeLink) {
        return Optional.ofNullable(links)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(l -> l.getRel().equals(typeLink))
                .findFirst();
    }
}
