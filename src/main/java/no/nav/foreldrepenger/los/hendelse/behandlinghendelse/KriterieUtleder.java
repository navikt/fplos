package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.Aksjonspunkt;
import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.AksjonspunktType;
import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.Behandlingsegenskap;
import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.Behandlingsårsak;
import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.Saksegenskap;

import java.util.HashSet;
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
            .map(Aksjonspunkt::type)
            .collect(Collectors.toSet());
        if (aktiveAksjonspunkt.contains(AksjonspunktType.PAPIRSØKNAD)) {
            kriterier.add(AndreKriterierType.PAPIRSØKNAD);
        }
        if (aktiveAksjonspunkt.contains(AksjonspunktType.KONTROLLER_TERMINBEKREFTELSE)) {
            kriterier.add(AndreKriterierType.TERMINBEKREFTELSE);
        }
        if (aktiveAksjonspunkt.contains(AksjonspunktType.ARBEID_OG_INNTEKT)) {
            kriterier.add(AndreKriterierType.ARBEID_INNTEKT);
        }
        if (aktiveAksjonspunkt.contains(AksjonspunktType.VURDER_FORMKRAV)) {
            kriterier.add(AndreKriterierType.VURDER_FORMKRAV);
        }

        var saksegenskaper = oppgaveGrunnlag.saksegenskaper();
        if (aktiveAksjonspunkt.contains(AksjonspunktType.AUTOMATISK_MARKERING_SOM_UTLAND) && (
            saksegenskaper.contains(Saksegenskap.BOSATT_UTLAND) || saksegenskaper.contains(
                Saksegenskap.EØS_BOSATT_NORGE))) {
            kriterier.add(AndreKriterierType.VURDER_EØS_OPPTJENING);
        }
        if (aktiveAksjonspunkt.contains(AksjonspunktType.VURDER_NÆRING) && saksegenskaper.contains(
            Saksegenskap.NÆRING)) {
            kriterier.add(AndreKriterierType.NÆRING);
        }
        if (saksegenskaper.contains(Saksegenskap.PRAKSIS_UTSETTELSE)) {
            kriterier.add(AndreKriterierType.PRAKSIS_UTSETTELSE);
        }
        if (saksegenskaper.contains(Saksegenskap.EØS_BOSATT_NORGE)) {
            kriterier.add(AndreKriterierType.EØS_SAK);
        }
        if (saksegenskaper.contains(Saksegenskap.BOSATT_UTLAND)) {
            kriterier.add(AndreKriterierType.UTLANDSSAK);
        }
        if (saksegenskaper.contains(Saksegenskap.SAMMENSATT_KONTROLL)) {
            kriterier.add(AndreKriterierType.SAMMENSATT_KONTROLL);
        }
        if (saksegenskaper.contains(Saksegenskap.DØD)) {
            kriterier.add(AndreKriterierType.DØD);
        }
        if (saksegenskaper.contains(Saksegenskap.BARE_FAR_RETT)) {
            kriterier.add(AndreKriterierType.BARE_FAR_RETT);
        }
        if (saksegenskaper.contains(Saksegenskap.HASTER)) {
            kriterier.add(AndreKriterierType.HASTER);
        }

        var behandlingsegenskaper = oppgaveGrunnlag.behandlingsegenskaper();
        if (behandlingsegenskaper.contains(Behandlingsegenskap.MOR_UKJENT_UTLAND)) {
            kriterier.add(AndreKriterierType.MOR_UKJENT_UTLAND);
        }
        if (behandlingsegenskaper.contains(Behandlingsegenskap.SYKDOMSVURDERING)) {
            kriterier.add(AndreKriterierType.VURDER_SYKDOM);
        }
        if (behandlingsegenskaper.contains(Behandlingsegenskap.TILBAKEKREVING_OVER_FIRE_RETTSGEBYR)) {
            kriterier.add(AndreKriterierType.OVER_FIRE_RETTSGEBYR);
        }
        if (oppgaveGrunnlag.behandlingstype().gjelderTilbakebetaling() && !behandlingsegenskaper.isEmpty()
            && !behandlingsegenskaper.contains(Behandlingsegenskap.TILBAKEKREVING_SENDT_VARSEL)) {
            kriterier.add(AndreKriterierType.IKKE_VARSLET);
        }
        if (!oppgaveGrunnlag.refusjonskrav() || behandlingsegenskaper.contains(Behandlingsegenskap.DIREKTE_UTBETALING)) {
            kriterier.add(AndreKriterierType.UTBETALING_TIL_BRUKER);
        }
        if (oppgaveGrunnlag.faresignaler() || behandlingsegenskaper.contains(Behandlingsegenskap.FARESIGNALER)) {
            kriterier.add(AndreKriterierType.VURDER_FARESIGNALER);
        }

        var behandlingsårsaker = oppgaveGrunnlag.behandlingsårsaker();
        if (behandlingsårsaker.contains(Behandlingsårsak.PLEIEPENGER)) {
            kriterier.add(AndreKriterierType.PLEIEPENGER);
        }
        if (behandlingsårsaker.contains(Behandlingsårsak.UTSATT_START)) {
            kriterier.add(AndreKriterierType.UTSATT_START);
        }
        if (behandlingsårsaker.contains(Behandlingsårsak.OPPHØR_NY_SAK)) {
            kriterier.add(AndreKriterierType.NYTT_VEDTAK);
        }
        if (behandlingsårsaker.contains(Behandlingsårsak.BERØRT)) {
            kriterier.add(AndreKriterierType.BERØRT_BEHANDLING);
        }
        if (behandlingsårsaker.contains(Behandlingsårsak.KLAGE_TILBAKEBETALING)) {
            kriterier.add(AndreKriterierType.KLAGE_PÅ_TILBAKEBETALING);
        }
        if (oppgaveGrunnlag.ytelse() == FagsakYtelseType.FORELDREPENGER && oppgaveGrunnlag.behandlingstype() == BehandlingType.REVURDERING
            && behandlingsårsaker.contains(Behandlingsårsak.SØKNAD)) {
            kriterier.add(AndreKriterierType.ENDRINGSSØKNAD);
        }
        if (oppgaveGrunnlag.behandlingstype() == BehandlingType.REVURDERING && behandlingsårsaker.contains(
            Behandlingsårsak.INNTEKTSMELDING) && behandlingsårsaker.size() == 1) {
            kriterier.add(AndreKriterierType.REVURDERING_INNTEKTSMELDING);
        }

        var besutterAP = aksjonspunkt.stream().filter(a -> a.type() == AksjonspunktType.TIL_BESLUTTER).findFirst();
        if (besutterAP.isPresent()) {
            if (besutterAP.get().status() == Aksjonspunktstatus.OPPRETTET) {
                kriterier.add(AndreKriterierType.TIL_BESLUTTER);
            } else if (besutterAP.get().status() == Aksjonspunktstatus.AVBRUTT) {
                kriterier.add(AndreKriterierType.RETURNERT_FRA_BESLUTTER);
            }
        }

        kriterier.addAll(beskyttelsesbehov.getBeskyttelsesKriterier(oppgaveGrunnlag.saksnummer()));

        return kriterier;
    }
}
