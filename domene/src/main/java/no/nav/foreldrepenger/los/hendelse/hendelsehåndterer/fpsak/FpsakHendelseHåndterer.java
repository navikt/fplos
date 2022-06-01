package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;

public interface FpsakHendelseHåndterer {

    String SYSTEM = Fagsystem.FPSAK.name();

    void håndter();

}
