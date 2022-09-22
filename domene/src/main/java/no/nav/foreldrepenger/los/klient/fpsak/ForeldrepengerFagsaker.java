package no.nav.foreldrepenger.los.klient.fpsak;

import java.net.URI;
import java.util.List;

import no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak.FagsakDto;

public interface ForeldrepengerFagsaker {

    List<FagsakDto> finnFagsaker(String søkestreng);

    <T> T get(URI href, Class<T> cls);

}
