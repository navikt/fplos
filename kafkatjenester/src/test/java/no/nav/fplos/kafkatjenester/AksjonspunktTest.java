package no.nav.fplos.kafkatjenester;

import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.fplos.kafkatjenester.ForeldrepengerEventHåndtererTest.aksjonspunktDtoFra;

public class AksjonspunktTest {
    private final Map<String, String> dto = new HashMap<>();
    private final List<Aksjonspunkt> aksjonspunkt = new ArrayList<>();

    public Map<String, String> getDto() {
        return dto;
    }

    public List<Aksjonspunkt> getAksjonspunkt() {
        return aksjonspunkt;
    }

    public AksjonspunktTest() {
    }

    public AksjonspunktTest(String kode, KodeStatus status) {
        addKode(kode, status);
    }

    public void addKode(String kode, KodeStatus status) {
        dto.put(kode, status.name());
        aksjonspunkt.add(aksjonspunktDtoFra(kode, status.name(), LocalDateTime.now()));
    }

    enum KodeStatus {
        OPPR, AVBR, UTFO
    }
}
