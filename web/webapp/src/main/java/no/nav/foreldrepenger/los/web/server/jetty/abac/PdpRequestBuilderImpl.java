package no.nav.foreldrepenger.los.web.server.jetty.abac;

import static no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType.SUBJECT_FELLES_ENHETIDLISTE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_DOMENE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_RESOURCE_TYPE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.XACML10_ACTION_ACTION_ID;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.abac.ForeldrepengerPipKlient;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.PdpKlient;
import no.nav.vedtak.sikkerhet.abac.PdpRequest;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.pdp.AppRessursData;
import no.nav.vedtak.sikkerhet.abac.pdp.BehandlingStatus;
import no.nav.vedtak.sikkerhet.abac.pdp.FagsakStatus;
import no.nav.vedtak.sikkerhet.abac.pdp.ForeldrepengerDataKeys;

@Dependent
@Alternative
@Priority(2)
public class PdpRequestBuilderImpl implements PdpRequestBuilder {

    public static final String ABAC_DOMAIN = "foreldrepenger";

    private ForeldrepengerPipKlient foreldrepengerPipKlient;
    private OppgaveTjeneste oppgaveTjeneste;

    @Inject
    public PdpRequestBuilderImpl(ForeldrepengerPipKlient foreldrepengerPipKlient,
                                 OppgaveTjeneste oppgaveTjeneste) {
        this.foreldrepengerPipKlient = foreldrepengerPipKlient;
        this.oppgaveTjeneste = oppgaveTjeneste;
    }

    @Override
    public PdpRequest lagPdpRequest(AbacAttributtSamling attributter) {
        var pdpRequest = new PdpRequest();
        pdpRequest.put(SUBJECT_FELLES_ENHETIDLISTE, new ArrayList<>(attributter.getVerdier(FplosAbacAttributtType.OPPGAVESTYRING_ENHET)));
        pdpRequest.put(RESOURCE_FELLES_DOMENE, ABAC_DOMAIN);
        pdpRequest.put(PdpKlient.ENVIRONMENT_AUTH_TOKEN, attributter.getIdToken());
        pdpRequest.put(XACML10_ACTION_ACTION_ID, attributter.getActionType().getEksternKode());
        pdpRequest.put(RESOURCE_FELLES_RESOURCE_TYPE, attributter.getResource());

        Set<Long> oppgaveIdList = attributter.getVerdier(FplosAbacAttributtType.OPPGAVE_ID);
        Set<UUID> behandlingIdList = attributter.getVerdier(StandardAbacAttributtType.BEHANDLING_UUID);

        if (oppgaveIdList.size() > 0 && behandlingIdList.size() > 0) {
            throw PdpRequestBuilderFeil.støtterIkkeBådeOppgaveIdOgBehandlingId();
        }

        if (oppgaveIdList.size() > 0) {
            var oppgave = oppgaveTjeneste.hentOppgave(oppgaveIdList.iterator().next());
            leggTilAttributterForBehandling(pdpRequest, oppgave);
        }
        if (behandlingIdList.size() > 0) {
            var behandlingId = new BehandlingId(behandlingIdList.iterator().next());
            var oppgave = oppgaveTjeneste.hentNyesteOppgaveTilknyttet(behandlingId)
                    .orElseThrow(() -> PdpRequestBuilderFeil.finnerIkkeOppgaveTilknyttetBehandling(behandlingId));
            leggTilAttributterForBehandling(pdpRequest, oppgave);
        }

        return pdpRequest;
    }

    @Override
    public boolean nyttAbacGrensesnitt() {
        return true;
    }

