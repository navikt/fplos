package no.nav.fplos.person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.fplos.person.api.TpsAdapter;
import no.nav.fplos.person.api.TpsTjeneste;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.vedtak.feil.FeilFactory;

public class TpsTjenesteTest {

    private static Map<AktørId, PersonIdent> FNR_VED_AKTØR_ID = new HashMap<>();
    private static Map<PersonIdent, AktørId> AKTØR_ID_VED_FNR = new HashMap<>();

    private static final AktørId AKTØR_ID = new AktørId(1L);
    private static final AktørId ENDRET_AKTØR_ID = new AktørId(2L);
    private static final AktørId AKTØR_ID_SOM_TRIGGER_EXCEPTION = new AktørId(10L);
    private static final PersonIdent FNR = new PersonIdent("12345678901");
    private static final PersonIdent ENDRET_FNR = new PersonIdent("02345678901");
    private static final LocalDate FØDSELSDATO = LocalDate.of(1992, Month.OCTOBER, 13);

    private static final String NAVN = "Anne-Berit Hjartdal";

    private TpsTjeneste tpsTjeneste;

    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();

    @Before
    public void oppsett() {
        FNR_VED_AKTØR_ID.put(AKTØR_ID, FNR);
        FNR_VED_AKTØR_ID.put(ENDRET_AKTØR_ID, ENDRET_FNR);
        AKTØR_ID_VED_FNR.put(FNR, AKTØR_ID);
        AKTØR_ID_VED_FNR.put(ENDRET_FNR, ENDRET_AKTØR_ID);

        tpsTjeneste = new TpsTjenesteImpl(new TpsAdapterMock());
    }

    @Test
    public void skal_ikke_hente_bruker_for_ukjent_aktør() {
        Optional<TpsPersonDto> funnetBruker = tpsTjeneste.hentBrukerForAktør(new AktørId(666L));
        assertThat(funnetBruker.isPresent()).isFalse();
    }

    @Test
    public void skal_hente_bruker_for_kjent_fnr() {
        Optional<TpsPersonDto> funnetBruker = tpsTjeneste.hentBrukerForFnr(FNR);
        assertThat(funnetBruker.isPresent()).isTrue();
    }

    @Test
    public void skal_ikke_hente_bruker_for_ukjent_fnr() {
        Optional<TpsPersonDto> funnetBruker = tpsTjeneste.hentBrukerForFnr(new PersonIdent("666"));
        assertThat(funnetBruker.isPresent()).isFalse();
    }

    @Test
    public void skal_kaste_feil_ved_tjenesteexception_dersom_aktør_ikke_er_cachet() {
        assertThatThrownBy(() -> tpsTjeneste.hentBrukerForAktør(AKTØR_ID_SOM_TRIGGER_EXCEPTION)).isInstanceOf(TpsException.class);
    }

    private static class TpsAdapterMock implements TpsAdapter {

        @Override
        public Optional<AktørId> hentAktørIdForPersonIdent(PersonIdent fnr) {
            return Optional.ofNullable(AKTØR_ID_VED_FNR.get(fnr));
        }

        @Override
        public Optional<PersonIdent> hentIdentForAktørId(AktørId aktørId) {
            if (aktørId == AKTØR_ID_SOM_TRIGGER_EXCEPTION) {
                throw new TpsException(FeilFactory.create(TpsFeilmeldinger.class)
                        .tpsUtilgjengeligSikkerhetsbegrensning(new HentPersonSikkerhetsbegrensning("String", null)));
            }
            return Optional.ofNullable(FNR_VED_AKTØR_ID.get(aktørId));
        }

        @Override
        public TpsPersonDto hentKjerneinformasjon(PersonIdent fnr, AktørId aktørId) {
            if (!AKTØR_ID_VED_FNR.containsKey(fnr)) {
                return null;
            }
            return new TpsPersonDto.Builder()
                    .medAktørId(aktørId)
                    .medFnr(fnr)
                    .medNavn(NAVN)
                    .medFødselsdato(FØDSELSDATO)
                    .medNavBrukerKjønn("K")
                    .build();
        }
    }
}
