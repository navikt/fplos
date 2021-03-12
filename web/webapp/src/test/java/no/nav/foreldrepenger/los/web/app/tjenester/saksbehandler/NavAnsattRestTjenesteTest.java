package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.vedtak.felles.integrasjon.ldap.LdapBruker;

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
        var brukerUtenforSaksbehandlerGruppe = getTestBruker();
        var brukerISaksbehandlerGruppe = getTestBruker(gruppenavnSaksbehandler);

        var innloggetBrukerUtenSaksbehandlerRettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerUtenforSaksbehandlerGruppe);
        var innloggetBrukerMedSaksbehandlerRettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerISaksbehandlerGruppe);

        assertThat(innloggetBrukerUtenSaksbehandlerRettighet.getKanSaksbehandle()).isFalse();
        assertThat(innloggetBrukerMedSaksbehandlerRettighet.getKanSaksbehandle()).isTrue();
    }

    @Test
    public void skalMappeVeilederGruppeTilKanVeiledeRettighet() {
        var brukerUtenforVeilederGruppe = getTestBruker();
        var brukerIVeilederGruppe = getTestBruker(gruppenavnVeileder);
        var innloggetBrukerUtenVeilederRettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerUtenforVeilederGruppe);
        var innloggetBrukerMedVeilederRettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerIVeilederGruppe);
        assertThat(innloggetBrukerUtenVeilederRettighet.getKanVeilede()).isFalse();
        assertThat(innloggetBrukerMedVeilederRettighet.getKanVeilede()).isTrue();
    }

    @Test
    public void skalMappeBeslutterGruppeTilKanBeslutteRettighet() {
        var brukerUtenforBeslutterGruppe = getTestBruker();
        var brukerIBeslutterGruppe = getTestBruker(gruppenavnBeslutter);
        var innloggetBrukerUtenBeslutterRettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerUtenforBeslutterGruppe);
        var innloggetBrukerMedBeslutterRettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerIBeslutterGruppe);
        assertThat(innloggetBrukerUtenBeslutterRettighet.getKanBeslutte()).isFalse();
        assertThat(innloggetBrukerMedBeslutterRettighet.getKanBeslutte()).isTrue();
    }

    @Test
    public void skalMappeEgenAnsattGruppeTilKanBehandleEgenAnsattRettighet() {
        var brukerUtenforEgenAnsattGruppe = getTestBruker();
        var brukerIEgenAnsattGruppe = getTestBruker(gruppenavnEgenAnsatt);

        var innloggetBrukerUtenEgenAnsattRettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerUtenforEgenAnsattGruppe);
        var innloggetBrukerMedEgenAnsattRettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerIEgenAnsattGruppe);

        assertThat(innloggetBrukerUtenEgenAnsattRettighet.getKanBehandleKodeEgenAnsatt()).isFalse();
        assertThat(innloggetBrukerMedEgenAnsattRettighet.getKanBehandleKodeEgenAnsatt()).isTrue();
    }

    @Test
    public void skalMappeKode6GruppeTilKanBehandleKode6Rettighet() {
        var brukerUtenforKode6Gruppe = getTestBruker();
        var brukerIKode6Gruppe = getTestBruker(gruppenavnKode6);

        var innloggetBrukerUtenKode6Rettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerUtenforKode6Gruppe);
        var innloggetBrukerMedKode6Rettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerIKode6Gruppe);

        assertThat(innloggetBrukerUtenKode6Rettighet.getKanBehandleKode6()).isFalse();
        assertThat(innloggetBrukerMedKode6Rettighet.getKanBehandleKode6()).isTrue();
    }

    @Test
    public void skalMappeKode7GruppeTilKanBehandleKode7Rettighet() {
        var brukerUtenforKode7Gruppe = getTestBruker();
        var brukerIKode7Gruppe = getTestBruker(gruppenavnKode7);

        var innloggetBrukerUtenKode7Rettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerUtenforKode7Gruppe);
        var innloggetBrukerMedKode7Rettighet = saksbehandlerTjeneste.getInnloggetBrukerDto(null, brukerIKode7Gruppe);

        assertThat(innloggetBrukerUtenKode7Rettighet.getKanBehandleKode7()).isFalse();
        assertThat(innloggetBrukerMedKode7Rettighet.getKanBehandleKode7()).isTrue();
    }

    private static LdapBruker getTestBruker(String ...grupper) {
        return new LdapBruker("Testbruker", Arrays.asList(grupper));
    }
}
