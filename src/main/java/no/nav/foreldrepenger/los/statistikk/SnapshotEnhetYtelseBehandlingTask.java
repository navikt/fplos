package no.nav.foreldrepenger.los.statistikk;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@Dependent
@ProsessTask(value = "statistikk.enhetytelsebehandling", cronExpression = "00 59 22 * * *", maxFailedRuns = 1)
public class SnapshotEnhetYtelseBehandlingTask implements ProsessTaskHandler {

    private final StatistikkRepository statistikkRepository;

    @Inject
    public SnapshotEnhetYtelseBehandlingTask(StatistikkRepository statistikkRepository) {
        this.statistikkRepository = statistikkRepository;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var dato = LocalDate.now();
        var tidsstempel = System.currentTimeMillis();
        var åpneOppgaver = statistikkRepository.hentÅpneOppgaverPerEnhetYtelseBehandling();
        var opprettedeOppgaver = statistikkRepository.hentOpprettetOppgaverPerEnhetYtelseBehandling();
        var avsluttetOppgaver = statistikkRepository.hentAvsluttetOppgaverPerEnhetYtelseBehandling();
        var alleInnslag = Stream.of(åpneOppgaver, opprettedeOppgaver, avsluttetOppgaver)
            .flatMap(Collection::stream)
            .map(Gruppering::new)
            .collect(Collectors.toSet());

        List<StatistikkEnhetYtelseBehandling> lagres = new ArrayList<>();
        for (var innslag : alleInnslag) {
            lagInnslag(innslag, tidsstempel, dato, åpneOppgaver, opprettedeOppgaver, avsluttetOppgaver).ifPresent(lagres::add);
        }
        statistikkRepository.lagreStatistikkEnhetYtelseBehandling(lagres);
    }
    private record Gruppering(String enhet, FagsakYtelseType ytelse, BehandlingType behandling) {
        public Gruppering(OppgaveEnhetYtelseBehandling o) {
            this(o.enhet(), o.fagsakYtelseType(), o.behandlingType());
        }
    }


    private Optional<StatistikkEnhetYtelseBehandling> lagInnslag(Gruppering nøkkel,
                                                                Long tidsstempel, LocalDate dato,
                                                                Collection<OppgaveEnhetYtelseBehandling> åpne,
                                                                Collection<OppgaveEnhetYtelseBehandling> opprettet,
                                                                Collection<OppgaveEnhetYtelseBehandling> avsluttet) {
        var antallÅpne = finnInnslag(åpne, nøkkel);
        var antallOpprettet = finnInnslag(opprettet, nøkkel);
        var antallAvsluttet = finnInnslag(avsluttet, nøkkel);
        if (antallÅpne == 0 && antallOpprettet == 0 && antallAvsluttet == 0) {
            return Optional.empty();
        }
        return Optional.of(new StatistikkEnhetYtelseBehandling(nøkkel.enhet(), tidsstempel, nøkkel.ytelse(), nøkkel.behandling(),
            dato, antallÅpne, antallOpprettet, antallAvsluttet));
    }

    private Integer finnInnslag(Collection<OppgaveEnhetYtelseBehandling> samling, Gruppering nøkkel) {
        return samling.stream()
            .filter(o -> o.enhet().equals(nøkkel.enhet()))
            .filter(o -> o.fagsakYtelseType() == nøkkel.ytelse())
            .filter(o -> o.behandlingType() == nøkkel.behandling())
            .map(OppgaveEnhetYtelseBehandling::antall)
            .findFirst()
            .orElse(0L)
            .intValue();
    }


}
