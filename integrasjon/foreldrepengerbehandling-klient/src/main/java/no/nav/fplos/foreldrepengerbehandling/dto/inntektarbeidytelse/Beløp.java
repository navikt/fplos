package no.nav.fplos.foreldrepengerbehandling.dto.inntektarbeidytelse;

import java.math.BigDecimal;

public class Beløp {

    private BigDecimal verdi;

    Beløp(){}

    public BigDecimal getVerdi() {
        return verdi;
    }

    public void setVerdi(BigDecimal verdi) {
        this.verdi = verdi;
    }
}
