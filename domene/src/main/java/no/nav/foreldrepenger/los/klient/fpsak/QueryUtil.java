package no.nav.foreldrepenger.los.klient.fpsak;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

final class QueryUtil {

    private QueryUtil() {

    }

    static List<NameValuePair> split(String q) {
        var parts = StringUtils.split(q, '=');
        if (parts.length % 2 != 0) {
            throw new IllegalArgumentException("Uventet query " + q + ", forventet likt antall elementer");
        }
        return IntStream.iterate(0, i -> i + 2)
                .limit(1)
                .mapToObj(i -> new BasicNameValuePair(parts[i], parts[i + 1]))
                .collect(Collectors.toList());
    }

    static WebTarget addQueryParams(URI href, WebTarget target) {
        for (var q : QueryUtil.split(href.getQuery())) {
            target = target.queryParam(q.getName(), q.getValue());
        }
        return target;
    }

    static UriBuilder addQueryParams(URI href, UriBuilder target) {
        for (var q : QueryUtil.split(href.getQuery())) {
            target = target.queryParam(q.getName(), q.getValue());
        }
        return target;
    }
}
