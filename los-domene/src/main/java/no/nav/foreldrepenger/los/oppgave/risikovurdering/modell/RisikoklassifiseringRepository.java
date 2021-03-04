package no.nav.foreldrepenger.los.oppgave.risikovurdering.modell;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static no.nav.vedtak.felles.jpa.HibernateVerktøy.hentUniktResultat;

@ApplicationScoped
public class RisikoklassifiseringRepository {

    private EntityManager entityManager;

    RisikoklassifiseringRepository() {
        // for CDI proxy
    }

    @Inject
    public RisikoklassifiseringRepository( EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "entityManager"); //$NON-NLS-1$
        this.entityManager = entityManager;
    }

    public void lagreRisikoklassifisering(RisikoklassifiseringEntitet risikoklassifisering, BehandlingId behandlingId) {
        Objects.requireNonNull(risikoklassifisering, "risikoklassifisering");
        Objects.requireNonNull(risikoklassifisering.getBehandlingId(), "behandlingId");

        deaktiverGammeltGrunnlagOmNødvendig(behandlingId);

        lagre(risikoklassifisering);
    }


    public Optional<RisikoklassifiseringEntitet> hentRisikoklassifiseringForBehandling(UUID behandlingId) {
        TypedQuery<RisikoklassifiseringEntitet> query = entityManager.createQuery("from RisikoklassifiseringEntitet where behandlingId = :behandlingId and erAktiv = :erAktiv", RisikoklassifiseringEntitet.class);
        query.setParameter("behandlingId", behandlingId);
        query.setParameter("erAktiv", true);
        return hentUniktResultat(query);
    }

    private void lagre(RisikoklassifiseringEntitet risikoklassifisering) {
        risikoklassifisering.setErAktiv(true);
        entityManager.persist(risikoklassifisering);
        entityManager.flush();
    }

    private void deaktiverGammeltGrunnlagOmNødvendig(BehandlingId behandlingId) {
        Optional<RisikoklassifiseringEntitet> risikoklassifiseringEntitet = hentRisikoklassifiseringForBehandling(behandlingId.toUUID());
        risikoklassifiseringEntitet.ifPresent(this::deaktiverGrunnlag);
    }

    private void deaktiverGrunnlag(RisikoklassifiseringEntitet risikoklassifiseringEntitet) {
        risikoklassifiseringEntitet.setErAktiv(false);
        entityManager.persist(risikoklassifiseringEntitet);
        entityManager.flush();
    }

}
