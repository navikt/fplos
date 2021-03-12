package no.nav.foreldrepenger.los.web.app.tjenester.admin;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.admin.driftsmelding.DriftsmeldingRepository;
import no.nav.foreldrepenger.los.admin.driftsmelding.DriftsmeldingTjenesteImpl;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.dto.DriftsmeldingOpprettelseDto;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
class DriftsmeldingerRestTjenesteTest {

    private DriftsmeldingerRestTjeneste driftsmeldinger;

    @BeforeEach
    public void setUp(EntityManager entityManager) {
        var driftsmeldingRepository = new DriftsmeldingRepository(entityManager);
        var driftsmeldingTjeneste = new DriftsmeldingTjenesteImpl(driftsmeldingRepository);
        driftsmeldinger = new DriftsmeldingerRestTjeneste(driftsmeldingTjeneste);
    }

    @Test
    public void opprettDriftsmelding() {
        var response = opprettMelding();
        assertThat(response.getStatus()).isEqualTo(200);
        var driftsmeldingerFraTjeneste = driftsmeldinger.hentAktiveDriftsmeldinger();
        assertThat(driftsmeldingerFraTjeneste).hasSize(1);

        var driftsmeldingDto = driftsmeldingerFraTjeneste.get(0);
        assertThat(driftsmeldingDto.getMelding()).isEqualTo("Dette er en driftsmelding");
        assertThat(omtrentSammeTid(driftsmeldingDto.getAktivFra(), LocalDateTime.now())).isTrue();
        assertThat(omtrentSammeTid(driftsmeldingDto.getAktivTil(), LocalDateTime.now().plusHours(4))).isTrue();
    }

    @Test
    public void deaktiverMeldinger() {
        opprettMelding();
        driftsmeldinger.deaktiverDriftsmeldinger();
        assertThat(driftsmeldinger.hentAktiveDriftsmeldinger()).isEmpty();
    }

    private Response opprettMelding() {
        var opprettelseDto = new DriftsmeldingOpprettelseDto();
        opprettelseDto.setMelding("Dette er en driftsmelding");
        return driftsmeldinger.opprettDriftsmelding(opprettelseDto);
    }

    private static boolean omtrentSammeTid(LocalDateTime tid, LocalDateTime utgangspunkt) {
        return tid.isAfter(utgangspunkt.minusSeconds(4))
                && tid.isBefore(utgangspunkt.plusSeconds(4));
    }

}
