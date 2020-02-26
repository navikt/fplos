package no.nav.fplos.kafkatjenester;

import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;

public interface EventHåndterer<T extends BehandlingProsessEventDto> {
    void håndterEvent(T dto);
}
