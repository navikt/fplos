package no.nav.fplos.person;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import static no.nav.fplos.person.TpsPersonMapper.tilPerson;

@ApplicationScoped
public class TpsAdapterImpl implements TpsAdapter {
    private static final Logger log = LoggerFactory.getLogger(TpsAdapterImpl.class);

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
    public Optional<AktørId> hentAktørForFødselsnummer(Fødselsnummer fnr) {
        try {
            return aktørConsumer.hentAktørIdForPersonIdent(fnr.asValue())
                    .map(AktørId::new);
        } catch (DetFinnesFlereAktørerMedSammePersonIdentException ignore) { // NOSONAR
            return Optional.empty();
        }
    }

    @Override
    public Optional<Person> hentPerson(AktørId aktørId) {
        var request = hentFødselsnummerForAktør(aktørId)
                .map(TpsAdapterImpl::lagRequest);
        if (request.isEmpty()) {
            return Optional.empty();
        }
        try {
            return håndterResponse(aktørId, request.get());
        } catch (HentPersonPersonIkkeFunnet | HentPersonSikkerhetsbegrensning e) {
            // none the wiser om manglende tilgang
            return Optional.empty();
        }
    }

    private Optional<Person> håndterResponse(AktørId aktørId, HentPersonRequest request) throws HentPersonPersonIkkeFunnet, HentPersonSikkerhetsbegrensning {
        HentPersonResponse response = personConsumer.hentPersonResponse(request);
        var tpsPerson = response.getPerson();
        if (!(tpsPerson instanceof Bruker)) {
            throw PersonTjenesteFeil.FACTORY.ukjentBrukerType().toException();
        }
        var person = tilPerson(aktørId, (Bruker) tpsPerson);
        return Optional.of(person);
    }

    private Optional<Fødselsnummer> hentFødselsnummerForAktør(AktørId aktørId) {
        return aktørConsumer.hentPersonIdentForAktørId(aktørId.getId()).map(Fødselsnummer::new);
    }

    private static HentPersonRequest lagRequest(Fødselsnummer fnr) {
        HentPersonRequest request = new HentPersonRequest();
        request.setAktoer(TpsUtil.lagTpsPersonIdent(fnr.asValue()));
        return request;
    }
}
