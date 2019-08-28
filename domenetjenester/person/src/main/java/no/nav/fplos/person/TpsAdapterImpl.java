package no.nav.fplos.person;

import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.fplos.person.api.TpsAdapter;
import no.nav.foreldrepenger.loslager.aktør.GeografiskTilknytning;
import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentGeografiskTilknytningPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentGeografiskTilknytningSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningResponse;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import no.nav.vedtak.felles.integrasjon.aktør.klient.AktørConsumerMedCache;
import no.nav.vedtak.felles.integrasjon.aktør.klient.DetFinnesFlereAktørerMedSammePersonIdentException;
import no.nav.vedtak.felles.integrasjon.person.PersonConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;
import java.util.function.Consumer;

@ApplicationScoped
public class TpsAdapterImpl implements TpsAdapter {
    private static final Logger log = LoggerFactory.getLogger(TpsAdapterImpl.class);

    private AktørConsumerMedCache aktørConsumer;
    private PersonConsumer personConsumer;
    private TpsOversetter tpsOversetter;

    public TpsAdapterImpl() {
        // for CDI proxy
    }

    @Inject
    public TpsAdapterImpl(AktørConsumerMedCache aktørConsumer,
                          PersonConsumer personConsumer,
                          TpsOversetter tpsOversetter) {
        this.aktørConsumer = aktørConsumer;
        this.personConsumer = personConsumer;
        this.tpsOversetter = tpsOversetter;
    }

    @Override
    public Optional<AktørId> hentAktørIdForPersonIdent(PersonIdent fnr) {
        if (fnr.erFdatNummer()) {
            // har ikke tildelt personnr
            return Optional.empty();
        }
        try {
            return aktørConsumer.hentAktørIdForPersonIdent(fnr.getIdent()).map(AktørId::new);
        } catch (DetFinnesFlereAktørerMedSammePersonIdentException e) { // NOSONAR
            // Her sorterer vi ut dødfødte barn
            return Optional.empty();
        }
    }

    @Override
    public Optional<PersonIdent> hentIdentForAktørId(AktørId aktørId) {
        return aktørConsumer.hentPersonIdentForAktørId(aktørId.getId()).map(PersonIdent::new);
    }

    private TpsPersonDto hentKjerneinformasjon(PersonIdent fnr, AktørId aktørId, Consumer<String> operasjonPåSerialisertPersonInfo) {
        HentPersonRequest request = new HentPersonRequest();
        request.setAktoer(TpsUtil.lagPersonIdent(fnr.getIdent()));
        request.getInformasjonsbehov().add(Informasjonsbehov.ADRESSE);
        request.getInformasjonsbehov().add(Informasjonsbehov.KOMMUNIKASJON);
        request.getInformasjonsbehov().add(Informasjonsbehov.FAMILIERELASJONER);
        try {
            return håndterResponse(aktørId, operasjonPåSerialisertPersonInfo, request);
        } catch (HentPersonPersonIkkeFunnet e) {
            throw TpsFeilmeldinger.FACTORY.fantIkkePerson(e).toException();
        } catch (HentPersonSikkerhetsbegrensning e) {
            //TODO Etter at kode6/7 meldingene er filtrert ved hjelp av ABAC istedenfor direkte kall mot TPS her, throw TpsFeilmeldinger.FACTORY.tpsUtilgjengeligSikkerhetsbegrensning(e).toException() og ikke returner null
            log.info("Kall mot TPS feilet", e);
            return null;
        }
    }

    private TpsPersonDto håndterResponse(AktørId aktørId, Consumer<String> operasjonPåSerialisertPersonInfo, HentPersonRequest request)
            throws HentPersonPersonIkkeFunnet, HentPersonSikkerhetsbegrensning {
        HentPersonResponse response = personConsumer.hentPersonResponse(request);
        Person person = response.getPerson();
        if (!(person instanceof Bruker)) {
            throw TpsFeilmeldinger.FACTORY.ukjentBrukerType().toException();
        }
        if (operasjonPåSerialisertPersonInfo != null) {
            operasjonPåSerialisertPersonInfo.accept(HentPersonSerialiserer.serialiserKjerneinformasjon(response));
        }
        return tpsOversetter.tilBrukerInfo(aktørId, (Bruker) person);
    }

    @Override
    public TpsPersonDto hentKjerneinformasjon(PersonIdent fnr, AktørId aktørId) {
        return hentKjerneinformasjon(fnr, aktørId, null);
    }

    @Override
    public GeografiskTilknytning hentGeografiskTilknytning(String fnr) {
        HentGeografiskTilknytningRequest request = new HentGeografiskTilknytningRequest();
        request.setAktoer(TpsUtil.lagPersonIdent(fnr));
        try {
            HentGeografiskTilknytningResponse response = personConsumer.hentGeografiskTilknytning(request);
            return tpsOversetter.tilGeografiskTilknytning(response.getGeografiskTilknytning(), response.getDiskresjonskode());
        } catch (HentGeografiskTilknytningSikkerhetsbegrensing e) {
            throw TpsFeilmeldinger.FACTORY.tpsUtilgjengeligGeografiskTilknytningSikkerhetsbegrensing(e).toException();
        } catch (HentGeografiskTilknytningPersonIkkeFunnet e) {
            throw TpsFeilmeldinger.FACTORY.geografiskTilknytningIkkeFunnet(e).toException();
        }
    }
}
