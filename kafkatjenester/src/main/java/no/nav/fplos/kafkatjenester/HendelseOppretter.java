package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.hendelse.Hendelse;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;

public interface HendelseOppretter<T extends BehandlingProsessEventDto> {
    Hendelse opprett(T dto);
}
