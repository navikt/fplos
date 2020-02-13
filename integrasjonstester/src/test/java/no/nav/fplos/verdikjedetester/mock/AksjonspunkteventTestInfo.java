package no.nav.fplos.verdikjedetester.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDto;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import no.nav.vedtak.felles.integrasjon.kafka.Fagsystem;

import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;

public class AksjonspunkteventTestInfo {

    private String avdeling;
    private String fagsakYtelseType;
    BehandlingProsessEventDto.Builder behandlingProsessEventDtoBuilder;
    BehandlingProsessEventDto behandlingProsessEventDto;
    private ObjectMapper objectMapper = new ObjectMapper();

    private final String DEFAULT = "DEFAULT_TEST";

    private final static String DEFAULT_AKTØR_ID = "3";
    private final static String DEFAULT_BEHANDLING_STEG_KODE = "BEHANDLING_STEG";
    private final static String DEFAULT_BEHANDLING_STATUS_KODE = "UTRED";


    AksjonspunkteventTestInfo(Long behandlingId, UUID eksternId, String behandlendeEnhet, Long saksnummer,
                              String behandlingtypeKode, String fagsakYtelseTypeKode) {
        behandlingProsessEventDtoBuilder = BehandlingProsessEventDto.builder()
                .medFagsystem(Fagsystem.FPSAK)
                .medBehandlingId(behandlingId).medBehandlendeEnhet(behandlendeEnhet)
                .medEksternId(eksternId)
                .medBehandlingTypeKode(behandlingtypeKode).medSaksnummer("" + saksnummer)
                .medYtelseTypeKode(fagsakYtelseTypeKode)
                .medOpprettetBehandling(LocalDateTime.now().truncatedTo(MILLIS))
                .medAktørId(DEFAULT_AKTØR_ID)
                .medBehandlingSteg(DEFAULT_BEHANDLING_STEG_KODE)
                .medBehandlingStatus(DEFAULT_BEHANDLING_STATUS_KODE);
    }

    public String tilmeldingstekst() {
        try {
            Writer jsonWriter = new StringWriter();
            objectMapper.writeValue(jsonWriter, behandlingProsessEventDto);
            jsonWriter.flush();
            return jsonWriter.toString();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return "";
    }

    public void sammenligne(OppgaveDto oppgave) {
        assertThat(oppgave.getEksternId()).isEqualTo(behandlingProsessEventDto.getEksternId());
        assertThat(oppgave.getOpprettetTidspunkt()).isEqualTo(behandlingProsessEventDto.getOpprettetBehandling());
        assertThat(oppgave.getBehandlingstype().getKode()).isEqualTo(behandlingProsessEventDto.getBehandlingTypeKode());
    }
}
