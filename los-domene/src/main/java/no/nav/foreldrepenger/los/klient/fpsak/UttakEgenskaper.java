package no.nav.foreldrepenger.los.klient.fpsak;

import java.util.Objects;

public class UttakEgenskaper {
    private final boolean vurderSykdom;
    private final boolean gradering;

    public UttakEgenskaper(boolean vurderSykdom, boolean gradering) {
        this.vurderSykdom = vurderSykdom;
        this.gradering = gradering;
    }

    public boolean isVurderSykdom() {
        return vurderSykdom;
    }

    public boolean isGradering() {
        return gradering;
    }

    @Override
    public String toString() {
        return "UttakEgenskaper{" +
                "vurderSykdom=" + vurderSykdom +
                ", gradering=" + gradering +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UttakEgenskaper that = (UttakEgenskaper) o;
        return vurderSykdom == that.vurderSykdom &&
                gradering == that.gradering;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vurderSykdom, gradering);
    }
}
