package no.nav.fplos.verdikjedetester.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDto;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import no.nav.vedtak.felles.integrasjon.kafka.FpsakBehandlingProsessEventDto;

import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;

public class AksjonspunkteventTestInfo {

    private String avdeling;
    private String fagsakYtelseType;
    FpsakBehandlingProsessEventDto.Builder behandlingProsessEventDtoBuilder;
    BehandlingProsessEventDto behandlingProsessEventDto;
    private ObjectMapper objectMapper = new ObjectMapper();

    private final String DEFAULT = "DEFAULT_TEST";

    private final static String DEFAULT_AKTØR_ID = "3";
    private final static String DEFAULT_BEHANDLING_STEG_KODE = "BEHANDLING_STEG";
    private final static String DEFAULT_BEHANDLING_STATUS_KODE = "UTRED";
    private final static String DEFAULT_FAGSYSTEM_KODE = "FPSAK";


    AksjonspunkteventTestInfo(Long behandlingId, String behandlendeEnhet, Long saksnummer, String behandlingtypeKode
            , String fagsakYtelseTypeKode) {
        behandlingProsessEventDtoBuilder = FpsakBehandlingProsessEventDto.builder().medBehandlingId(behandlingId).medBehandlendeEnhet(behandlendeEnhet)
                .medBehandlingTypeKode(behandlingtypeKode).medSaksnummer(""+saksnummer).medYtelseTypeKode(fagsakYtelseTypeKode)
                .medOpprettetBehandling(LocalDateTime.now().truncatedTo(MILLIS)).medAktørId(DEFAULT_AKTØR_ID)
                .medBehandlingSteg(DEFAULT_BEHANDLING_STEG_KODE).medBehandlingStatus(DEFAULT_BEHANDLING_STATUS_KODE);
                //.medSystem(DEFAULT_FAGSYSTEM_KODE);
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
        assertThat(oppgave.getBehandlingId()).isEqualTo(behandlingProsessEventDto.getBehandlingId());
        assertThat(oppgave.getOpprettetTidspunkt()).isEqualTo(behandlingProsessEventDto.getOpprettetBehandling());
        assertThat(oppgave.getBehandlingstype().getKode()).isEqualTo(behandlingProsessEventDto.getBehandlingTypeKode());
    }
}
