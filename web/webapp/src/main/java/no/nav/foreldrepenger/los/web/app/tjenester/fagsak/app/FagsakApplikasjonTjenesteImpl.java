package no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.foreldrepenger.domene.typer.Saksnummer;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.fplos.ansatt.AnsattTjeneste;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.fplos.person.api.TpsTjeneste;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;

@ApplicationScoped
public class FagsakApplikasjonTjenesteImpl implements FagsakApplikasjonTjeneste{

    private TpsTjeneste tpsTjeneste;
    private ForeldrepengerBehandlingRestKlient restKlient;
    private OppgaveTjeneste oppgaveTjeneste;
    private AnsattTjeneste ansattTjeneste;


    private Predicate<String> predikatErFnr = søkestreng -> søkestreng.matches("\\d{11}");

    protected FagsakApplikasjonTjenesteImpl() {
        //CDI runner
    }

    @Inject
    public FagsakApplikasjonTjenesteImpl(TpsTjeneste tpsTjeneste,
                                         OppgaveTjeneste oppgaveTjeneste,
                                         AnsattTjeneste ansattTjeneste,
                                         ForeldrepengerBehandlingRestKlient restKlient) {
        this.tpsTjeneste = tpsTjeneste;
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.ansattTjeneste = ansattTjeneste;
        this.restKlient = restKlient;
    }

    @Override
    public List<FagsakDto> hentSaker(String søkestreng) {
        if (!søkestreng.matches("\\d+")) {
            return Collections.emptyList();
        }

        return predikatErFnr.test(søkestreng) ? hentSakerForFnr(new PersonIdent(søkestreng)) : hentFagsakForSaksnummer(new Saksnummer(søkestreng));
    }

    @Override
    public String hentNavnHvisReservertAvAnnenSaksbehandler(Oppgave oppgave) {
        String innloggetBruker = SubjectHandler.getSubjectHandler().getUid();
        if(oppgave.getReservasjon() != null &&
                oppgave.getReservasjon().getReservertAv() != null &&
                oppgave.getReservasjon().getReservertTil() != null &&
                oppgave.getReservasjon().erAktiv()) {
            String oppgaveReservertAv = oppgave.getReservasjon().getReservertAv();
            boolean reservertAvAnnenSaksbehandler = !oppgaveReservertAv.equalsIgnoreCase(innloggetBruker);
            return reservertAvAnnenSaksbehandler ? ansattTjeneste.hentAnsattNavn(oppgaveReservertAv) : null;
        }
        return null;
    }

    private List<FagsakDto> hentSakerForFnr(PersonIdent fnr) {
        Optional<TpsPersonDto> funnetNavBruker = tpsTjeneste.hentBrukerForFnr(fnr);
        if (funnetNavBruker.isEmpty()) {
            return Collections.emptyList();
        }

        List<FagsakDto> fagsakDtos = restKlient.getFagsakFraFnr(fnr.getIdent());
        if (fagsakDtos.isEmpty()) {
            return Collections.emptyList();
        }
        return fagsakDtos;
    }

    private List<FagsakDto> hentFagsakForSaksnummer(Saksnummer saksnummer) {
        List<FagsakDto> fagsakDtos = restKlient.getFagsakFraSaksnummer(saksnummer.getVerdi());
        if (fagsakDtos.isEmpty()) {
            return Collections.emptyList();
        }
        return fagsakDtos;
    }
}
