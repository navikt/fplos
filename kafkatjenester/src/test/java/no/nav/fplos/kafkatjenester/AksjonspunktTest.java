package no.nav.fplos.kafkatjenester;

import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.fplos.kafkatjenester.ForeldrepengerEventHåndtererTest.aksjonspunktDtoFra;

public class AksjonspunktTest {
    private Map<String, String> dto = new HashMap<>();
    private List<Aksjonspunkt> aksjonspunkt = new ArrayList<>();

    public Map<String, String> getDto() {
        return dto;
    }

    public List<Aksjonspunkt> getAksjonspunkt() {
        return aksjonspunkt;
    }

    public AksjonspunktTest() {
    }

    public AksjonspunktTest(int kode, KodeStatus status) {
        addKode(String.valueOf(kode), status);
    }

    private void addKode(String kode, KodeStatus status) {
        dto.put(kode, status.name());
        aksjonspunkt.add(aksjonspunktDtoFra(kode, status.name(), LocalDateTime.now()));
    }

    public void addOpprettet(int kode) {
        addKode(String.valueOf(kode), KodeStatus.OPPR);
    }

    public void addUtført(int kode) {
        addKode(String.valueOf(kode), KodeStatus.UTFO);
    }

    public void addAvbrutt(int kode) {
        addKode(String.valueOf(kode), KodeStatus.AVBR);
    }

    enum KodeStatus {
        OPPR, AVBR, UTFO
    }

    public static final class Builder {
        private AksjonspunktTest tmp = new AksjonspunktTest();

        private Builder() {
        }

        public static Builder aksjonspunktTestBuilder() {
            return new Builder();
        }

        public Builder medOpprettet(int kode) {
            tmp.addOpprettet(kode);
            return this;
        }

        public Builder medUtført(int kode) {
            tmp.addUtført(kode);
            return this;
        }

        public Builder medAvbrutt(int kode) {
            tmp.addAvbrutt(kode);
            return this;
        }

        public AksjonspunktTest build() {
            AksjonspunktTest aksjonspunktTest = new AksjonspunktTest();
            aksjonspunktTest.aksjonspunkt = tmp.aksjonspunkt;
            aksjonspunktTest.dto = tmp.dto;
            tmp = null;
            return aksjonspunktTest;
        }
    }
}
