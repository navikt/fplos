package no.nav.fplos.foreldrepengerbehandling.dto;

import java.util.Set;

public class PipDto {

    private Set<String> aktørIder;
    private String fagsakStatus;
    private String behandlingStatus;


    public Set<String> getAktørIder() {
        return aktørIder;
    }

    public void setAktørIder(Set<String> aktørIder) {
        this.aktørIder = aktørIder;
    }

    public String getFagsakStatus() {
        return fagsakStatus;
    }

    public void setFagsakStatus(String fagsakStatus) {
        this.fagsakStatus = fagsakStatus;
    }

    public String getBehandlingStatus() {
        return behandlingStatus;
    }

    public void setBehandlingStatus(String behandlingStatus) {
        this.behandlingStatus = behandlingStatus;
    }

    public boolean hasValues() {
        return getAktørIder() != null || getBehandlingStatus() != null || getFagsakStatus() != null;
    }
}
