package no.nav.foreldrepenger.los.tjenester.felles.dto;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;
import no.nav.foreldrepenger.los.organisasjon.ansatt.BrukerProfil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(JpaExtension.class)
class SaksbehandlerDtoTjenesteTest {

    private SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste;
    private AnsattTjeneste ansattTjeneste;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        var organisasjonRepository = new OrganisasjonRepository(entityManager);
        ansattTjeneste = mock(AnsattTjeneste.class);
        saksbehandlerDtoTjeneste = new SaksbehandlerDtoTjeneste(organisasjonRepository, ansattTjeneste);
    }

    @Test
    void testHentSaksbehandlerSomIkkeFinnesILos() {
        var saksbehandler1Ident = "Z999999";

        when(ansattTjeneste.hentBrukerProfil(saksbehandler1Ident)).thenReturn(Optional.of(new BrukerProfil(saksbehandler1Ident, "Navn Navnesen", "Avdelingsnavnet")));
        var saksbehandlerDto = saksbehandlerDtoTjeneste.saksbehandlerDtoForNavIdent(saksbehandler1Ident);

        assertThat(saksbehandlerDto).isPresent().hasValueSatisfying(dto -> {
            assertThat(dto.brukerIdent()).isEqualTo(saksbehandler1Ident);
            assertThat(dto.navn()).isEqualTo("Navn Navnesen");
            assertThat(dto.ansattAvdeling()).isEqualTo("Avdelingsnavnet");
        });
    }
}
