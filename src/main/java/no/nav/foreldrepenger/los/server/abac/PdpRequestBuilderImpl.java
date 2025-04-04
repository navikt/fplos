package no.nav.foreldrepenger.los.server.abac;

import java.util.Set;
import java.util.UUID;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.log.mdc.MdcExtendedLogContext;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.pdp.AppRessursData;
import no.nav.vedtak.sikkerhet.abac.pipdata.PipBehandlingStatus;
import no.nav.vedtak.sikkerhet.abac.pipdata.PipFagsakStatus;

@Dependent
@Alternative
@Priority(2)
public class PdpRequestBuilderImpl implements PdpRequestBuilder {

    private static final MdcExtendedLogContext MDC_EXTENDED_LOG_CONTEXT = MdcExtendedLogContext.getContext("prosess");

    private ForeldrepengerPipKlient foreldrepengerPipKlient;
    private OppgaveTjeneste oppgaveTjeneste;

    @Inject
    public PdpRequestBuilderImpl(ForeldrepengerPipKlient foreldrepengerPipKlient, OppgaveTjeneste oppgaveTjeneste) {
        this.foreldrepengerPipKlient = foreldrepengerPipKlient;
        this.oppgaveTjeneste = oppgaveTjeneste;
    }

    @Override
    public AppRessursData lagAppRessursData(AbacDataAttributter dataAttributter) {
        Set<Long> oppgaveIdList = dataAttributter.getVerdier(FplosAbacAttributtType.OPPGAVE_ID);
        Set<UUID> behandlingIdList = dataAttributter.getVerdier(StandardAbacAttributtType.BEHANDLING_UUID);

        if (!oppgaveIdList.isEmpty() && !behandlingIdList.isEmpty()) {
            throw PdpRequestBuilderFeil.støtterIkkeBådeOppgaveIdOgBehandlingId();
        }

        Oppgave oppgave = null;
        if (!oppgaveIdList.isEmpty()) {
            oppgave = oppgaveTjeneste.hentOppgave(oppgaveIdList.iterator().next());
        } else if (!behandlingIdList.isEmpty()) {
            var behandlingId = new BehandlingId(behandlingIdList.iterator().next());
            oppgave = oppgaveTjeneste.hentNyesteOppgaveTilknyttet(behandlingId)
                .orElseThrow(() -> PdpRequestBuilderFeil.finnerIkkeOppgaveTilknyttetBehandling(behandlingId));
        }

        var ressursData = AppRessursData.builder();
        if (oppgave != null) {
            // Set loggkontekst
            MDC_EXTENDED_LOG_CONTEXT.add("fagsak", oppgave.getFagsakSaksnummer());
            MDC_EXTENDED_LOG_CONTEXT.add("behandling", oppgave.getBehandlingId().getValue());

            var system = oppgave.getSystem();
            if (Fagsystem.FPSAK.name().equals(system)) {
                var dto = foreldrepengerPipKlient.hentPipdataForBehandling(oppgave.getBehandlingId());
                ressursData.leggTilAbacAktørIdSet(dto)
                    .medAuditAktørId(oppgave.getAktørId().getId())
                    .medFagsakStatus(PipFagsakStatus.UNDER_BEHANDLING)
                    .medBehandlingStatus(PipBehandlingStatus.UTREDES);
            } else if (Fagsystem.FPTILBAKE.name().equals(system)) {
                ressursData.leggTilAktørId(oppgave.getAktørId().getId());
            } else {
                throw PdpRequestBuilderFeil.ukjentSystem(system);
            }
        }
        return ressursData.build();
    }

    private static class PdpRequestBuilderFeil {
        static TekniskException finnerIkkeOppgaveTilknyttetBehandling(BehandlingId behandlingId) {
            return new TekniskException("FPLOS-00001",
                String.format("Kunne ikke lage PDP-request: finner ikke oppgave knyttet til behandling %s", behandlingId));
        }

        static TekniskException støtterIkkeBådeOppgaveIdOgBehandlingId() {
            return new TekniskException("FPLOS-00002", "Kunne ikke lage PDP-request: støtter ikke både oppgaveId og behandligId");
        }

        static TekniskException ukjentSystem(String system) {
            return new TekniskException("FPLOS-0003", String.format("Kunne ikke lage PDP-request: ukjent system %s", system));
        }
    }
}
