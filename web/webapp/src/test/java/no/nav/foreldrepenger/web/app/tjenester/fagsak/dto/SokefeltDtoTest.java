package no.nav.foreldrepenger.web.app.tjenester.fagsak.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.dto.SokefeltDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

public class SokefeltDtoTest {

    @Test
    public void skal_ha_spesial_abac_type_når_det_er_et_fødslelsnummer_siden_alle_sakene_kan_være_knyttet_til_andre_parter() {
        var fnr = "07078518434";
        var dto = new SokefeltDto(fnr);

        assertThat(dto.abacAttributter()).isEqualTo(AbacDataAttributter.opprett()
                .leggTil(StandardAbacAttributtType.FNR, fnr)
                .leggTil(FplosAbacAttributtType.SAKER_MED_FNR, fnr)
        );
    }

    @Test
    public void skal_ha_normal_saksnummer_abac_type_når_det_ikke_er_et_fødslelsnummer() {
        var saksnummer  = new Saksnummer("123123123123");
        var dto = new SokefeltDto(saksnummer);

        assertThat(dto.abacAttributter()).isEqualTo(AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.SAKSNUMMER,saksnummer.value()));
    }
}
