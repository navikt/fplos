package no.nav.fplos.person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Person;

public class PersonTjenesteTest {

    private static final Map<AktørId, Fødselsnummer> FNR_VED_AKTØR_ID = new HashMap<>();
    private static final Map<Fødselsnummer, AktørId> AKTØR_ID_VED_FNR = new HashMap<>();

    private static final AktørId AKTØR_ID = new AktørId(1L);
    private static final AktørId ENDRET_AKTØR_ID = new AktørId(2L);
    private static final Fødselsnummer FNR = new Fødselsnummer("12345678901");
    private static final Fødselsnummer ENDRET_FNR = new Fødselsnummer("02345678901");

    private static final String NAVN = "Testbruker Testbrukersen";

    private PersonTjeneste personTjeneste;

    @BeforeEach
    public void oppsett() {
        FNR_VED_AKTØR_ID.put(AKTØR_ID, FNR);
        FNR_VED_AKTØR_ID.put(ENDRET_AKTØR_ID, ENDRET_FNR);
        AKTØR_ID_VED_FNR.put(FNR, AKTØR_ID);
        AKTØR_ID_VED_FNR.put(ENDRET_FNR, ENDRET_AKTØR_ID);

        personTjeneste = new TpsPersonTjeneste(new TpsAdapterMock());
    }

    @Test
    public void skal_ikke_hente_bruker_for_ukjent_aktør() {
        assertThatThrownBy(() -> personTjeneste.hentPerson(new AktørId(666L))).isNotNull();
    }

    @Test
    public void skal_hente_bruker_for_kjent_fnr() {
        Optional<Person> funnetBruker = personTjeneste.hentPerson(FNR);
        assertThat(funnetBruker.isPresent()).isTrue();
    }

    @Test
    public void skal_ikke_hente_bruker_for_ukjent_fnr() {
        Optional<Person> funnetBruker = personTjeneste.hentPerson(new Fødselsnummer("66666666666"));
        assertThat(funnetBruker.isPresent()).isFalse();
    }

    private static class TpsAdapterMock implements TpsAdapter {

        @Override
        public Optional<AktørId> hentAktørForFødselsnummer(Fødselsnummer fnr) {
            return Optional.ofNullable(AKTØR_ID_VED_FNR.get(fnr));
        }

        @Override
        public Optional<Person> hentPerson(AktørId aktørId) {
            var fnr = FNR_VED_AKTØR_ID.get(aktørId);
            if (!AKTØR_ID_VED_FNR.containsKey(fnr)) {
                throw PersonTjenesteFeil.FACTORY.fantIkkePerson(null).toException();
            }
            return Optional.of(new Person.Builder()
                    .medAktørId(aktørId)
                    .medFnr(fnr)
                    .medNavn(NAVN)
                    .build());
        }
    }
}
