package no.nav.fplos.ansatt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.loslager.aktør.OrganisasjonsEnhet;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepositoryImpl;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
class AnsattTjenesteTest {

    private AnsattTjeneste ansattTjeneste;
    private EnhetstilgangTjeneste enhetstilgangTjeneste;

    @BeforeEach
    public void setup(EntityManager entityManager) {
        OrganisasjonRepository organisasjonRepository = new OrganisasjonRepositoryImpl(entityManager);
        enhetstilgangTjeneste = mock(EnhetstilgangTjeneste.class);
        ansattTjeneste = new AnsattTjeneste(enhetstilgangTjeneste, organisasjonRepository);
    }

    @Test
    public void henter_kun_relevante_avdelingsnavn() {
        var aktuellEnhet = new OrganisasjonsEnhet(Avdeling.AVDELING_DRAMMEN_ENHET, "NAV Enhet 1", Set.of("FOR"));
        var uaktuellEnhet = new OrganisasjonsEnhet("0001", "NAV Uaktuell enhet", Set.of("FOR"));
        List<OrganisasjonsEnhet> organisasjonsEnheter = List.of(aktuellEnhet, uaktuellEnhet);
        when(enhetstilgangTjeneste.hentEnhetstilganger(any())).thenReturn(organisasjonsEnheter);
        var avdelingerNavn = ansattTjeneste.hentAvdelingerNavnForAnsatt("12345");
        assertThat(avdelingerNavn).containsExactly(aktuellEnhet.getEnhetNavn());
    }
}
