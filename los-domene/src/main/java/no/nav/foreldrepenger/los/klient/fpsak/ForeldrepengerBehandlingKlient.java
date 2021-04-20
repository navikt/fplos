package no.nav.foreldrepenger.los.klient.fpsak;

import static no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt.aksjonspunktFra;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.klient.fpsak.dto.KontrollerFaktaDataDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.KontrollerFaktaPeriodeDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.KontrollresultatDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.aksjonspunkt.AksjonspunktDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.BehandlingÅrsakDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.BehandlingÅrsakType;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.ResourceLink;
import no.nav.foreldrepenger.los.klient.fpsak.dto.inntektarbeidytelse.Beløp;
import no.nav.foreldrepenger.los.klient.fpsak.dto.inntektarbeidytelse.InntektArbeidYtelseDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.inntektarbeidytelse.InntektsmeldingDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.ytelsefordeling.YtelseFordelingDto;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class ForeldrepengerBehandlingKlient {

    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerBehandlingKlient.class);

    private static final String FPSAK_BEHANDLINGER = "/fpsak/api/behandlinger";
    private static final String AKSJONSPUNKTER_LINK = "aksjonspunkter";
    private static final String INNTEKT_ARBEID_YTELSE_LINK = "inntekt-arbeid-ytelse";
    private static final String UTTAK_KONTROLLER_FAKTA_PERIODER_LINK = "uttak-kontroller-fakta-perioder";
    private static final String KONTROLLRESULTAT = "kontrollresultat";
    private static final String YTELSEFORDELING_LINK = "ytelsefordeling";

    private OidcRestClient oidcRestClient;
    private String fpsakBaseUrl;

    @Inject
    public ForeldrepengerBehandlingKlient(OidcRestClient oidcRestClient,
                                          @KonfigVerdi(value = "fpsak.url", defaultVerdi = "http://fpsak") String fpsakUrl) {
        this.oidcRestClient = oidcRestClient;
        this.fpsakBaseUrl = fpsakUrl;
    }

    public ForeldrepengerBehandlingKlient() {
        // for CDI
    }

    public BehandlingFpsak getBehandling(BehandlingId behandlingId) {
        var uri = behandlingUri(behandlingId.toString());
        var behandlingDto = oidcRestClient.get(uri, BehandlingDto.class);
        var links = behandlingDto.links();
        var builder = BehandlingFpsak.builder()
                .medBehandlingType(behandlingDto.type())
                .medBehandlingId(new BehandlingId(behandlingDto.uuid()))
                .medBehandlingOpprettet(behandlingDto.opprettet())
                .medBehandlendeEnhetId(behandlingDto.behandlendeEnhetId())
                .medStatus(behandlingDto.status().getKode())
                .medAnsvarligSaksbehandler(behandlingDto.ansvarligSaksbehandler())
                .medHarRefusjonskravFraArbeidsgiver(new Lazy<>(() -> hentHarRefusjonskrav(links)))
                .medAksjonspunkter(new Lazy<>(() -> hentAksjonspunkter(links)))
                .medBehandlingstidFrist(behandlingDto.behandlingsfristTid())
                .medFørsteUttaksdag(new Lazy<>(() -> hentFørsteUttaksdato(links)))
                .medErBerørtBehandling(harBehandlingÅrsakType(behandlingDto, BehandlingÅrsakType.BERØRT_BEHANDLING))
                .medErEndringssøknad(harBehandlingÅrsakType(behandlingDto, BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER))
                .medKontrollresultat(new Lazy<>(() -> hentKontrollresultat(links)))
                .medUttakEgenskaper(new Lazy<>(() -> hentUttakEgenskaper(behandlingId, links)));
        return builder.build();
    }

    public BehandlingDto hentUtvidetBehandlingDto(String behandlingId) {
        var uri = behandlingUri(behandlingId);
        return oidcRestClient.get(uri, BehandlingDto.class);
    }

    public Optional<Long> getFpsakInternBehandlingId(BehandlingId eksternBehandlingId) {
        var uri = behandlingUri(eksternBehandlingId.toString());
        try {
            var behandlingDto = oidcRestClient.get(uri, BehandlingDto.class);
            return Optional.ofNullable(behandlingDto.id());
        } catch (ManglerTilgangException e) {
            throw new InternIdMappingException(eksternBehandlingId);
        }
    }

    private URI behandlingUri(String id) {
        try {
            var builder = new URIBuilder(URI.create(fpsakBaseUrl + FPSAK_BEHANDLINGER));
            builder.setParameter("behandlingId", id);
            return builder.build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Ikke gyldig uri for behandling", e);
        }
    }

    private static boolean harBehandlingÅrsakType(BehandlingDto dto, BehandlingÅrsakType type) {
        return dto.behandlingÅrsaker()
                .stream()
                .map(BehandlingÅrsakDto::behandlingÅrsakType)
                .filter(Objects::nonNull)
                .anyMatch(t -> t.equals(type));
    }

    private List<Aksjonspunkt> hentAksjonspunkter(List<ResourceLink> links) {
        return velgLink(links, AKSJONSPUNKTER_LINK)
                .flatMap(ap -> hentFraResourceLink(ap, AksjonspunktDto[].class))
                .map(ForeldrepengerBehandlingKlient::aksjonspunktFraDto)
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
                .map(ForeldrepengerBehandlingKlient::harRefusjonskrav)
                .orElse(null); // har ikke inntektsmelding enda, kan ikke vurdere refusjonskrav
    }

    private UttakEgenskaper hentUttakEgenskaper(BehandlingId behandlingId, List<ResourceLink> links) {
        var uttakLink = velgLink(links, UTTAK_KONTROLLER_FAKTA_PERIODER_LINK);
        if (uttakLink.isPresent()) {
            var kontrollerFaktaData = hentFraResourceLink(uttakLink.get(), KontrollerFaktaDataDto.class);
            if (kontrollerFaktaData.isPresent()) {
                return new UttakEgenskaper(harVurderSykdom(kontrollerFaktaData.get()), harGraderingFra(kontrollerFaktaData.get()));
            }
            LOG.warn("Kunne ikke hente gradering for behandlingId " + behandlingId);
        }
        return null;
    }

    private KontrollresultatDto hentKontrollresultat(List<ResourceLink> links) {
        return velgLink(links, KONTROLLRESULTAT).flatMap(resourceLink -> hentFraResourceLink(resourceLink, KontrollresultatDto.class))
                .orElse(null);
    }

    private boolean harVurderSykdom(KontrollerFaktaDataDto kontrollerFaktaDataDto) {
        return kontrollerFaktaDataDto.getPerioder().stream()
                .anyMatch(KontrollerFaktaPeriodeDto::gjelderSykdom);
    }

    private <T> Optional<T> hentFraResourceLink(ResourceLink resourceLink, Class<T> clazz) {
        var uri = URI.create(fpsakBaseUrl + resourceLink.getHref());
        return "POST".equals(resourceLink.getType().name())
                ? oidcRestClient.postReturnsOptional(uri, resourceLink.getRequestPayload(), clazz)
                : oidcRestClient.getReturnsOptional(uri, clazz);
    }

    private static boolean harGraderingFra(KontrollerFaktaDataDto faktaDataDto) {
        return faktaDataDto.getPerioder().stream()
                .map(KontrollerFaktaPeriodeDto::getArbeidstidsprosent)
                .filter(Objects::nonNull)
                .anyMatch(a -> a.compareTo(BigDecimal.ZERO) != 0);
    }

    private static boolean harRefusjonskrav(InntektArbeidYtelseDto inntektArbeidYtelseDto) {
        return inntektArbeidYtelseDto.inntektsmeldinger().stream()
                .map(InntektsmeldingDto::getRefusjonBeløpPerMnd)
                .filter(Objects::nonNull)
                .anyMatch(refusjonsbeløp -> refusjonsbeløp.compareTo(Beløp.ZERO) > 0);
    }

    private static List<Aksjonspunkt> aksjonspunktFraDto(AksjonspunktDto[] aksjonspunktDtos) {
        List<Aksjonspunkt> liste = new ArrayList<>();
        for (var dto : aksjonspunktDtos) {
            liste.add(aksjonspunktFra(dto));
        }
        return liste;
    }

    private static Optional<ResourceLink> velgLink(List<ResourceLink> links, String typeLink) {
        return links.stream()
                .filter(l -> l.getRel().equals(typeLink))
                .findFirst();
    }

}
