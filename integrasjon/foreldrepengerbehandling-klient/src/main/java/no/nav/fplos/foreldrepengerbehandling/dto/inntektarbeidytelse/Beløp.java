package no.nav.fplos.foreldrepengerbehandling.dto.inntektarbeidytelse;

import java.math.BigDecimal;

public class Beløp {

    public static final Beløp ZERO = new Beløp(BigDecimal.ZERO);

    private BigDecimal verdi;

    Beløp(){}

    public Beløp(BigDecimal verdi) {
        this.verdi = verdi;
    }

    public BigDecimal getVerdi() {
        return verdi;
    }

    public void setVerdi(BigDecimal verdi) {
        this.verdi = verdi;
    }

    public int compareTo(Beløp annetBeløp) {
        return verdi.compareTo(annetBeløp.verdi);
    }
}
