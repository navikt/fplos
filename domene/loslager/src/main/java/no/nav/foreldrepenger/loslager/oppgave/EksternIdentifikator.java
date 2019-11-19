package no.nav.foreldrepenger.loslager.oppgave;


import no.nav.foreldrepenger.loslager.BaseEntitet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "EksternIdentifikator")
@Table(name = "EKSTERN_IDENTIFIKATOR")
public class EksternIdentifikator extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EKSTERN_IDENTIFIKATOR")
    private Long id;

    @Column(name = "SYSTEM")
    private String system;

    @Column(name = "EKSTERN_REF_ID")
    private String eksternRefId;

    public EksternIdentifikator(){

    }

    public EksternIdentifikator(String system, String eksternRefId) {
        this.system = system;
        this.eksternRefId = eksternRefId;
    }

    public Long getId() {
        return id;
    }

    public String getSystem() {
        return system;
    }

    public String getEksternRefId() {
        return eksternRefId;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private EksternIdentifikator tempEksternIdentifikator;

        private Builder(){
            tempEksternIdentifikator = new EksternIdentifikator();
        }

        public Builder medSystem(String system){
            tempEksternIdentifikator.system = system;
            return this;
        }

        public Builder medEksternRefId(String eksternRefId){
            tempEksternIdentifikator.eksternRefId = eksternRefId;
            return this;
        }

        public Builder dummyEksternIdentifikator(){
            tempEksternIdentifikator.id = 0L;
            tempEksternIdentifikator.system = "FPSAK";
            tempEksternIdentifikator.eksternRefId = "TEST_IDENT";
            return this;
        }

        public EksternIdentifikator build(){
            EksternIdentifikator eksternIdentifikator = tempEksternIdentifikator;
            tempEksternIdentifikator = new EksternIdentifikator();
            return eksternIdentifikator;
        }
    }

}
