package no.nav.foreldrepenger.los.migrering.dto;

import java.util.List;

public record OrgDataDto(
    List<AvdelingDataDto> avdelinger,
    List<SaksbehandlerDataDto> saksbehandlere,
    List<AvdelingSaksbehandlerDataDto> avdelingSaksbehandlere,
    List<SaksbehandlerGruppeDataDto> saksbehandlerGrupper,
    List<GruppeTilknytningDataDto> gruppeTilknytninger
) {
}
