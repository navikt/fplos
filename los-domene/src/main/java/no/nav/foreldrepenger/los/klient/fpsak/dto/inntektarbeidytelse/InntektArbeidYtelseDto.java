package no.nav.foreldrepenger.los.klient.fpsak.dto.inntektarbeidytelse;

import java.util.Collections;
import java.util.List;

public class InntektArbeidYtelseDto {

    private List<InntektsmeldingDto> inntektsmeldinger = Collections.emptyList();

    public void setInntektsmeldinger(List<InntektsmeldingDto> inntektsmeldinger) {
        this.inntektsmeldinger = inntektsmeldinger;
    }

    public List<InntektsmeldingDto> getInntektsmeldinger() {
        return inntektsmeldinger;
    }

}
