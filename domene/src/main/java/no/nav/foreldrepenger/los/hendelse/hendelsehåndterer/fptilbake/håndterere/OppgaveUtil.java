package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere;

import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.TilbakekrevingHendelse;
import no.nav.foreldrepenger.los.oppgave.TilbakekrevingOppgave;

import java.time.LocalDate;
import java.util.Optional;

public class OppgaveUtil {

    private OppgaveUtil() {
    }

    public static TilbakekrevingOppgave oppgaveFra(TilbakekrevingHendelse hendelse) {
        var feilutbetalingStart = Optional.ofNullable(hendelse.getFørsteFeilutbetalingDato())
                .map(LocalDate::atStartOfDay)
                .orElse(null);
        return TilbakekrevingOppgave.tbuilder()
                .medBeløp(hendelse.getFeilutbetaltBeløp())
                .medFeilutbetalingStart(feilutbetalingStart)
                .medHref(hendelse.getHref())
                .medSystem(hendelse.getFagsystem().name())
                .medFagsakSaksnummer(Long.valueOf(hendelse.getSaksnummer()))
                .medAktorId(new AktørId(hendelse.getAktørId()))
                .medBehandlendeEnhet(hendelse.getBehandlendeEnhet())
                .medBehandlingType(hendelse.getBehandlingType())
                .medFagsakYtelseType(hendelse.getYtelseType())
                .medAktiv(true)
                .medBehandlingOpprettet(hendelse.getBehandlingOpprettetTidspunkt())
                .medUtfortFraAdmin(false)
                .medBehandlingId(hendelse.getBehandlingId())
                .build();
    }
}
