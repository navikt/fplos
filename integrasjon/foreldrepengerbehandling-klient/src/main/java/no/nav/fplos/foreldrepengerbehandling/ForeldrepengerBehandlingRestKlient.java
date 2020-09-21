package no.nav.fplos.foreldrepengerbehandling;

import static no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt.aksjonspunktFra;

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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.fplos.foreldrepengerbehandling.dto.KontrollerFaktaDataDto;
import no.nav.fplos.foreldrepengerbehandling.dto.KontrollerFaktaPeriodeDto;
import no.nav.fplos.foreldrepengerbehandling.dto.SokefeltDto;
import no.nav.fplos.foreldrepengerbehandling.dto.aksjonspunkt.AksjonspunktDto;
import no.nav.fplos.foreldrepengerbehandling.dto.behandling.BehandlingÅrsakDto;
import no.nav.fplos.foreldrepengerbehandling.dto.behandling.BehandlingÅrsakType;
import no.nav.fplos.foreldrepengerbehandling.dto.behandling.ResourceLink;
import no.nav.fplos.foreldrepengerbehandling.dto.behandling.UtvidetBehandlingDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.fplos.foreldrepengerbehandling.dto.inntektarbeidytelse.Beløp;
import no.nav.fplos.foreldrepengerbehandling.dto.inntektarbeidytelse.InntektArbeidYtelseDto;
import no.nav.fplos.foreldrepengerbehandling.dto.inntektarbeidytelse.InntektsmeldingDto;
import no.nav.fplos.foreldrepengerbehandling.dto.ytelsefordeling.YtelseFordelingDto;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;
import no.nav.vedtak.sikkerhet.loginmodule.ContainerLogin;

