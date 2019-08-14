package no.nav.foreldrepenger.loslager.oppgave;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.fplos.kodeverk.Kodeliste;

@Entity(name = "FagsakYtelseType")
@DiscriminatorValue(FagsakYtelseType.DISCRIMINATOR)
public class FagsakYtelseType extends Kodeliste {

    public static final String DISCRIMINATOR = "FAGSAK_YTELSE"; //$NON-NLS-1$
    public static final FagsakYtelseType ENGANGSTØNAD = new FagsakYtelseType("ES"); //$NON-NLS-1$
    public static final FagsakYtelseType FORELDREPENGER = new FagsakYtelseType("FP"); //$NON-NLS-1$
    public static final FagsakYtelseType SVANGERSKAPSPENGER = new FagsakYtelseType("SVP"); //$NON-NLS-1$

    public static final FagsakYtelseType UDEFINERT = new FagsakYtelseType("-"); //$NON-NLS-1$

    FagsakYtelseType() {
        // Hibernate trenger den
    }

    protected FagsakYtelseType(String kode) {
        super(kode, DISCRIMINATOR);
    }

}
