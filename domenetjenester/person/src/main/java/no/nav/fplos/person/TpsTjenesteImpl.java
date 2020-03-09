package no.nav.fplos.person;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.ws.soap.SOAPFaultException;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.fplos.person.api.TpsAdapter;
import no.nav.fplos.person.api.TpsTjeneste;

@ApplicationScoped
public class TpsTjenesteImpl implements TpsTjeneste {

    private TpsAdapter tpsAdapter;

    TpsTjenesteImpl() {
        // for CDI proxy
    }

    @Inject
    public TpsTjenesteImpl(TpsAdapter tpsAdapter) {
        this.tpsAdapter = tpsAdapter;
    }

    @Override
    public Optional<TpsPersonDto> hentBrukerForFnr(PersonIdent fnr) {
        if (fnr.erFdatNummer()) {
            return Optional.empty();
        }
        Optional<AktørId> aktørId = tpsAdapter.hentAktørIdForPersonIdent(fnr);
        if (aktørId.isEmpty()) {
            return Optional.empty();
        }
        try {
            TpsPersonDto personinfo = tpsAdapter.hentKjerneinformasjon(fnr, aktørId.get());
            return Optional.ofNullable(personinfo);
        } catch (SOAPFaultException e) {
            if (e.getMessage().contains("status: S100008F")) {
                // Her sorterer vi ut dødfødte barn
                return Optional.empty();
            } else {
                throw e;
            }
        }
    }

    private Optional<PersonIdent> hentFnr(AktørId aktørId) {
        return tpsAdapter.hentIdentForAktørId(aktørId);
    }

    @Override
    public Optional<TpsPersonDto> hentBrukerForAktør(AktørId aktørId) {
        Optional<PersonIdent> funnetFnr;
        funnetFnr = hentFnr(aktørId);

        return funnetFnr.map(fnr -> tpsAdapter.hentKjerneinformasjon(fnr, aktørId));
    }
}
