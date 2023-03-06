package no.nav.foreldrepenger.los.reservasjon;

import java.util.List;

public final class ReservasjonKonstanter {

    private ReservasjonKonstanter() {
    }

    public static final String OPPGAVE_AVSLUTTET = "Oppgave avsluttet";
    public static final String RESERVASJON_VIDEREFØRT_NY_OPPGAVE = "Reservasjon flyttes til ny oppgave";
    public static final String RESERVASJON_AVSLUTTET_AVDELINGSLEDER = "Opphevet av avdelingsleder";
    public static final String NY_ENHET = "Flyttet til ny enhet";

    public static final String SLETTET_AV_ADMIN = "Oppgave er avsluttet fra admin REST-tjeneste";


    public static boolean tekstBlantReservasjonKonstanter(String kandidat) {
        final var aktuelle = List.of(OPPGAVE_AVSLUTTET, RESERVASJON_AVSLUTTET_AVDELINGSLEDER, NY_ENHET, SLETTET_AV_ADMIN, RESERVASJON_VIDEREFØRT_NY_OPPGAVE);
        return kandidat != null && aktuelle.contains(kandidat);
    }

}
