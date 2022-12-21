package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.util.UUID;

import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

public interface BehandlingKlient {

    LosBehandlingDto hentLosBehandlingDto(UUID uuid);
}
