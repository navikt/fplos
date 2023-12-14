package no.nav.foreldrepenger.los;


import java.util.TimeZone;

import no.nav.foreldrepenger.los.Databaseskjemainitialisering;
import no.nav.vedtak.felles.testutilities.db.EntityManagerAwareExtension;

public class JpaExtension extends EntityManagerAwareExtension {

    @Override
    protected void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Oslo"));
        Databaseskjemainitialisering.initUnitTestDataSource();
        Databaseskjemainitialisering.migrerUnittestSkjemaer();
    }
}
