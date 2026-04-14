package no.nav.foreldrepenger.los.migrering.dto;

import java.util.List;

public record BulkDataWrapper(
    List<BehandlingDataDto> behandlinger,
    List<OppgaveDataDto> aktiveOppgaver,
    List<OppgaveDataDto> inaktiveOppgaver,
    OrgDataDto organisasjonData,
    List<OppgaveFiltreringDataDto> oppgaveFiltrering,
    List<StatEnhetYtelseBehandlingDataDto> statistikkEnhetYtelseBehandling,
    List<StatOppgaveFilterDataDto> statistikkOppgaveFilter
) {
    public static BulkDataWrapper behandlinger(List<BehandlingDataDto> behandlinger) {
        return new BulkDataWrapper(behandlinger, List.of(), List.of(), null, List.of(), List.of(), List.of());
    }

    public static BulkDataWrapper aktiveOppgaver(List<OppgaveDataDto> oppgaver) {
        return new BulkDataWrapper(List.of(), oppgaver, List.of(), null, List.of(), List.of(), List.of());
    }

    public static BulkDataWrapper inaktiveOppgaver(List<OppgaveDataDto> inaktiveOppgaver) {
        return new BulkDataWrapper(List.of(), List.of(), inaktiveOppgaver, null, List.of(), List.of(), List.of());
    }

    public static BulkDataWrapper organisasjonOgKøOppset(OrgDataDto orgData, List<OppgaveFiltreringDataDto> oppgaveFiltreringDataDtoer) {
        return new BulkDataWrapper(List.of(), List.of(), List.of(), orgData, oppgaveFiltreringDataDtoer, List.of(), List.of());
    }

    public static BulkDataWrapper statistikkOppgaveFilter(List<StatOppgaveFilterDataDto> oppgaveFilter) {
        return new BulkDataWrapper(List.of(), List.of(), List.of(), null, List.of(), List.of(), oppgaveFilter);
    }

    public static BulkDataWrapper statistikkEnhetYtelseBehandling(List<StatEnhetYtelseBehandlingDataDto> enhetYtelseBehandling) {
        return new BulkDataWrapper(List.of(), List.of(), List.of(), null, List.of(), enhetYtelseBehandling, List.of());
    }
}
