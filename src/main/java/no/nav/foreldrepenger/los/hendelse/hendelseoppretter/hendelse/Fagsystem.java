package no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum Fagsystem {

    FPSAK("FPSAK"),
    FPTILBAKE("FPTILBAKE");

    private final String kode;

    private String getKode() {
        return kode;
    }

    Fagsystem(String kode) {
        this.kode = kode;
    }

    @Converter(autoApply = true)
    public static class FagSystemConverter implements AttributeConverter<Fagsystem, String> {

        @Override
        public String convertToDatabaseColumn(Fagsystem attribute) {
            return attribute == null ? null : attribute.getKode();
        }

        @Override
        public Fagsystem convertToEntityAttribute(String dbData) {
            return dbData == null ? null : Fagsystem.valueOf(dbData);
        }
    }
}