@ApplicationScoped
public class ForeldrepengerBehandlingRestKlient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForeldrepengerBehandlingRestKlient.class);

    private static final String FPSAK_FAGSAK_FNR = "/fpsak/api/fagsak/sok";
    private static final String FPSAK_BEHANDLINGER = "/fpsak/api/behandlinger";
    private static final String FPSAK_FAGSAK_SAKSNUMMER = "/fpsak/api/fagsak";
    private static final String AKSJONSPUNKTER_LINK = "aksjonspunkter";
    private static final String INNTEKT_ARBEID_YTELSE_LINK = "inntekt-arbeid-ytelse";
    private static final String UTTAK_KONTROLLER_FAKTA_PERIODER_LINK = "uttak-kontroller-fakta-perioder";
    private static final String YTELSEFORDELING_LINK = "ytelsefordeling";

    private OidcRestClient oidcRestClient;
    private String fpsakBaseUrl;

    @Inject
    public ForeldrepengerBehandlingRestKlient(OidcRestClient oidcRestClient,
                                              @KonfigVerdi(value = "fpsak.url", defaultVerdi = "http://fpsak") String fpsakUrl) {
        this.oidcRestClient = oidcRestClient;
        this.fpsakBaseUrl = fpsakUrl;
    }

    public ForeldrepengerBehandlingRestKlient() {
        // for CDI
    }

    public BehandlingFpsak getBehandling(BehandlingId behandlingId) {
        URIBuilder uriBuilder = new URIBuilder(URI.create(fpsakBaseUrl + FPSAK_BEHANDLINGER));
        uriBuilder.setParameter("behandlingId", behandlingId.toString());
        ContainerLogin loginContext = new ContainerLogin();
        loginContext.login();

        try {
            LOGGER.info("Slår opp i fpsak for behandling {} per GET-kall til {}", behandlingId, uriBuilder.build());
            UtvidetBehandlingDto behandlingDto = oidcRestClient.get(uriBuilder.build(), UtvidetBehandlingDto.class);
            List<ResourceLink> links = behandlingDto.getLinks();
            BehandlingFpsak.Builder builder = BehandlingFpsak.builder()
                    .medBehandlingType(behandlingDto.getType())
                    .medBehandlingId(new BehandlingId(behandlingDto.getUuid()))
                    .medBehandlendeEnhetNavn(behandlingDto.getBehandlendeEnhetNavn())
                    .medStatus(behandlingDto.getStatus().getKode())
                    .medAnsvarligSaksbehandler(behandlingDto.getAnsvarligSaksbehandler())
                    .medHarRefusjonskravFraArbeidsgiver(hentHarRefusjonskrav(links))
                    .medAksjonspunkter(hentAksjonspunkter(links))
                    .medBehandlingstidFrist(behandlingDto.getBehandlingsfristTid())
                    .medFørsteUttaksdag(hentFørsteUttaksdato(links))
                    .medErBerørtBehandling(harBehandlingÅrsakType(behandlingDto, BehandlingÅrsakType.BERØRT_BEHANDLING))
                    .medErEndringssøknad(harBehandlingÅrsakType(behandlingDto, BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER));
            hentUttakKontrollerFaktaPerioder(behandlingId, builder, links);
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            loginContext.logout();
        }
    }

    private static boolean harBehandlingÅrsakType(UtvidetBehandlingDto dto, BehandlingÅrsakType type) {
        return Optional.ofNullable(dto.getBehandlingÅrsaker())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(BehandlingÅrsakDto::getBehandlingÅrsakType)
                .filter(Objects::nonNull)
                .anyMatch(t -> t.equals(type));
    }

    public Optional<Long> getFpsakInternBehandlingId(BehandlingId eksternBehandlingId) {
        URIBuilder uriBuilder = new URIBuilder(URI.create(fpsakBaseUrl + FPSAK_BEHANDLINGER));
        uriBuilder.setParameter("behandlingId", eksternBehandlingId.toString());
        ContainerLogin loginContext = new ContainerLogin();
        loginContext.login();

        try {
            LOGGER.info("Slår opp intern behandling id i fpsak for behandling med eksternBehandlingId {} per GET-kall til {}", eksternBehandlingId, uriBuilder.build());
            UtvidetBehandlingDto behandlingDto = oidcRestClient.get(uriBuilder.build(), UtvidetBehandlingDto.class);
            return Optional.ofNullable(behandlingDto.getId());
        } catch (ManglerTilgangException e) {
            throw new InternIdMappingException(eksternBehandlingId);
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

    private LocalDate hentFørsteUttaksdato(List<ResourceLink> links) {
        return velgLink(links, YTELSEFORDELING_LINK)
                .flatMap(yf -> hentFraResourceLink(yf, YtelseFordelingDto.class))
                .map(YtelseFordelingDto::getFørsteUttaksdato)
                .orElse(null);
    }

    private Boolean hentHarRefusjonskrav(List<ResourceLink> links) {
        return velgLink(links, INNTEKT_ARBEID_YTELSE_LINK)
                .flatMap(iay -> hentFraResourceLink(iay, InntektArbeidYtelseDto.class))
                .map(ForeldrepengerBehandlingRestKlient::harRefusjonskrav)
                .orElse(null); // har ikke inntektsmelding enda, kan ikke vurdere refusjonskrav
    }

    private void hentUttakKontrollerFaktaPerioder(BehandlingId behandlingId, BehandlingFpsak.Builder builder, List<ResourceLink> links) {
        Optional<ResourceLink> uttakLink = velgLink(links, UTTAK_KONTROLLER_FAKTA_PERIODER_LINK);
        if (uttakLink.isPresent()) {
            Optional<KontrollerFaktaDataDto> kontrollerFaktaData = hentFraResourceLink(uttakLink.get(), KontrollerFaktaDataDto.class);
            if (kontrollerFaktaData.isPresent()) {
                builder.medHarGradering(harGraderingFra(kontrollerFaktaData.get()));
                builder.medHarVurderSykdom(harVurderSykdom(kontrollerFaktaData.get()));
            } else {
                LOGGER.error("Feilet å hente gradering for behandlingId " + behandlingId);
            }
        }
    }

    private boolean harVurderSykdom(KontrollerFaktaDataDto kontrollerFaktaDataDto) {
        return kontrollerFaktaDataDto.getPerioder().stream()
                .anyMatch(KontrollerFaktaPeriodeDto::gjelderSykdom);
    }


        private <T> Optional<T> hentFraResourceLink(ResourceLink resourceLink, Class<T> clazz) {
        URI uri = URI.create(fpsakBaseUrl + resourceLink.getHref());
        return "POST".equals(resourceLink.getType().name())
                ? oidcRestClient.postReturnsOptional(uri, resourceLink.getRequestPayload(), clazz)
                : oidcRestClient.getReturnsOptional(uri, clazz);
    }

    public List<FagsakDto> getFagsakFraSaksnummer(String saksnummer) {
        URIBuilder uriBuilder = new URIBuilder(URI.create(fpsakBaseUrl + FPSAK_FAGSAK_SAKSNUMMER));
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
        URIBuilder uriBuilder = new URIBuilder(URI.create(fpsakBaseUrl + FPSAK_FAGSAK_FNR));
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
