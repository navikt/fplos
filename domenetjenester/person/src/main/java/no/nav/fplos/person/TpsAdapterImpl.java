package no.nav.fplos.person;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Person;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import no.nav.vedtak.felles.integrasjon.aktør.klient.AktørConsumerMedCache;
import no.nav.vedtak.felles.integrasjon.aktør.klient.DetFinnesFlereAktørerMedSammePersonIdentException;
import no.nav.vedtak.felles.integrasjon.person.PersonConsumer;

import static no.nav.fplos.person.TpsMapper.tilPerson;

@ApplicationScoped
public class TpsAdapterImpl implements TpsAdapter {

    private AktørConsumerMedCache aktørConsumer;
    private PersonConsumer personConsumer;

    public TpsAdapterImpl() {
        // for CDI proxy
    }

    @Inject
    TpsAdapterImpl(AktørConsumerMedCache aktørConsumer,
                          PersonConsumer personConsumer) {
        this.aktørConsumer = aktørConsumer;
        this.personConsumer = personConsumer;
    }

    @Override
    public Optional<Person> hentPerson(AktørId aktørId) {
        return hentFødselsnummerForAktør(aktørId)
                .flatMap(fnr -> hentPerson(aktørId, fnr));
    }

    private Optional<Fødselsnummer> hentFødselsnummerForAktør(AktørId aktørId) {
        return aktørConsumer.hentPersonIdentForAktørId(aktørId.getId()).map(Fødselsnummer::new);
    }

    private Optional<Person> hentPerson(AktørId aktørId, Fødselsnummer fnr) {
        var request = TpsMapper.hentPersonRequest(fnr);
        try {
            var tpsPerson = hentTpsPerson(request);
            var person = tilPerson(aktørId, (Bruker) tpsPerson);
            return Optional.of(person);
        } catch (HentPersonPersonIkkeFunnet | HentPersonSikkerhetsbegrensning e) {
            // none the wiser om manglende tilgang
            return Optional.empty();
        }
    }

    private no.nav.tjeneste.virksomhet.person.v3.informasjon.Person hentTpsPerson(HentPersonRequest request) throws HentPersonPersonIkkeFunnet, HentPersonSikkerhetsbegrensning {
        HentPersonResponse response = personConsumer.hentPersonResponse(request);
        var tpsPerson = response.getPerson();
        if (!(tpsPerson instanceof Bruker)) {
            throw PersonTjenesteFeil.FACTORY.ukjentBrukerType().toException();
        }
        return tpsPerson;
    }

}
