package no.nav.fplos.kafkatjenester;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.hendelse.Fagsystem;
import no.nav.foreldrepenger.loslager.hendelse.Hendelse;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;

@ApplicationScoped
public class ForeldrepengerHendelseOppretter implements HendelseOppretter<FpsakBehandlingProsessEventDto> {


    @Override
    public Hendelse opprett(FpsakBehandlingProsessEventDto dto) {
        var hendelse = new Hendelse();
        hendelse.setBehandlendeEnhet(dto.getBehandlendeEnhet());
        hendelse.setYtelseType(FagsakYtelseType.fraKode(dto.getYtelseTypeKode()));
        hendelse.setFagsystem(Fagsystem.valueOf(dto.getFagsystem().name()));
        hendelse.setBehandlingType(BehandlingType.fraKode(dto.getBehandlingTypeKode()));
        hendelse.setSaksnummer(dto.getSaksnummer());
        hendelse.setBehandlingOpprettetTidspunkt(dto.getOpprettetBehandling());
        hendelse.setBehandlingId(new BehandlingId(dto.getEksternId()));
        hendelse.setAktørId(dto.getAktørId());
        return hendelse;
    }
}
