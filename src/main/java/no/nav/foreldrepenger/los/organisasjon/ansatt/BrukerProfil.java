package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.util.UUID;

public record BrukerProfil(UUID uid, String ident, String navn, String ansattAvdeling) {
}
