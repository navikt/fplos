package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer;

import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;

public interface FpsakHendelseHåndterer {

    String system = Fagsystem.FPSAK.name();

    void håndter();

}
