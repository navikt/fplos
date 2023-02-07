package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.vedtak.hendelser.behandling.AktørId;
import no.nav.vedtak.hendelser.behandling.Behandlingsstatus;
import no.nav.vedtak.hendelser.behandling.Behandlingstype;
import no.nav.vedtak.hendelser.behandling.Behandlingsårsak;
import no.nav.vedtak.hendelser.behandling.Kildesystem;
import no.nav.vedtak.hendelser.behandling.Ytelse;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FpsakOppgaveEgenskapFinnerTest {

    @Test
    void skalHaKlagePåTilbakebetalingEgenskapVedÅrsakKlageTilbakebetaling() {
        var dto = lagLosBehandlingDto();
        var fpsakEgenskaper = new FpsakOppgaveEgenskapFinner(dto);
        assertThat(fpsakEgenskaper.getAndreKriterier()).containsExactly(AndreKriterierType.KLAGE_PÅ_TILBAKEBETALING);
    }

    public static LosBehandlingDto lagLosBehandlingDto() {
        return new LosBehandlingDto(UUID.randomUUID(),
                Kildesystem.FPSAK,
                "42",
                Ytelse.FORELDREPENGER,
                new AktørId("1234"),
                Behandlingstype.KLAGE,
                Behandlingsstatus.UTREDES,
                LocalDateTime.now(),
                "0001",
                LocalDate.now(),
                "z999999",
                List.of(),
                List.of(Behandlingsårsak.KLAGE_TILBAKEBETALING),
                false,
                true,
                null,
                null);
    }

}