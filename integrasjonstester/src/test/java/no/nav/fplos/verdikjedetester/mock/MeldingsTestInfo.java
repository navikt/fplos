package no.nav.fplos.verdikjedetester.mock;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDto;

public class MeldingsTestInfo {

    private final Long behandlingId;
    private final Long fagsakId;
    private final LocalDateTime behandlingsfrist;
    private final Boolean aktiv;
    private final BehandlingType behandlingType;
    private String avdeling;
    private String fagsakYtelseType;

    private final String DEFAULT = "DEFAULT_TEST";
    //Der id-er ikke har noe å si genererer vi negative ID-er.
    private static Long DEFAULT_ID_COUNTER = -1L;


    MeldingsTestInfo(Long behandlingId, Long fagsakId, LocalDateTime behandlingsfrist, Boolean aktiv, BehandlingType behandlingType) {
        this.behandlingId = behandlingId;
        this.fagsakId = fagsakId;
        this.behandlingsfrist = behandlingsfrist.truncatedTo(MILLIS);
        this.aktiv = aktiv;
        this.behandlingType = behandlingType;
    }

    MeldingsTestInfo(Long behandlingId){
        this.behandlingId = behandlingId;
        this.fagsakId = hentNesteId();
        behandlingsfrist = LocalDateTime.now().truncatedTo(MILLIS);
        aktiv = Boolean.TRUE;
        behandlingType = BehandlingType.FØRSTEGANGSSØKNAD;
    }

    private Long hentNesteId() {
        Long temp = DEFAULT_ID_COUNTER;
        DEFAULT_ID_COUNTER--;
        return temp;
    }


    String tilmeldingstekst(){
        return "{ \"behandlingId\": "+behandlingId+", \"fagsakId\": "+fagsakId+",\"behandlingsfrist\": \"" + behandlingsfrist + "\",\"aktiv\": \""+aktiv.toString()+"\", \"behandlingType\":\""+behandlingType.getKode()+"\"}";
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public Long getFagsakId() {
        return fagsakId;
    }

    public LocalDateTime getBehandlingsfrist() {
        return behandlingsfrist;
    }

    public Boolean getAktiv() {
        return aktiv;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public void sammenligne(OppgaveDto oppgave) {
        assertThat(oppgave.getBehandlingId()).isEqualTo(behandlingId);
        assertThat(oppgave.getBehandlingsfrist()).isEqualTo(behandlingsfrist);
        assertThat(oppgave.getBehandlingstype()).isEqualTo(behandlingType);
    }
}
