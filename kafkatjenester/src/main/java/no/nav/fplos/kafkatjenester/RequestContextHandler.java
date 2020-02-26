package no.nav.fplos.kafkatjenester;

import org.jboss.weld.context.RequestContext;
import org.jboss.weld.context.unbound.UnboundLiteral;

import javax.enterprise.inject.spi.CDI;
import java.util.function.Supplier;

/**
 * Kjør angitt funksjon med RequestScope aktivt.
 */
public final class RequestContextHandler {

    private RequestContextHandler() {
        // hidden ctor
    }

    public static <V> V doWithRequestContext(Supplier<V> supplier) {

        RequestContext requestContext = CDI.current().select(RequestContext.class, UnboundLiteral.INSTANCE).get();
        if (requestContext.isActive()) {
            return supplier.get();
        } else {

            try {
                requestContext.activate();
                return supplier.get();
            } finally {
                requestContext.invalidate();
                requestContext.deactivate();
            }
        }
    }

}
