package no.nav.foreldrepenger.los.web.app.tjenester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = { "/avdelingsleder", "/avdelingsleder/", "/avdelingsleder/*" })
public class AvdelingslederDispatchToRoot extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(AvdelingslederDispatchToRoot.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher("/").forward(request, response);
        } catch (ServletException | IOException e) {
            log.error("Kunne ikke utføre forward-operasjon", e);
        }
    }
}