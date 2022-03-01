package no.nav.foreldrepenger.los.web.app.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import no.nav.foreldrepenger.los.felles.Kodeverdi;


/**
 * Enkel serialisering av KodeverkTabell klasser, uten at disse trenger @JsonIgnore eller lignende.
 * Deserialisering går av seg selv normalt (får null for andre felter).
 *
 * TODO: Flytt til web kodeverk KodeverRestTjeneste når all normal (De)Ser av Kodeverdi skjer med JsonValue
 */
public class KodeverdiSerializer extends StdSerializer<Kodeverdi> {

    private boolean serialiserKodelisteNavn;

    public KodeverdiSerializer(boolean serialiserKodelisteNavn) {
        super(Kodeverdi.class);
        this.serialiserKodelisteNavn = serialiserKodelisteNavn;
    }

    @Override
    public void serialize(Kodeverdi value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        /*
         * Midlertidig til vi helt skiller vanlig serialisering (JsonValue) fra custom kodemapserialisering
         */
        if (!serialiserKodelisteNavn) {
            jgen.writeString(value.getKode());
            return;
        }

        jgen.writeStartObject();

        jgen.writeStringField("kode", value.getKode());
        jgen.writeStringField("kodeverk", value.getKodeverk());
        jgen.writeStringField("navn", value.getNavn());

        jgen.writeEndObject();
    }

}
