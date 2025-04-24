package no.nav.foreldrepenger.los.oppgave.tilbudtoppgave;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Immutable
@Subselect("""
    select o2.behandling_id
    from oppgave o2
    where o2.aktiv = 'J'
    and not exists (
        select 1
            from reservasjon r
            where r.oppgave_id = o2.id
            and r.reservert_til > current_timestamp
    )
""")
public class BehandlingMedAktivOppgave {
    @Id
    @Column(name = "behandling_id")
    private BehandlingId behandlingId;

    public BehandlingId getBehandlingId() {
        return behandlingId;
    }
}
