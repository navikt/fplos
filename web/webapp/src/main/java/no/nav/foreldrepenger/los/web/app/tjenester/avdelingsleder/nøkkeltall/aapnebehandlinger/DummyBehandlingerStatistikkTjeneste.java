package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.aapnebehandlinger;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.aapnebehandlinger.dto.AapneBehandlerDto;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DummyBehandlingerStatistikkTjeneste {

    private static final List<AapneBehandlerDto> behandlinger = new ArrayList<>();

    static {
        behandlinger.add(fra("BT-004","N","202012",14));
        behandlinger.add(fra("BT-004","J","202007",4));
        behandlinger.add(fra("BT-004","J","202101",21));
        behandlinger.add(fra("BT-004","N","202001",1));
        behandlinger.add(fra("BT-002","J","201911",1));
        behandlinger.add(fra("BT-004","J","202010",4));
        behandlinger.add(fra("BT-004","J","202004",2));
        behandlinger.add(fra("BT-002","J","202101",170));
        behandlinger.add(fra("BT-002","J","202009",5));
        behandlinger.add(fra("BT-002","J","202011",23));
        behandlinger.add(fra("BT-004","N","",74));
        behandlinger.add(fra("BT-004","J","202012",12));
        behandlinger.add(fra("BT-002","J","202102",413));
        behandlinger.add(fra("BT-004","J","202009",2));
        behandlinger.add(fra("BT-002","N","202101",36));
        behandlinger.add(fra("BT-002","J","202006",2));
        behandlinger.add(fra("BT-002","J","202012",57));
        behandlinger.add(fra("BT-002","J","202007",2));
        behandlinger.add(fra("BT-002","J","202105",59));
        behandlinger.add(fra("BT-002","J","202003",2));
        behandlinger.add(fra("BT-002","J","",123));
    }

    public DummyBehandlingerStatistikkTjeneste() {
    }

    public List<AapneBehandlerDto> hentÅpneBehandlinger() {
        return behandlinger;
    }

    private static AapneBehandlerDto fra(String behandlingType, String status, String måned,
                    int antall) {
        return new AapneBehandlerDto(BehandlingType.fraKode(behandlingType),
                status.equals("J"), måned, antall);
    }
}
