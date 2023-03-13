package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.util.UUID;

import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;

public interface BehandlingKlient {

    LosBehandlingDto hentLosBehandlingDto(UUID uuid);

    default LosFagsakEgenskaperDto hentLosFagsakEgenskaperDto(Saksnummer saksnummer) {
        throw new IllegalStateException("Utviklerfeil - kall på metode som ikke er implementert");
    }
}
