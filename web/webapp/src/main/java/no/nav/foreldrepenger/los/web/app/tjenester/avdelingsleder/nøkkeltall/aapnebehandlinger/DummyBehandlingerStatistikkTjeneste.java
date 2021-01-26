package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.aapnebehandlinger;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.aapnebehandlinger.dto.AapneBehandlerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.aapnebehandlinger.dto.BehandlingVenteStatus;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DummyBehandlingerStatistikkTjeneste {

    private static final List<AapneBehandlerDto> behandlinger = new ArrayList<>();

    static {
        behandlinger.add(dtoFra("BT-004","PÅ_VENT","202012",14));
        behandlinger.add(dtoFra("BT-004","AKTIV","202007",4));
        behandlinger.add(dtoFra("BT-004","AKTIV","202101",21));
        behandlinger.add(dtoFra("BT-004","PÅ_VENT","202001",1));
        behandlinger.add(dtoFra("BT-002","AKTIV","201911",1));
        behandlinger.add(dtoFra("BT-004","AKTIV","202010",4));
        behandlinger.add(dtoFra("BT-004","AKTIV","202004",2));
        behandlinger.add(dtoFra("BT-002","AKTIV","202101",170));
        behandlinger.add(dtoFra("BT-002","AKTIV","202009",5));
        behandlinger.add(dtoFra("BT-002","AKTIV","202011",23));
        behandlinger.add(dtoFra("BT-004","PÅ_VENT","",74));
        behandlinger.add(dtoFra("BT-004","AKTIV","202012",12));
        behandlinger.add(dtoFra("BT-002","AKTIV","202102",413));
        behandlinger.add(dtoFra("BT-004","AKTIV","202009",2));
        behandlinger.add(dtoFra("BT-002","PÅ_VENT","202101",36));
        behandlinger.add(dtoFra("BT-002","AKTIV","202006",2));
        behandlinger.add(dtoFra("BT-002","AKTIV","202012",57));
        behandlinger.add(dtoFra("BT-002","AKTIV","202007",2));
        behandlinger.add(dtoFra("BT-002","AKTIV","202105",59));
        behandlinger.add(dtoFra("BT-002","AKTIV","202003",2));
        behandlinger.add(dtoFra("BT-002","AKTIV","",123));
    }

    public DummyBehandlingerStatistikkTjeneste() {
    }

    public List<AapneBehandlerDto> hentÅpneBehandlinger() {
        return behandlinger;
    }

    private static AapneBehandlerDto dtoFra(String behandlingType, String status, String måned,
                                            int antall) {
        var dato = dato(måned);
        return new AapneBehandlerDto(BehandlingType.fraKode(behandlingType),
                BehandlingVenteStatus.fraKode(status), dato, antall);
    }

    private static LocalDate dato(String måned) {
        if (måned == null || måned.equals("")) return null;
        var månedInput = Integer.parseInt(måned.substring(4));
        var årInput = Integer.parseInt(måned.substring(0,4));
        return LocalDate.of(årInput, månedInput, 1);
    }
}
