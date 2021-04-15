package no.nav.foreldrepenger.los.klient.fpsak.dto.behandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResourceLink {

    @JsonProperty("href")
    @NotNull
    private final URI href;

    /** Link relationship type. */
    @JsonProperty("rel")
    @NotNull
    private final String rel;

    /** Http Method type. */
    @JsonProperty("type")
    @NotNull
    private final HttpMethod type;

    @JsonProperty("requestPayload")
    private Object requestPayload;

    @SuppressWarnings("unused")
    private ResourceLink() { // NOSONAR
        this((URI) null, null, null); // for Jackson
    }

    public ResourceLink(URI href, String rel, HttpMethod type) {
        this.href = href;
        this.rel = rel;
        this.type = type;
    }

    public ResourceLink(String href, String rel, HttpMethod type) {
        try {
            this.href = new URI(href);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        this.rel = rel;
        this.type = type;
    }

    public ResourceLink(String href, String rel, HttpMethod type, Object requestPayload) {
        this(href, rel, type);
        this.requestPayload = requestPayload;
    }

    /** Ctor lager default GET link. */
    public ResourceLink(String href, String rel) {
        this(href, rel, HttpMethod.GET);
    }

    public HttpMethod getType() {
        return type;
    }

    public String getRel() {
        return rel;
    }

    public URI getHref() {
        return href;
    }

    public Object getRequestPayload() {
        return requestPayload;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !this.getClass().equals(obj.getClass())) {
            return false;
        }
        var other = (ResourceLink) obj;
        return Objects.equals(this.href, other.href)
                && Objects.equals(this.rel, other.rel)
                && Objects.equals(this.type, other.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(href, rel, type);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<" + type + " " + href + " [" + rel + "]>";
    }

    public enum HttpMethod {
        POST,
        GET,
        PUT,
        PATCH,
        DELETE,
    }

    public static ResourceLink post(String href, String rel, Object requestPayload) {
        return new ResourceLink(href, rel, HttpMethod.POST, requestPayload);
    }

    public static ResourceLink get(String href, String rel, Object requestPayload) {
        return new ResourceLink(href, rel, HttpMethod.GET, requestPayload);
    }
}
