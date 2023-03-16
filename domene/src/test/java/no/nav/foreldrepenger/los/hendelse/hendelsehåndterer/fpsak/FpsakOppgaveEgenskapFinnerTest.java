package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto.UtlandMarkering;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.vedtak.hendelser.behandling.AktørId;
import no.nav.vedtak.hendelser.behandling.Behandlingsstatus;
import no.nav.vedtak.hendelser.behandling.Behandlingstype;
import no.nav.vedtak.hendelser.behandling.Behandlingsårsak;
import no.nav.vedtak.hendelser.behandling.Kildesystem;
import no.nav.vedtak.hendelser.behandling.Ytelse;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto.LosAksjonspunktDto;

class FpsakOppgaveEgenskapFinnerTest {

    @Test
    void skalHaKlagePåTilbakebetalingEgenskapVedÅrsakKlageTilbakebetaling() {
        var dto = lagLosBehandlingDto(null);
        var fpsakEgenskaper = new FpsakOppgaveEgenskapFinner(dto);
        assertThat(fpsakEgenskaper.getAndreKriterier()).contains(AndreKriterierType.KLAGE_PÅ_TILBAKEBETALING);
    }

    @Test
    void skalFåEgenskapEøsSakNårMarkertSomEøs() {
        var eøsFagsakEgenskaperDto = new LosFagsakEgenskaperDto(Boolean.TRUE, UtlandMarkering.EØS_BOSATT_NORGE);
        var eøsBehandlingDto = lagLosBehandlingDto(eøsFagsakEgenskaperDto);
        var eøsFpsakEgenskaper = new FpsakOppgaveEgenskapFinner(eøsBehandlingDto);
        assertThat(eøsFpsakEgenskaper.getAndreKriterier()).contains(AndreKriterierType.EØS_SAK);

        var ikkeEøsFagsakEgenskaperDto = new LosFagsakEgenskaperDto(Boolean.TRUE, UtlandMarkering.BOSATT_UTLAND);
        var ikkeEøsBehandlingDto = lagLosBehandlingDto(ikkeEøsFagsakEgenskaperDto);
        var ikkeEøsFpsakEgenskaper = new FpsakOppgaveEgenskapFinner(ikkeEøsBehandlingDto);
        assertThat(ikkeEøsFpsakEgenskaper.getAndreKriterier()).isNotNull().doesNotContain(AndreKriterierType.EØS_SAK);
    }

    static LosBehandlingDto lagLosBehandlingDto(LosFagsakEgenskaperDto fagsakEgenskaperDto, LosAksjonspunktDto... dto) {
        return new LosBehandlingDto(UUID.randomUUID(), Kildesystem.FPSAK, "42", Ytelse.FORELDREPENGER, new AktørId("1234"), Behandlingstype.KLAGE,
            Behandlingsstatus.UTREDES, LocalDateTime.now(), "0001", LocalDate.now(), "z999999", List.of(dto),
            List.of(Behandlingsårsak.KLAGE_TILBAKEBETALING), false, true, fagsakEgenskaperDto,
            new LosBehandlingDto.LosForeldrepengerDto(LocalDate.now(), true, false, false), null);
    }

}
