package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.dto.InnloggetNavAnsattDto;
import no.nav.vedtak.felles.integrasjon.ldap.LdapBruker;

import static org.assertj.core.api.Assertions.assertThat;

public class NavAnsattRestTjenesteTest {
    private static final String gruppenavnSaksbehandler = "Saksbehandler";
    private static final String gruppenavnVeileder = "Veileder";
    private static final String gruppenavnBeslutter = "Beslutter";
    private static final String gruppenavnEgenAnsatt = "EgenAnsatt";
    private static final String gruppenavnKode6 = "Kode6";
    private static final String gruppenavnKode7 = "Kode7";
    private static final String gruppenavnOppgavestyrer = "Oppgavestyrer";

    private NavAnsattRestTjeneste saksbehandlerTjeneste;

    @BeforeEach
    public void setUp() {
        saksbehandlerTjeneste = new NavAnsattRestTjeneste(gruppenavnSaksbehandler, gruppenavnVeileder, gruppenavnBeslutter, gruppenavnEgenAnsatt, gruppenavnKode6, gruppenavnKode7, gruppenavnOppgavestyrer);
    }

    @Test
    public void skalMappeSaksbehandlerGruppeTilKanSaksbehandleRettighet() {
        LdapBruker brukerUtenforSaksbehandlerGruppe = getTestBruker();
        LdapBruker brukerISaksbehandlerGruppe = getTestBruker(gruppenavnSaksbehandler);

        InnloggetNavAnsattDto innloggetBrukerUtenSaksbehandlerRettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerUtenforSaksbehandlerGruppe);
        InnloggetNavAnsattDto innloggetBrukerMedSaksbehandlerRettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerISaksbehandlerGruppe);

        assertThat(innloggetBrukerUtenSaksbehandlerRettighet.getKanSaksbehandle()).isFalse();
        assertThat(innloggetBrukerMedSaksbehandlerRettighet.getKanSaksbehandle()).isTrue();
    }

    @Test
    public void skalMappeVeilederGruppeTilKanVeiledeRettighet() {
        LdapBruker brukerUtenforVeilederGruppe = getTestBruker();
        LdapBruker brukerIVeilederGruppe = getTestBruker(gruppenavnVeileder);

        InnloggetNavAnsattDto innloggetBrukerUtenVeilederRettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerUtenforVeilederGruppe);
        InnloggetNavAnsattDto innloggetBrukerMedVeilederRettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerIVeilederGruppe);

        assertThat(innloggetBrukerUtenVeilederRettighet.getKanVeilede()).isFalse();
        assertThat(innloggetBrukerMedVeilederRettighet.getKanVeilede()).isTrue();
    }

    @Test
    public void skalMappeBeslutterGruppeTilKanBeslutteRettighet() {
        LdapBruker brukerUtenforBeslutterGruppe = getTestBruker();
        LdapBruker brukerIBeslutterGruppe = getTestBruker(gruppenavnBeslutter);

        InnloggetNavAnsattDto innloggetBrukerUtenBeslutterRettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerUtenforBeslutterGruppe);
        InnloggetNavAnsattDto innloggetBrukerMedBeslutterRettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerIBeslutterGruppe);

        assertThat(innloggetBrukerUtenBeslutterRettighet.getKanBeslutte()).isFalse();
        assertThat(innloggetBrukerMedBeslutterRettighet.getKanBeslutte()).isTrue();
    }

    @Test
    public void skalMappeEgenAnsattGruppeTilKanBehandleEgenAnsattRettighet() {
        LdapBruker brukerUtenforEgenAnsattGruppe = getTestBruker();
        LdapBruker brukerIEgenAnsattGruppe = getTestBruker(gruppenavnEgenAnsatt);

        InnloggetNavAnsattDto innloggetBrukerUtenEgenAnsattRettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerUtenforEgenAnsattGruppe);
        InnloggetNavAnsattDto innloggetBrukerMedEgenAnsattRettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerIEgenAnsattGruppe);

        assertThat(innloggetBrukerUtenEgenAnsattRettighet.getKanBehandleKodeEgenAnsatt()).isFalse();
        assertThat(innloggetBrukerMedEgenAnsattRettighet.getKanBehandleKodeEgenAnsatt()).isTrue();
    }

    @Test
    public void skalMappeKode6GruppeTilKanBehandleKode6Rettighet() {
        LdapBruker brukerUtenforKode6Gruppe = getTestBruker();
        LdapBruker brukerIKode6Gruppe = getTestBruker(gruppenavnKode6);

        InnloggetNavAnsattDto innloggetBrukerUtenKode6Rettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerUtenforKode6Gruppe);
        InnloggetNavAnsattDto innloggetBrukerMedKode6Rettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerIKode6Gruppe);

        assertThat(innloggetBrukerUtenKode6Rettighet.getKanBehandleKode6()).isFalse();
        assertThat(innloggetBrukerMedKode6Rettighet.getKanBehandleKode6()).isTrue();
    }

    @Test
    public void skalMappeKode7GruppeTilKanBehandleKode7Rettighet() {
        LdapBruker brukerUtenforKode7Gruppe = getTestBruker();
        LdapBruker brukerIKode7Gruppe = getTestBruker(gruppenavnKode7);

        InnloggetNavAnsattDto innloggetBrukerUtenKode7Rettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerUtenforKode7Gruppe);
        InnloggetNavAnsattDto innloggetBrukerMedKode7Rettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerIKode7Gruppe);

        assertThat(innloggetBrukerUtenKode7Rettighet.getKanBehandleKode7()).isFalse();
        assertThat(innloggetBrukerMedKode7Rettighet.getKanBehandleKode7()).isTrue();
    }

    private static LdapBruker getTestBruker(String ...grupper) {
        return new LdapBruker("Testbruker", Arrays.asList(grupper));
    }
}
