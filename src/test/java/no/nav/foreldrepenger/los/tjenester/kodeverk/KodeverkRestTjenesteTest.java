package no.nav.foreldrepenger.los.tjenester.kodeverk;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.los.oppgave.FagsakStatus;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;

class KodeverkRestTjenesteTest {

    @Test
    void skal_hente_kodeverk_og_gruppere_på_kodeverknavn() {

        var tjeneste = new KodeverkRestTjeneste();
        var response = tjeneste.hentGruppertKodeliste();

        var gruppertKodeliste = (Map<String, Collection<KodeverdiMedNavnDto>>)response.getEntity();

        assertThat(gruppertKodeliste).containsKeys(FagsakStatus.class.getSimpleName(), KøSortering.class.getSimpleName());

        var fagsakStatuser = gruppertKodeliste.get(FagsakStatus.class.getSimpleName());
        assertThat(fagsakStatuser.stream().map(k -> k.kode()).toList()).contains(FagsakStatus.AVSLUTTET.getKode(),
            FagsakStatus.OPPRETTET.getKode());
    }

}
