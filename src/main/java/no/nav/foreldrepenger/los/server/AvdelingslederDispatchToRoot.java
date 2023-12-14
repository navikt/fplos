package no.nav.foreldrepenger.los.server;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
