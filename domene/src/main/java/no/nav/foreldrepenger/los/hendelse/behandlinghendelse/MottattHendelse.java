package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.NaturalId;

import no.nav.foreldrepenger.los.felles.BaseEntitet;

import java.sql.Types;


@Entity(name = "MottattHendelse")
@Table(name = "MOTTATT_HENDELSE")
public class MottattHendelse extends BaseEntitet {

    @Id
    @NaturalId
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "hendelse_uid")
    private String hendelseUid;

    MottattHendelse() {
        //for hibernate
    }

    public MottattHendelse(String hendelseUid) {
        this.hendelseUid = hendelseUid;
    }

    public String getHendelseUid() {
        return hendelseUid;
    }
}
