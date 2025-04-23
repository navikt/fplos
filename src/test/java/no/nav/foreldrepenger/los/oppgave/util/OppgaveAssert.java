package no.nav.foreldrepenger.los.oppgave.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.assertj.core.api.AbstractAssert;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;

public class OppgaveAssert extends AbstractAssert<OppgaveAssert, Oppgave> {

    private OppgaveAssert(Oppgave actual) {
        super(actual, OppgaveAssert.class);
    }

    public static OppgaveAssert assertThatOppgave(Oppgave actual) {
        return new OppgaveAssert(actual);
    }

    public OppgaveAssert harSaksnummer(Saksnummer saksnummer) {
        isNotNull();
        assertThat(actual.getSaksnummer()).overridingErrorMessage("Forventet saksnummer <%s> men fikk <%s>", saksnummer,
            actual.getSaksnummer()).isEqualTo(saksnummer);
        return this;
    }

    public OppgaveAssert harSystem(String system) {
        isNotNull();
        assertThat(actual.getSystem()).overridingErrorMessage("Forventet system <%s> men fikk <%s>", system, actual.getSystem()).isEqualTo(system);
        return this;
    }

    public OppgaveAssert harBehandlingOpprettet(LocalDateTime behandlingOpprettet) {
        isNotNull();
        var faktisk = actual.getBehandlingOpprettet().truncatedTo(ChronoUnit.SECONDS);
        assertThat(faktisk).overridingErrorMessage("Forventet behandlingOpprettet <%s> men fikk <%s>", behandlingOpprettet, faktisk)
            .isEqualTo(behandlingOpprettet.truncatedTo(ChronoUnit.SECONDS));
        return this;
    }

    public OppgaveAssert harAktiv(boolean aktiv) {
        isNotNull();
        assertThat(actual.getAktiv()).overridingErrorMessage("Forventet aktiv <%s> men fikk <%s>", aktiv, actual.getAktiv()).isEqualTo(aktiv);
        return this;
    }

    public OppgaveAssert harBehandlingId(BehandlingId behandlingId) {
        isNotNull();
        assertThat(actual.getBehandlingId()).overridingErrorMessage("Forventet behandlingId <%s> men fikk <%s>", behandlingId,
            actual.getBehandlingId()).isEqualTo(behandlingId);
        return this;
    }

    public OppgaveAssert harBehandlingType(BehandlingType behandlingType) {
        isNotNull();
        assertThat(actual.getBehandlingType()).overridingErrorMessage("Forventet behandlingType <%s> men fikk <%s>", behandlingType,
            actual.getBehandlingType()).isEqualTo(behandlingType);
        return this;
    }

    public OppgaveAssert harBehandlingsfrist(LocalDateTime behandlingsFrist) {
        isNotNull();
        assertThat(actual.getBehandlingsfrist()).overridingErrorMessage("Forventet behandlingsFrist <%s> men fikk <%s>", behandlingsFrist,
            actual.getBehandlingsfrist()).isEqualTo(behandlingsFrist);
        return this;
    }

    public OppgaveAssert harAktørId(AktørId aktørId) {
        isNotNull();
        assertThat(actual.getAktørId()).overridingErrorMessage("Forventet aktørId <%s> men fikk <%s>", aktørId, actual.getAktørId())
            .isEqualTo(aktørId);
        return this;
    }

    public OppgaveAssert harFørsteStønadsdag(LocalDate førsteStønadsdag) {
        isNotNull();
        assertThat(actual.getFørsteStønadsdag()).overridingErrorMessage("Forventet førsteStønadsdag <%s> men fikk <%s>", førsteStønadsdag,
            actual.getFørsteStønadsdag()).isEqualTo(førsteStønadsdag);
        return this;
    }

    public OppgaveAssert harOppgaveAvsluttet(LocalDateTime oppgaveAvsluttet) {
        isNotNull();
        assertThat(actual.getOppgaveAvsluttet()).overridingErrorMessage("Forventet oppgaveAvsluttet <%s> men fikk <%s>", oppgaveAvsluttet,
            actual.getOppgaveAvsluttet()).isEqualTo(oppgaveAvsluttet);
        return this;
    }

    public OppgaveAssert harBehandlingStatus(BehandlingStatus behandlingStatus) {
        isNotNull();
        assertThat(actual.getBehandlingStatus()).overridingErrorMessage("Forventet behandlingStatus <%s> men fikk <%s>", behandlingStatus,
            actual.getBehandlingStatus()).isEqualTo(behandlingStatus);
        return this;
    }

    public OppgaveAssert harBehandlendeEnhet(String behandlendeEnhet) {
        isNotNull();
        assertThat(actual.getBehandlendeEnhet()).overridingErrorMessage("Forventet behandlendeEnhet <%s> men fikk <%s>", behandlendeEnhet,
            actual.getBehandlendeEnhet()).isEqualTo(behandlendeEnhet);
        return this;
    }

    public OppgaveAssert harFagsakYtelseType(FagsakYtelseType fagsakYtelseType) {
        isNotNull();
        assertThat(actual.getFagsakYtelseType()).overridingErrorMessage("Forventet fagsakYtelseType <%s> men fikk <%s>", fagsakYtelseType,
            actual.getFagsakYtelseType()).isEqualTo(fagsakYtelseType);
        return this;
    }

}
