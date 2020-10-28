package no.nav.fplos.foreldrepengerbehandling.dto.fagsak;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nav.foreldrepenger.loslager.oppgave.FagsakStatus;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.fplos.foreldrepengerbehandling.dto.behandling.ResourceLink;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FagsakDto {

    private Long saksnummer;
    private FagsakYtelseType sakstype;
    private FagsakStatus status;
    private LocalDate barnFodt;
    private List<ResourceLink> links;
    private List<ResourceLink> onceLinks;

    public FagsakDto() {
        // Injiseres i test
    }

    public FagsakDto(Long saksnummer, FagsakYtelseType sakstype, FagsakStatus status, LocalDate barnFodt, List<ResourceLink> links, List<ResourceLink> onceLinks) {
        this.saksnummer = saksnummer;
        this.sakstype = sakstype;
        this.status = status;
        this.barnFodt = barnFodt;
        this.links = links;
        this.onceLinks = onceLinks;
    }

    public Long getSaksnummer() {
        return saksnummer;
    }

    public FagsakYtelseType getSakstype() {
        return sakstype;
    }

    public FagsakStatus getStatus() {
        return status;
    }

    public LocalDate getBarnFodt() {
        return barnFodt;
    }

    public List<ResourceLink> getLinks() {
        return links;
    }

    public List<ResourceLink> getOnceLinks() {
        return onceLinks;
    }
}
