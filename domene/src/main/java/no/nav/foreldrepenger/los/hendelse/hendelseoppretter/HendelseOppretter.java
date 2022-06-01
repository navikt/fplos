package no.nav.foreldrepenger.los.hendelse.hendelseoppretter;

import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Hendelse;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;

public interface HendelseOppretter<T extends BehandlingProsessEventDto> {
    Hendelse opprett(T dto);
}
