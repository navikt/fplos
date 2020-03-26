package no.nav.fplos.foreldrepengerbehandling.dto.behandling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingDto {

    private Long id;
    private UUID uuid;
    private Long versjon;
    private BehandlingType type;
    private BehandlingStatus status;
    private BehandlingÅrsakDto førsteÅrsak;
    private Long fagsakId;
    private LocalDateTime opprettet;
    private LocalDateTime avsluttet;
    private LocalDateTime endret;
    private String endretAvBrukernavn;
    private String behandlendeEnhetId;
    private String behandlendeEnhetNavn;
    private boolean erAktivPapirsoknad = false;
    private LocalDate behandlingsfristTid;

    /**
     * REST HATEOAS - pekere på data innhold som hentes fra andre url'er, eller handlinger som er tilgjengelig på behandling.
     *
     * @see https://restfulapi.net/hateoas/
     */
    private List<ResourceLink> links = new ArrayList<>();
    public Long getFagsakId() {
        return fagsakId;
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Long getVersjon() {
        return versjon;
    }

    public BehandlingType getType() {
        return type;
    }

    public LocalDateTime getOpprettet() {
        return opprettet;
    }

    public LocalDateTime getAvsluttet() {
        return avsluttet;
    }

    public BehandlingStatus getStatus() {
        return status;
    }

    public LocalDateTime getEndret() {
        return endret;
    }

    public String getEndretAvBrukernavn() {
        return endretAvBrukernavn;
    }

    public String getBehandlendeEnhetId() {
        return behandlendeEnhetId;
    }

    public String getBehandlendeEnhetNavn() {
        return behandlendeEnhetNavn;
    }

    public boolean isErAktivPapirsoknad() {
        return erAktivPapirsoknad;
    }

    public LocalDate getBehandlingsfristTid() {
        return behandlingsfristTid;
    }

    public BehandlingÅrsakDto getFørsteÅrsak() {
        return førsteÅrsak;
    }

    public List<ResourceLink> getLinks() {
        return Collections.unmodifiableList(links);
    }

    void setFagsakId(Long fagsakId) {
        this.fagsakId = fagsakId;
    }

    void setId(Long id) {
        this.id = id;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    void setVersjon(Long versjon) {
        this.versjon = versjon;
    }

    void setType(BehandlingType type) {
        this.type = type;
    }

    void setOpprettet(LocalDateTime opprettet) {
        this.opprettet = opprettet;
    }

    void setEndret(LocalDateTime endret) {
        this.endret = endret;
    }

    void setEndretAvBrukernavn(String endretAvBrukernavn) {
        this.endretAvBrukernavn = endretAvBrukernavn;
    }

    void setAvsluttet(LocalDateTime avsluttet) {
        this.avsluttet = avsluttet;
    }

    void setStatus(BehandlingStatus status) {
        this.status = status;
    }

    void setBehandlendeEnhetId(String behandlendeEnhetId) {
        this.behandlendeEnhetId = behandlendeEnhetId;
    }

    void setBehandlendeEnhetNavn(String behandlendeEnhetNavn) {
        this.behandlendeEnhetNavn = behandlendeEnhetNavn;
    }

    void setErAktivPapirsøknad(boolean erAktivPapirsoknad) {
        this.erAktivPapirsoknad = erAktivPapirsoknad;
    }

    public void setFørsteÅrsak(BehandlingÅrsakDto førsteÅrsak) {
        this.førsteÅrsak = førsteÅrsak;
    }

    public void setBehandlingsfristTid(LocalDate behandlingsfristTid) {
        this.behandlingsfristTid = behandlingsfristTid;
    }
}
