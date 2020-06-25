package no.nav.fplos.kafkatjenester;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.hendelse.Aksjonspunkt;
import no.nav.foreldrepenger.loslager.hendelse.Fagsystem;
import no.nav.foreldrepenger.loslager.hendelse.TilbakekrevingHendelse;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.vedtak.felles.integrasjon.kafka.TilbakebetalingBehandlingProsessEventDto;

@ApplicationScoped
public class TilbakekrevingHendelseOppretter implements HendelseOppretter<TilbakebetalingBehandlingProsessEventDto> {

    @Override
    public TilbakekrevingHendelse opprett(TilbakebetalingBehandlingProsessEventDto dto) {
        var tilbakekrevingHendelse = new TilbakekrevingHendelse();
        tilbakekrevingHendelse.setFørsteFeilutbetalingDato(dto.getFørsteFeilutbetaling());
        tilbakekrevingHendelse.setFeilutbetaltBeløp(dto.getFeilutbetaltBeløp());

        tilbakekrevingHendelse.setBehandlendeEnhet(dto.getBehandlendeEnhet());
        tilbakekrevingHendelse.setYtelseType(FagsakYtelseType.fraKode(dto.getYtelseTypeKode()));
        tilbakekrevingHendelse.setFagsystem(Fagsystem.valueOf(dto.getFagsystem().name()));
        tilbakekrevingHendelse.setBehandlingType(BehandlingType.fraKode(dto.getBehandlingTypeKode()));
        tilbakekrevingHendelse.setSaksnummer(dto.getSaksnummer());
        tilbakekrevingHendelse.setBehandlingOpprettetTidspunkt(dto.getOpprettetBehandling());
        tilbakekrevingHendelse.setBehandlingId(new BehandlingId(dto.getEksternId()));
        tilbakekrevingHendelse.setAktørId(dto.getAktørId());

        var aksjonspunkter = aksjonspunkter(dto.getAksjonspunktKoderMedStatusListe());
        tilbakekrevingHendelse.setAksjonspunkter(aksjonspunkter);

        return tilbakekrevingHendelse;
    }

    private List<Aksjonspunkt> aksjonspunkter(Map<String, String> aksjonspunktKoderMedStatusListe) {
        return aksjonspunktKoderMedStatusListe.entrySet().stream()
                .map(e -> new Aksjonspunkt(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
