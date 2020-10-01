package no.nav.foreldrepenger.los.web.app.tjenester.admin;

import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.dto.DriftsmeldingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.dto.DriftsmeldingOpprettelseDto;
import no.nav.foreldrepenger.loslager.repository.DriftsmeldingRepository;
import no.nav.fplos.admin.DriftsmeldingTjeneste;
import no.nav.fplos.admin.DriftsmeldingTjenesteImpl;
import no.nav.vedtak.felles.testutilities.db.EntityManagerAwareTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
class DriftsmeldingerRestTjenesteTest extends EntityManagerAwareTest {

    private DriftsmeldingerRestTjeneste driftsmeldinger;

    @BeforeEach
    public void setUp() {
        DriftsmeldingRepository driftsmeldingRepository = new DriftsmeldingRepository(getEntityManager());
        DriftsmeldingTjeneste driftsmeldingTjeneste = new DriftsmeldingTjenesteImpl(driftsmeldingRepository);
        driftsmeldinger = new DriftsmeldingerRestTjeneste(driftsmeldingTjeneste);
    }

    @Test
    public void opprettDriftsmelding() {
        Response response = opprettMelding();
        assertEquals(200, response.getStatus());
        List<DriftsmeldingDto> driftsmeldingerFraTjeneste = driftsmeldinger.hentAktiveDriftsmeldinger();
        assertEquals(1, driftsmeldingerFraTjeneste.size());

        DriftsmeldingDto driftsmeldingDto = driftsmeldingerFraTjeneste.get(0);
        assertEquals("Dette er en driftsmelding", driftsmeldingDto.getMelding());
        assertTrue(omtrentSammeTid(driftsmeldingDto.getAktivFra(), LocalDateTime.now()));
        assertTrue(omtrentSammeTid(driftsmeldingDto.getAktivTil(), LocalDateTime.now().plusHours(4)));
    }

    @Test
    public void deaktiverMeldinger() {
        opprettMelding();
        driftsmeldinger.deaktiverDriftsmeldinger();
        assertEquals(driftsmeldinger.hentAktiveDriftsmeldinger().size(), 0);
    }

    private Response opprettMelding() {
        DriftsmeldingOpprettelseDto opprettelseDto = new DriftsmeldingOpprettelseDto();
        opprettelseDto.setMelding("Dette er en driftsmelding");
        return driftsmeldinger.opprettDriftsmelding(opprettelseDto);
    }

    private static boolean omtrentSammeTid(LocalDateTime tid, LocalDateTime utgangspunkt) {
        return tid.isAfter(utgangspunkt.minusSeconds(4))
                && tid.isBefore(utgangspunkt.plusSeconds(4));
    }

}
