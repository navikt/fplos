package no.nav.foreldrepenger.los.klient.fpsak.dto.inntektarbeidytelse;

import java.util.List;

public record InntektsmeldingerDto(List<InntektsmeldingDto> inntektsmeldinger) {

    public List<InntektsmeldingDto> inntektsmeldinger() {
        return inntektsmeldinger == null ? List.of() : inntektsmeldinger;
    }
}