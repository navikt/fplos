package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer;

import java.util.Optional;

import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;

public final class FagsakEgenskaper {
    private FagsakEgenskaper() {
    }

    public static boolean fagsakErMarkertBosattUtland(LosBehandlingDto behandlingDto) {
        return Optional.ofNullable(behandlingDto).map(LosBehandlingDto::fagsakEgenskaper)
            .filter(FagsakEgenskaper::fagsakErMarkertBosattUtland).isPresent();
    }

    public static boolean fagsakErMarkertBosattUtland(LosFagsakEgenskaperDto egenskaperDto) {
        return Optional.ofNullable(egenskaperDto)
            .map(LosFagsakEgenskaperDto::fagsakMarkering)
            .filter(LosFagsakEgenskaperDto.FagsakMarkering.BOSATT_UTLAND::equals)
            .isPresent();
    }

    public static boolean fagsakErMarkertEØSBosattNorge(LosBehandlingDto behandlingDto) {
        return Optional.ofNullable(behandlingDto).map(LosBehandlingDto::fagsakEgenskaper)
            .filter(FagsakEgenskaper::fagsakErMarkertEØSBosattNorge).isPresent();
    }

    public static boolean fagsakErMarkertEØSBosattNorge(LosFagsakEgenskaperDto egenskaperDto) {
        return Optional.ofNullable(egenskaperDto)
            .map(LosFagsakEgenskaperDto::fagsakMarkering)
            .filter(LosFagsakEgenskaperDto.FagsakMarkering.EØS_BOSATT_NORGE::equals)
            .isPresent();
    }

    public static boolean fagsakErMarkertSammensattKontroll(LosBehandlingDto behandlingDto) {
        return Optional.ofNullable(behandlingDto).map(LosBehandlingDto::fagsakEgenskaper)
            .filter(FagsakEgenskaper::fagsakErMarkertSammensattKontroll).isPresent();
    }

    public static boolean fagsakErMarkertSammensattKontroll(LosFagsakEgenskaperDto egenskaperDto) {
        return Optional.ofNullable(egenskaperDto)
            .map(LosFagsakEgenskaperDto::fagsakMarkering)
            .filter(LosFagsakEgenskaperDto.FagsakMarkering.SAMMENSATT_KONTROLL::equals)
            .isPresent();
    }

}
