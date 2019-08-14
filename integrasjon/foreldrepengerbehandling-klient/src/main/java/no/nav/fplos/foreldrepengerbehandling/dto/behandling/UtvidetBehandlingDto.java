package no.nav.fplos.foreldrepengerbehandling.dto.behandling;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UtvidetBehandlingDto extends BehandlingDto {

    @JsonProperty("behandlingPaaVent")
    private boolean behandlingPåVent;

    @JsonProperty("behandlingKoet")
    private boolean behandlingKøet;

    @JsonProperty("ansvarligSaksbehandler")
    private String ansvarligSaksbehandler;

    @JsonProperty("ansvarligBeslutter")
    private String ansvarligBeslutter;

    @JsonProperty("fristBehandlingPaaVent")
    private String fristBehandlingPåVent;

    @JsonProperty("venteArsakKode")
    private String venteÅrsakKode;

/*
    @JsonProperty("sprakkode")
    private Språkkode språkkode;
*/

    @JsonProperty("behandlingHenlagt")
    private boolean behandlingHenlagt;

    @JsonProperty("toTrinnsBehandling")
    private boolean toTrinnsBehandling;

/*
    @JsonProperty("behandlingsresultat")
    private BehandlingsresultatDto behandlingsresultat;
*/

/*
    */
/** Eventuelt async status på tasks. *//*

    @JsonProperty("taskStatus")
    private AsyncPollingStatus taskStatus;
*/

/*
    @JsonProperty("behandlingArsaker")
    private List<BehandlingÅrsakDto> behandlingÅrsaker;
*/

    public boolean isBehandlingPåVent() {
        return behandlingPåVent;
    }

    public String getAnsvarligSaksbehandler() {
        return ansvarligSaksbehandler;
    }

    public String getAnsvarligBeslutter() {
        return ansvarligBeslutter;
    }

/*
    public AsyncPollingStatus getTaskStatus() {
        return taskStatus;
    }
*/

    public String getFristBehandlingPåVent() {
        return fristBehandlingPåVent;
    }

    public String getVenteÅrsakKode() {
        return venteÅrsakKode;
    }

/*
    public Språkkode getSpråkkode() {
        return språkkode;
    }
*/

    public boolean isBehandlingHenlagt() {
        return behandlingHenlagt;
    }

    public boolean getToTrinnsBehandling() {
        return toTrinnsBehandling;
    }

    void setBehandlingPåVent(boolean behandlingPåVent) {
        this.behandlingPåVent = behandlingPåVent;
    }

    void setAnsvarligSaksbehandler(String ansvarligSaksbehandler) {
        this.ansvarligSaksbehandler = ansvarligSaksbehandler;
    }

    public void setAnsvarligBeslutter(String ansvarligBeslutter) {
        this.ansvarligBeslutter = ansvarligBeslutter;
    }

    void setFristBehandlingPåVent(String fristBehandlingPåVent) {
        this.fristBehandlingPåVent = fristBehandlingPåVent;
    }

    void setVenteÅrsakKode(String venteÅrsakKode) {
        this.venteÅrsakKode = venteÅrsakKode;
    }

/*
    void setSpråkkode(Språkkode språkkode) {
        this.språkkode = språkkode;
    }
*/

    void setBehandlingHenlagt(boolean behandlingHenlagt) {
        this.behandlingHenlagt = behandlingHenlagt;
    }

    void setToTrinnsBehandling(boolean toTrinnsBehandling) {
        this.toTrinnsBehandling = toTrinnsBehandling;
    }

/*
    public void setAsyncStatus(AsyncPollingStatus asyncStatus) {
        this.taskStatus = asyncStatus;
    }
*/

/*
    public List<BehandlingÅrsakDto> getBehandlingÅrsaker() {
        return behandlingÅrsaker;
    }
*/

/*
    void setBehandlingArsaker(List<BehandlingÅrsakDto> behandlingÅrsaker) {
        this.behandlingÅrsaker = behandlingÅrsaker;
    }
*/

/*
    void setBehandlingsresultatDto(BehandlingsresultatDto behandlingsresultatDto) {
        this.behandlingsresultat = behandlingsresultatDto;
    }
*/

/*
    public BehandlingsresultatDto getBehandlingsresultat() {
        return behandlingsresultat;
    }
*/

    public boolean isBehandlingKoet() {
        return behandlingKøet;
    }

    public void setBehandlingKøet(boolean behandlingKøet) {
        this.behandlingKøet = behandlingKøet;
    }
}

