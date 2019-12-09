package no.nav.foreldrepenger.loslager.oppgave;

import no.nav.fplos.kodeverk.Kodeliste;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "KoSortering")
@DiscriminatorValue(KøSortering.DISCRIMINATOR)
public class KøSortering extends Kodeliste {

    public static final String DISCRIMINATOR = "KO_SORTERING";

    /**
     * Konstanter for å skrive ned kodeverdi. For å hente ut andre data konfigurert, må disse leses fra databasen (eks.
     * for å hente offisiell kode for et Nav kodeverk).
     */
    public static final KøSortering BEHANDLINGSFRIST = new KøSortering( "BEHFRIST");
    public static final KøSortering OPPRETT_BEHANDLING = new KøSortering( "OPPRBEH");
    public static final KøSortering FORSTE_STONADSDAG = new KøSortering( "FORSTONAD");
    public static final KøSortering BELOP = new KøSortering( "BELOP");

    /**
     * Alle kodeverk må ha en verdi, det kan ikke være null i databasen. Denne koden gjør samme nytten.
     */
    public static final KøSortering UDEFINERT = new KøSortering("-"); //$NON-NLS-1$

    KøSortering() {
        // Hibernate trenger den
    }

    protected KøSortering(String kode) {
        super(kode, DISCRIMINATOR);
    }
}