    @Override
    public AppRessursData lagAppRessursData(AbacDataAttributter dataAttributter) {
        Set<Long> oppgaveIdList = dataAttributter.getVerdier(FplosAbacAttributtType.OPPGAVE_ID);
        Set<UUID> behandlingIdList = dataAttributter.getVerdier(StandardAbacAttributtType.BEHANDLING_UUID);

        if (!oppgaveIdList.isEmpty() && !behandlingIdList.isEmpty()) {
            throw PdpRequestBuilderFeil.støtterIkkeBådeOppgaveIdOgBehandlingId();
        }

        Oppgave oppgave  = null;
        if (!oppgaveIdList.isEmpty()) {
            oppgave = oppgaveTjeneste.hentOppgave(oppgaveIdList.iterator().next());
        } else if (!behandlingIdList.isEmpty()) {
            var behandlingId = new BehandlingId(behandlingIdList.iterator().next());
             oppgave = oppgaveTjeneste.hentNyesteOppgaveTilknyttet(behandlingId)
                    .orElseThrow(() -> PdpRequestBuilderFeil.finnerIkkeOppgaveTilknyttetBehandling(behandlingId));
        }

        var ressursData = AppRessursData.builder();
        if (oppgave != null) {
            var system = oppgave.getSystem();
            if (Fagsystem.FPSAK.name().equals(system)) {
                var dto = foreldrepengerPipKlient.hentPipdataForBehandling(oppgave.getBehandlingId());
                ressursData.leggTilAktørIdSet(dto.aktørIder());
                Optional.ofNullable(dto.fagsakStatus()).ifPresent(fss -> ressursData.leggTilRessurs(ForeldrepengerDataKeys.FAGSAK_STATUS, fss));
                Optional.ofNullable(dto.behandlingStatus()).ifPresent(bhs -> ressursData.leggTilRessurs(ForeldrepengerDataKeys.BEHANDLING_STATUS, bhs));
            } else if (Fagsystem.FPTILBAKE.name().equals(system)) {
                ressursData.leggTilAktørId(oppgave.getAktørId().getId());
            } else {
                throw PdpRequestBuilderFeil.ukjentSystem(system);
            }
        }
        return ressursData.build();
    }

    private void leggTilAttributterForBehandling(PdpRequest pdpRequest, Oppgave oppgave) {
        var system = oppgave.getSystem();
        if ("FPSAK".equals(system)) {
            leggTilAttributterForFpsakBehandling(pdpRequest, oppgave);
        } else if ("FPTILBAKE".equals(system)) {
            leggTilAttributterForFptilbakeBehandling(pdpRequest, oppgave);
        } else {
            throw PdpRequestBuilderFeil.ukjentSystem(system);
        }
    }

    private void leggTilAttributterForFpsakBehandling(PdpRequest pdpRequest, Oppgave oppgave) {
        var dto = foreldrepengerPipKlient.hentPipdataForBehandling(oppgave.getBehandlingId());

        pdpRequest.put(FplosAbacAttributtType.RESOURCE_FORELDREPENGER_SAK_SAKSSTATUS, dto.fagsakStatus());
        pdpRequest.put(FplosAbacAttributtType.RESOURCE_FORELDREPENGER_SAK_BEHANDLINGSSTATUS, dto.behandlingStatus());
        pdpRequest.put(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, dto.aktørIder());
    }

    private void leggTilAttributterForFptilbakeBehandling(PdpRequest pdpRequest, Oppgave oppgave) {
        pdpRequest.put(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, Set.of(oppgave.getAktørId().getId()));
    }

    private static class PdpRequestBuilderFeil {
        static TekniskException finnerIkkeOppgaveTilknyttetBehandling(BehandlingId behandlingId) {
            return new TekniskException("FPLOS-00001", String.format("Kunne ikke lage PDP-request: finner ikke oppgave knyttet til behandling %s", behandlingId));
        }

        static TekniskException støtterIkkeBådeOppgaveIdOgBehandlingId() {
            return new TekniskException("FPLOS-00002", "Kunne ikke lage PDP-request: støtter ikke både oppgaveId og behandligId");
        }

        static TekniskException ukjentSystem(String system) {
            return new TekniskException("FPLOS-0003", String.format("Kunne ikke lage PDP-request: ukjent system %s", system));
        }
    }
}
