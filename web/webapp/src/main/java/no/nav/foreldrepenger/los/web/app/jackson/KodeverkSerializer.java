package no.nav.foreldrepenger.los.web.app.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

import no.nav.fplos.kodeverk.KodeverkTabell;

/**
 * Enkel serialisering av KodeverkTabell klasser, uten at disse trenger @JsonIgnore eller lignende. Deserialisering går
 * av seg selv normalt (får null for andre felter).
 */
public class KodeverkSerializer extends StdSerializer<KodeverkTabell> {

    public KodeverkSerializer() {
        super(KodeverkTabell.class);
    }

    @Override
    public void serialize(KodeverkTabell value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStartObject();

        jgen.writeStringField("kode", value.getKode());
        jgen.writeStringField("navn", value.getNavn());

        jgen.writeEndObject();
    }

}
