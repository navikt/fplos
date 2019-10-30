package no.nav.fplos.verdikjedetester.mock;

import static org.assertj.core.api.Assertions.assertThat;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDto;

public class MeldingsTestInfo {

    private final Long behandlingId;
    private final String saksnummer;
    private final String fagsystem = "FPSAK";
    private final String behandlingTypeKode;
    private String behandlendeEnhet;
    private String fagsakYtelseType;

    private final String DEFAULT = "DEFAULT_TEST";
    //Der id-er ikke har noe å si genererer vi negative ID-er.
    private static Long DEFAULT_ID_COUNTER = -1L;


    MeldingsTestInfo(Long behandlingId, String saksnummer, BehandlingType behandlingTypeKode) {
        this.behandlingId = behandlingId;
        this.saksnummer = saksnummer;
        this.behandlingTypeKode = behandlingTypeKode.getKode();
    }

    MeldingsTestInfo(Long behandlingId){
        this.behandlingId = behandlingId;
        this.saksnummer = hentNesteId().toString();
        //behandlingsfrist = LocalDateTime.now().truncatedTo(MILLIS);
        //aktiv = Boolean.TRUE;
        behandlingTypeKode = BehandlingType.FØRSTEGANGSSØKNAD.getKode();
    }

    private Long hentNesteId() {
        Long temp = DEFAULT_ID_COUNTER;
        DEFAULT_ID_COUNTER--;
        return temp;
    }


    String tilmeldingstekst(){
        return "{ \"behandlingId\": "+behandlingId +
                ", \"fagsystem\": "+  "\"" + fagsystem + "\"" +
                ", \"saksnummer\": "+saksnummer+
                //",\"behandlingsfrist\": \"" + behandlingsfrist +
                //"\",\"aktiv\": \""+aktiv.toString()+
                ", \"behandlingTypeKode\":\"" + behandlingTypeKode + "\"}";
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public String getFagsakId() {
        return saksnummer;
    }

    public String getBehandlingTypeKode() {
        return behandlingTypeKode;
    }

    public void sammenligne(OppgaveDto oppgave) {
        assertThat(oppgave.getBehandlingId()).isEqualTo(behandlingId);
        assertThat(oppgave.getBehandlingstype()).isEqualTo(behandlingTypeKode);
    }
}
