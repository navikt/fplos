package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.Beskyttelsesbehov;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;

@ApplicationScoped
class KriterieUtleder {

    private Beskyttelsesbehov beskyttelsesbehov;

    @Inject
    KriterieUtleder(Beskyttelsesbehov beskyttelsesbehov) {
        this.beskyttelsesbehov = beskyttelsesbehov;
    }

    KriterieUtleder() {
        //CDI
    }

    Set<AndreKriterierType> utledKriterier(OppgaveGrunnlag oppgaveGrunnlag) {
        var kriterier = new HashSet<AndreKriterierType>();

        var aksjonspunkt = oppgaveGrunnlag.aksjonspunkt();
        var aktiveAksjonspunkt = aksjonspunkt
            .stream()
            .filter(a -> a.status() == Aksjonspunktstatus.OPPRETTET)
            .map(OppgaveGrunnlag.Aksjonspunkt::type)
            .collect(Collectors.toSet());
        if (aktiveAksjonspunkt.contains(OppgaveGrunnlag.AksjonspunktType.PAPIRSØKNAD)) {
            kriterier.add(AndreKriterierType.PAPIRSØKNAD);
        }
        if (aktiveAksjonspunkt.contains(OppgaveGrunnlag.AksjonspunktType.KONTROLLER_TERMINBEKREFTELSE)) {
            kriterier.add(AndreKriterierType.TERMINBEKREFTELSE);
        }
        if (aktiveAksjonspunkt.contains(OppgaveGrunnlag.AksjonspunktType.ARBEID_OG_INNTEKT)) {
            kriterier.add(AndreKriterierType.ARBEID_INNTEKT);
        }
        if (aktiveAksjonspunkt.contains(OppgaveGrunnlag.AksjonspunktType.VURDER_FORMKRAV)) {
            kriterier.add(AndreKriterierType.VURDER_FORMKRAV);
        }

        var saksegenskaper = oppgaveGrunnlag.saksegenskaper();
        if (aktiveAksjonspunkt.contains(OppgaveGrunnlag.AksjonspunktType.AUTOMATISK_MARKERING_SOM_UTLAND) && (
            saksegenskaper.contains(OppgaveGrunnlag.Saksegenskap.BOSATT_UTLAND) || saksegenskaper.contains(
                OppgaveGrunnlag.Saksegenskap.EØS_BOSATT_NORGE))) {
            kriterier.add(AndreKriterierType.VURDER_EØS_OPPTJENING);
        }
        if (aktiveAksjonspunkt.contains(OppgaveGrunnlag.AksjonspunktType.VURDER_NÆRING) && saksegenskaper.contains(
            OppgaveGrunnlag.Saksegenskap.NÆRING)) {
            kriterier.add(AndreKriterierType.NÆRING);
        }
        if (saksegenskaper.contains(OppgaveGrunnlag.Saksegenskap.PRAKSIS_UTSETTELSE)) {
            kriterier.add(AndreKriterierType.PRAKSIS_UTSETTELSE);
        }
        if (saksegenskaper.contains(OppgaveGrunnlag.Saksegenskap.EØS_BOSATT_NORGE)) {
            kriterier.add(AndreKriterierType.EØS_SAK);
        }
        if (saksegenskaper.contains(OppgaveGrunnlag.Saksegenskap.BOSATT_UTLAND)) {
            kriterier.add(AndreKriterierType.UTLANDSSAK);
        }
        if (saksegenskaper.contains(OppgaveGrunnlag.Saksegenskap.SAMMENSATT_KONTROLL)) {
            kriterier.add(AndreKriterierType.SAMMENSATT_KONTROLL);
        }
        if (saksegenskaper.contains(OppgaveGrunnlag.Saksegenskap.DØD)) {
            kriterier.add(AndreKriterierType.DØD);
        }
        if (saksegenskaper.contains(OppgaveGrunnlag.Saksegenskap.BARE_FAR_RETT)) {
            kriterier.add(AndreKriterierType.BARE_FAR_RETT);
        }
        if (saksegenskaper.contains(OppgaveGrunnlag.Saksegenskap.HASTER)) {
            kriterier.add(AndreKriterierType.HASTER);
        }

        var behandlingsegenskaper = oppgaveGrunnlag.behandlingsegenskaper();
        if (behandlingsegenskaper.contains(OppgaveGrunnlag.Behandlingsegenskap.MOR_UKJENT_UTLAND)) {
            kriterier.add(AndreKriterierType.MOR_UKJENT_UTLAND);
        }
        if (behandlingsegenskaper.contains(OppgaveGrunnlag.Behandlingsegenskap.SYKDOMSVURDERING)) {
            kriterier.add(AndreKriterierType.VURDER_SYKDOM);
        }
        if (behandlingsegenskaper.contains(OppgaveGrunnlag.Behandlingsegenskap.TILBAKEKREVING_OVER_FIRE_RETTSGEBYR)) {
            kriterier.add(AndreKriterierType.OVER_FIRE_RETTSGEBYR);
        }
        if (oppgaveGrunnlag.behandlingstype().gjelderTilbakebetaling() && !behandlingsegenskaper.isEmpty() && !behandlingsegenskaper.contains(
            OppgaveGrunnlag.Behandlingsegenskap.TILBAKEKREVING_SENDT_VARSEL)) {
            kriterier.add(AndreKriterierType.IKKE_VARSLET);
        }
        if (!oppgaveGrunnlag.refusjonskrav() || behandlingsegenskaper.contains(OppgaveGrunnlag.Behandlingsegenskap.DIREKTE_UTBETALING)) {
            kriterier.add(AndreKriterierType.UTBETALING_TIL_BRUKER);
        }
        if (oppgaveGrunnlag.faresignaler() || behandlingsegenskaper.contains(OppgaveGrunnlag.Behandlingsegenskap.FARESIGNALER)) {
            kriterier.add(AndreKriterierType.VURDER_FARESIGNALER);
        }

        var behandlingsårsaker = oppgaveGrunnlag.behandlingsårsaker();
        if (behandlingsårsaker.contains(OppgaveGrunnlag.Behandlingsårsak.PLEIEPENGER)) {
            kriterier.add(AndreKriterierType.PLEIEPENGER);
        }
        if (behandlingsårsaker.contains(OppgaveGrunnlag.Behandlingsårsak.UTSATT_START)) {
            kriterier.add(AndreKriterierType.UTSATT_START);
        }
        if (behandlingsårsaker.contains(OppgaveGrunnlag.Behandlingsårsak.OPPHØR_NY_SAK)) {
            kriterier.add(AndreKriterierType.NYTT_VEDTAK);
        }
        if (behandlingsårsaker.contains(OppgaveGrunnlag.Behandlingsårsak.BERØRT)) {
            kriterier.add(AndreKriterierType.BERØRT_BEHANDLING);
        }
        if (behandlingsårsaker.contains(OppgaveGrunnlag.Behandlingsårsak.KLAGE_TILBAKEBETALING)) {
            kriterier.add(AndreKriterierType.KLAGE_PÅ_TILBAKEBETALING);
        }
        if (oppgaveGrunnlag.ytelse() == FagsakYtelseType.FORELDREPENGER && oppgaveGrunnlag.behandlingstype() == BehandlingType.REVURDERING
            && behandlingsårsaker.contains(OppgaveGrunnlag.Behandlingsårsak.SØKNAD)) {
            kriterier.add(AndreKriterierType.ENDRINGSSØKNAD);
        }
        if (oppgaveGrunnlag.behandlingstype() == BehandlingType.REVURDERING && behandlingsårsaker.contains(
            OppgaveGrunnlag.Behandlingsårsak.INNTEKTSMELDING) && behandlingsårsaker.size() == 1) {
            kriterier.add(AndreKriterierType.REVURDERING_INNTEKTSMELDING);
        }

        //Fpsak og fptilbake har forskjellig håndtering av retur fra beslutter.
            // Fpsak avbryter beslutter AP
            // Fptilbake fjerner beslutter AP, andre ganger holdes beslutter ap opprettet. Avhengig av hvor langt tilbake behandlingen hopper????
        if (oppgaveGrunnlag.behandlingstype().gjelderTilbakebetaling() && opprettetBeslutterAp(aksjonspunkt)) {
            if (opprettetAnnetApEnnBeslutter(aksjonspunkt)) {
                kriterier.add(AndreKriterierType.RETURNERT_FRA_BESLUTTER);
            } else {
                kriterier.add(AndreKriterierType.TIL_BESLUTTER);
            }
        }
        if (!oppgaveGrunnlag.behandlingstype().gjelderTilbakebetaling()) {
            if (opprettetBeslutterAp(aksjonspunkt)) {
                kriterier.add(AndreKriterierType.TIL_BESLUTTER);
            }
            if (harAvbruttBeslutterAp(aksjonspunkt)) {
                kriterier.add(AndreKriterierType.RETURNERT_FRA_BESLUTTER);
            }
        }

        kriterier.addAll(beskyttelsesbehov.getBeskyttelsesKriterier(oppgaveGrunnlag.saksnummer()));

        return kriterier;
    }

    private static boolean opprettetAnnetApEnnBeslutter(List<OppgaveGrunnlag.Aksjonspunkt> aksjonspunkt) {
        return aksjonspunkt.stream().anyMatch(a -> a.status() == Aksjonspunktstatus.OPPRETTET && a.type() != OppgaveGrunnlag.AksjonspunktType.TIL_BESLUTTER);
    }

    private static boolean opprettetBeslutterAp(List<OppgaveGrunnlag.Aksjonspunkt> aksjonspunkter) {
        return aksjonspunkter.stream()
            .anyMatch(a -> a.type() == OppgaveGrunnlag.AksjonspunktType.TIL_BESLUTTER && a.status() == Aksjonspunktstatus.OPPRETTET);
    }

    private static boolean harAvbruttBeslutterAp(List<OppgaveGrunnlag.Aksjonspunkt> aksjonspunkt) {
        return aksjonspunkt
            .stream()
            .anyMatch(a -> a.type() == OppgaveGrunnlag.AksjonspunktType.TIL_BESLUTTER && a.status() == Aksjonspunktstatus.AVBRUTT);
    }
}
