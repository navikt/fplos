package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import java.time.LocalDateTime;

public class FlyttetReservasjonDto {
    private LocalDateTime tidspunkt;
    private String uid;
    private String navn;
    private String begrunnelse;

    public FlyttetReservasjonDto(LocalDateTime tidspunkt,  String uid, String navn, String begrunnelse) {
        this.tidspunkt = tidspunkt;
        this.uid = uid;
        this.navn = navn;
        this.begrunnelse = begrunnelse;
    }

    public LocalDateTime getTidspunkt() {
        return tidspunkt;
    }

    public String getUid() {
        return uid;
    }

    public String getNavn() {
        return navn;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }
}
