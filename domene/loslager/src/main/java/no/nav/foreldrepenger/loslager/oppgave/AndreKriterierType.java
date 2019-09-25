package no.nav.foreldrepenger.loslager.oppgave;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.fplos.kodeverk.Kodeliste;

@Entity(name = "AndreKriterierType")
@DiscriminatorValue(AndreKriterierType.DISCRIMINATOR)
public class AndreKriterierType extends Kodeliste {

    public static final String DISCRIMINATOR = "ANDRE_KRITERIER"; //$NON-NLS-1$
    public static final AndreKriterierType TIL_BESLUTTER = new AndreKriterierType("TIL_BESLUTTER"); //$NON-NLS-1$
    public static final AndreKriterierType PAPIRSØKNAD = new AndreKriterierType("PAPIRSOKNAD"); //$NON-NLS-1$
    public static final AndreKriterierType UTBETALING_TIL_BRUKER = new AndreKriterierType("UTBETALING_TIL_BRUKER"); //$NON-NLS-1$
    public static final AndreKriterierType UTLANDSSAK = new AndreKriterierType("UTLANDSSAK"); //$NON-NLS-1$
    public static final AndreKriterierType SOKT_GRADERING = new AndreKriterierType("SOKT_GRADERING"); //$NON-NLS-1$
    public static final AndreKriterierType UKJENT = new AndreKriterierType("-"); //$NON-NLS-1$
    public static final AndreKriterierType FEILUTBETALT_BELØP = new AndreKriterierType("FEILUTBETALT_BELØP");

    AndreKriterierType() {
        // Hibernate trenger den
    }

    protected AndreKriterierType(String kode) {
        super(kode, DISCRIMINATOR);
    }

}
