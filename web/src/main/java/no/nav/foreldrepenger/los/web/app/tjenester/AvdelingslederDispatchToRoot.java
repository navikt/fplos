package no.nav.foreldrepenger.los.web.app.tjenester;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = {"/avdelingsleder", "/avdelingsleder/", "/avdelingsleder/*"})
public class AvdelingslederDispatchToRoot extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(AvdelingslederDispatchToRoot.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher("/").forward(request, response);
        } catch (ServletException | IOException e) {
            LOG.error("Kunne ikke utføre forward-operasjon", e);
        }
    }
}
