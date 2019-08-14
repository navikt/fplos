package no.nav.foreldrepenger.los.web.app.util;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@RequestScoped
public class UtilBeans {

    private HttpServletRequest request;

    UtilBeans() {
        //
    }

    @Inject
    public UtilBeans(HttpServletRequest request) {
        this.request = request;
    }

    @Produces
    @RequestURL
    public String getRequestPath(InjectionPoint injectionPoint) {
        StringBuilder stringBuilder = new StringBuilder();

        if (injectionPoint.getAnnotated().getAnnotation(RequestURL.class).fullUrl()) {
            stringBuilder.append(request.getScheme())
                    .append("://")
                    .append(request.getLocalName())
                    .append(":") // NOSONAR
                    .append(request.getLocalPort());
        }

        stringBuilder.append(request.getContextPath())
                .append(request.getServletPath());
        return stringBuilder.toString();
    }
}
