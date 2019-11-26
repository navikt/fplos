package no.nav.foreldrepenger.loslager.oppgave;

import no.nav.fplos.kodeverk.Kodeliste;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Arrays;
import java.util.List;

@Entity(name = "oppgaveEventType")
@DiscriminatorValue(OppgaveEventType.DISCRIMINATOR)
public class OppgaveEventType extends Kodeliste {

    public static final String DISCRIMINATOR = "OPPGAVE_EVENT_TYPE"; //$NON-NLS-1$

    public static final OppgaveEventType OPPRETTET = new OppgaveEventType("OPPRETTET"); //$NON-NLS-1$
    public static final OppgaveEventType LUKKET = new OppgaveEventType("LUKKET"); //$NON-NLS-1$
    public static final OppgaveEventType VENT = new OppgaveEventType("VENT"); //$NON-NLS-1$
    public static final OppgaveEventType MANU_VENT = new OppgaveEventType("MANU_VENT"); //$NON-NLS-1$
    public static final OppgaveEventType GJENAPNET = new OppgaveEventType("GJENAPNET"); //$NON-NLS-1$

    OppgaveEventType() {
        // Hibernate trenger den
    }

    protected OppgaveEventType(String kode) {
        super(kode, DISCRIMINATOR);
    }

    public static List<OppgaveEventType> åpningseventtyper(){
        return Arrays.asList(OPPRETTET, GJENAPNET);
    }

}