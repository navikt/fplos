package no.nav.fplos.kafkatjenester.genereltgrensesnitt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class OppgaveSaksbehandler {
    private String saksbehandler;

    @JsonCreator
    public OppgaveSaksbehandler(String saksbehandler) {
        this.saksbehandler = saksbehandler;
    }

    @JsonValue
    public String getSaksbehandler() {
        return saksbehandler;
    }

    @Override
    public String toString() {
        return "OppgaveSaksbehandler{" +
                "saksbehandler='" + saksbehandler + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OppgaveSaksbehandler that = (OppgaveSaksbehandler) o;

        return saksbehandler != null ? saksbehandler.equals(that.saksbehandler) : that.saksbehandler == null;
    }

    @Override
    public int hashCode() {
        return saksbehandler != null ? saksbehandler.hashCode() : 0;
    }
}
