package no.nav.fplos.verdikjedetester.mock;

import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDto;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class MeldingsTestInfo {

    private final Long behandlingId;
    private final Long saksnummer;
    private final UUID eksternId;
    //private final Boolean aktiv;
    private String aktørId;
    private final String behandlingTypeKode;
    private String ytelseTypeKode;
    private String behandlendeEnhet = "4806";

    private final String DEFAULT = "DEFAULT_TEST";
    //Der id-er ikke har noe å si genererer vi negative ID-er.
    private static Long DEFAULT_ID_COUNTER = -1L;


    MeldingsTestInfo(Long behandlingId, UUID eksternId, Long saksnummer, String aktørId, BehandlingType behandlingType, FagsakYtelseType ytelseType) {
        this.behandlingId = behandlingId;
        this.eksternId = eksternId;
        this.aktørId = aktørId;
        this.saksnummer = saksnummer;
        this.behandlingTypeKode = behandlingType.getKode();
        this.ytelseTypeKode = ytelseType.getKode();
    }

    MeldingsTestInfo(Long behandlingId, UUID eksternId, String aktørId){
        this.behandlingId = behandlingId;
        this.eksternId = eksternId;
        this.aktørId = aktørId;
        this.saksnummer = hentNesteId();
        behandlingTypeKode = BehandlingType.FØRSTEGANGSSØKNAD.getKode();
        this.ytelseTypeKode = FagsakYtelseType.FORELDREPENGER.getKode();
    }

    private Long hentNesteId() {
        Long temp = DEFAULT_ID_COUNTER;
        DEFAULT_ID_COUNTER--;
        return temp;
    }


    String tilmeldingstekst(){
        return "{ \"fagsystem\": \"FPSAK\", \"behandlingId\": "+behandlingId+",\"eksternId\": \""+eksternId+"\", \"saksnummer\": "+ saksnummer +", \"aktørId\":\""+ aktørId + "\", \"behandlendeEnhet\":\""+ behandlendeEnhet + "\", \"behandlingTypeKode\":\""+ behandlingTypeKode+"\", \"ytelseTypeKode\":\""+ ytelseTypeKode+"\"}";
    }

    public String getFagsystem(){ return "FPSAK"; }

    public String getBehandlendeEnhet() {
        return behandlendeEnhet;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public UUID getEksternId() {return eksternId;}

    public Long getSaksnummer() {
        return saksnummer;
    }

    public String getBehandlingTypeKode() {
        return behandlingTypeKode;
    }

    public String getAktørId() {
        return aktørId;
    }

    public String getYtelseTypeKode() {
        return ytelseTypeKode;
    }

    public void sammenligne(OppgaveDto oppgave) {
        assertThat(oppgave.getBehandlingId()).isEqualTo(behandlingId);
        assertThat(oppgave.getBehandlingstype().getKode()).isEqualTo(behandlingTypeKode);
    }
}
