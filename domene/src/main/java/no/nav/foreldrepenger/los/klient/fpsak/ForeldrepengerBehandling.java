package no.nav.foreldrepenger.los.klient.fpsak;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak.FagsakDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.inntektarbeidytelse.Beløp;
import no.nav.foreldrepenger.los.klient.fpsak.dto.inntektarbeidytelse.InntektsmeldingDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.inntektarbeidytelse.InntektsmeldingerDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.ytelsefordeling.YtelseFordelingDto;

public interface ForeldrepengerBehandling {

    Logger LOG = LoggerFactory.getLogger(ForeldrepengerBehandling.class);
    String BEHANDLING_ID = "behandlingId";
    String INNTEKTSMELDINGER_LINK = "inntektsmeldinger";
    String UTTAK_KONTROLLER_FAKTA_PERIODER_LINK = "uttak-kontroller-fakta-perioder";
    String KONTROLLRESULTAT = "kontrollresultat";
    String YTELSEFORDELING_LINK = "ytelsefordeling";
    String FAGSAK_LINK = "fagsak";

    <T> Optional<T> hentFraResourceLink(ResourceLink resourceLink, Class<T> clazz);

    BehandlingDto hentUtvidetBehandlingDto(String behandlingId);

    default BehandlingFpsak getBehandling(BehandlingId behandlingId) {
        var behandlingDto = hentUtvidetBehandlingDto(behandlingId.toUUID().toString());
        var links = behandlingDto.links();
        var builder = BehandlingFpsak.builder()
                .medBehandlingType(behandlingDto.type())
                .medBehandlingId(new BehandlingId(behandlingDto.uuid()))
                .medBehandlingOpprettet(behandlingDto.opprettet())
                .medBehandlendeEnhetId(behandlingDto.behandlendeEnhetId())
                .medStatus(behandlingDto.status())
                .medAnsvarligSaksbehandler(behandlingDto.ansvarligSaksbehandler())
                .medHarRefusjonskravFraArbeidsgiver(new Lazy<>(() -> hentHarRefusjonskrav(links)))
                .medAksjonspunkt(aksjonspunktFraDto(behandlingDto.aksjonspunkt()))
                .medBehandlingstidFrist(behandlingDto.behandlingsfristTid())
                .medYtelseFordeling(new Lazy<>(() -> hentYtelseFordeling(links)))
                .medErBerørtBehandling(harBehandlingÅrsakType(behandlingDto, BehandlingÅrsakType.BERØRT_BEHANDLING))
                .medErPleiepengerBehandling(harBehandlingÅrsakType(behandlingDto, BehandlingÅrsakType.RE_VEDTAK_PLEIEPENGER))
                .medErEndringssøknad(harBehandlingÅrsakType(behandlingDto, BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER))
                .medKontrollresultat(new Lazy<>(() -> hentKontrollresultat(links)))
                .medUttakEgenskaper(new Lazy<>(() -> hentUttakEgenskaper(behandlingId, links)));
        return builder.build();
    }

    default FagsakDto hentFagsak(List<ResourceLink> links) {
        return velgLink(links, FAGSAK_LINK).flatMap(f -> hentFraResourceLink(f, FagsakDto.class))
                .orElse(null);
    }

    private static boolean harBehandlingÅrsakType(BehandlingDto dto, BehandlingÅrsakType type) {
        return dto.behandlingÅrsaker()
                .stream()
                .map(BehandlingÅrsakDto::behandlingÅrsakType)
                .filter(Objects::nonNull)
                .anyMatch(t -> t.equals(type));
    }

    private YtelseFordelingDto hentYtelseFordeling(List<ResourceLink> links) {
        return velgLink(links, YTELSEFORDELING_LINK).flatMap(yf -> hentFraResourceLink(yf, YtelseFordelingDto.class))
                .orElse(null);
    }

    private Boolean hentHarRefusjonskrav(List<ResourceLink> links) {
        return velgLink(links, INNTEKTSMELDINGER_LINK)
                .flatMap(iay -> hentFraResourceLink(iay, InntektsmeldingerDto.class))
                .map(ForeldrepengerBehandling::harRefusjonskrav)
                .orElse(null); // har ikke inntektsmelding enda, kan ikke vurdere refusjonskrav
    }

    private UttakEgenskaper hentUttakEgenskaper(BehandlingId behandlingId, List<ResourceLink> links) {
        var uttakLink = velgLink(links, UTTAK_KONTROLLER_FAKTA_PERIODER_LINK);
        if (uttakLink.isPresent()) {
            var kontrollerFaktaData = hentFraResourceLink(uttakLink.get(), KontrollerFaktaDataDto.class);
            if (kontrollerFaktaData.isPresent()) {
                var uttakEgenskaper = new UttakEgenskaper(harVurderSykdom(kontrollerFaktaData.get()),
                        harGraderingFra(kontrollerFaktaData.get()));
                LOG.info("Utleder uttaksegenskaper {}", uttakEgenskaper);
                return uttakEgenskaper;
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
        return kontrollerFaktaDataDto.perioder().stream()
                .anyMatch(KontrollerFaktaPeriodeDto::gjelderSykdom);
    }

    private static boolean harGraderingFra(KontrollerFaktaDataDto faktaDataDto) {
        return faktaDataDto.perioder().stream()
                .map(KontrollerFaktaPeriodeDto::arbeidstidsprosent)
                .filter(Objects::nonNull)
                .anyMatch(a -> a.compareTo(BigDecimal.ZERO) != 0);
    }

    private static boolean harRefusjonskrav(InntektsmeldingerDto inntektArbeidYtelseDto) {
        return inntektArbeidYtelseDto.inntektsmeldinger().stream()
                .map(InntektsmeldingDto::getRefusjonBeløpPerMnd)
                .filter(Objects::nonNull)
                .anyMatch(refusjonsbeløp -> refusjonsbeløp.compareTo(Beløp.ZERO) > 0);
    }

    private static List<Aksjonspunkt> aksjonspunktFraDto(List<AksjonspunktDto> aksjonspunktDtos) {
        return aksjonspunktDtos.stream().map(Aksjonspunkt::aksjonspunktFra).toList();
    }

    private static Optional<ResourceLink> velgLink(List<ResourceLink> links, String typeLink) {
        return links.stream()
                .filter(l -> l.getRel().equals(typeLink))
                .findFirst();
    }

}
