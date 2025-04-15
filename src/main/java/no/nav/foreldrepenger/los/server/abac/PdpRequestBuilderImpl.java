package no.nav.foreldrepenger.los.server.abac;

import java.util.Set;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.log.mdc.MdcExtendedLogContext;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.pdp.AppRessursData;
import no.nav.vedtak.sikkerhet.abac.pipdata.PipBehandlingStatus;
import no.nav.vedtak.sikkerhet.abac.pipdata.PipFagsakStatus;

@Dependent
@Alternative
@Priority(2)
public class PdpRequestBuilderImpl implements PdpRequestBuilder {

    private static final MdcExtendedLogContext MDC_EXTENDED_LOG_CONTEXT = MdcExtendedLogContext.getContext("prosess");

    private final ForeldrepengerPipKlient foreldrepengerPipKlient;
    private final OppgaveTjeneste oppgaveTjeneste;

    @Inject
    public PdpRequestBuilderImpl(ForeldrepengerPipKlient foreldrepengerPipKlient, OppgaveTjeneste oppgaveTjeneste) {
        this.foreldrepengerPipKlient = foreldrepengerPipKlient;
        this.oppgaveTjeneste = oppgaveTjeneste;
    }

    @Override
    public AppRessursData lagAppRessursData(AbacDataAttributter dataAttributter) {

        var oppgave = hentOppgave(dataAttributter);
        if (oppgave == null) {
            return minimalbuilder().build();
        }

        setLogContext(oppgave);

        var ressursData = minimalbuilder()
            .medAuditIdent(oppgave.getAktørId().getId());
        var system = oppgave.getSystem();
        if (Fagsystem.FPSAK.name().equals(system)) {
            var dto = foreldrepengerPipKlient.hentPipdataForSak(oppgave.getSaksnummer());
            ressursData.leggTilAktørIdSet(dto);
        } else if (Fagsystem.FPTILBAKE.name().equals(system)) {
            ressursData.leggTilAktørId(oppgave.getAktørId().getId());
        } else {
            throw new TekniskException("FPLOS-0003", String.format("Kunne ikke lage PDP-request: ukjent system %s", system));
        }

        return ressursData.build();
    }

    @Override
    public AppRessursData lagAppRessursDataForSystembruker(AbacDataAttributter dataAttributter) {
        setLogContext(hentOppgave(dataAttributter));
        return minimalbuilder().build();
    }

    private static void setLogContext(Oppgave oppgave) {
        if (oppgave == null) {
            return;
        }
        MDC_EXTENDED_LOG_CONTEXT.add("fagsak", oppgave.getSaksnummer());
        MDC_EXTENDED_LOG_CONTEXT.add("behandling", oppgave.getBehandlingId().getValue());
    }

    private Oppgave hentOppgave(AbacDataAttributter dataAttributter) {
        Set<Long> oppgaveIdList = dataAttributter.getVerdier(FplosAbacAttributtType.OPPGAVE_ID);
        return oppgaveIdList.stream().findFirst().map(oppgaveTjeneste::hentOppgave).orElse(null);
    }

    private AppRessursData.Builder minimalbuilder() {
        return AppRessursData.builder()
            .medFagsakStatus(PipFagsakStatus.UNDER_BEHANDLING)
            .medBehandlingStatus(PipBehandlingStatus.UTREDES);
    }
}
