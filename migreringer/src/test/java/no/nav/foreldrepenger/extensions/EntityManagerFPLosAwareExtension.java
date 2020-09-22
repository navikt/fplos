package no.nav.foreldrepenger.extensions;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.dbstoette.Databaseskjemainitialisering;
import no.nav.vedtak.felles.testutilities.db.EntityManagerAwareExtension;

public class EntityManagerFPLosAwareExtension extends EntityManagerAwareExtension {

    private static final Logger LOG = LoggerFactory.getLogger(EntityManagerFPLosAwareExtension.class);

    @Override
    protected void init() {
        if (System.getenv("MAVEN_CMD_LINE_ARGS") == null) {
            // prøver alltid migrering hvis endring, ellers funker det dårlig i IDE.
            LOG.warn("Kjører migreringer");
            Databaseskjemainitialisering.migrerUnittestSkjemaer();
        }
        // Maven kjører testen
        // kun kjør migreringer i migreringer modul

        Databaseskjemainitialisering.settPlaceholdereOgJdniOppslag();
    }
}
