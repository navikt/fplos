package no.nav.foreldrepenger.los.web.app.sikkerhetsfilter;

import static no.nav.vedtak.log.util.LoggerUtils.removeLineBreaks;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter(urlPatterns = "/*")
public final class XSSFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(XSSFilter.class);

    static class FilteredRequest extends HttpServletRequestWrapper {

        public FilteredRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String paramName) {//ikke i bruk??
            LOG.info("XSSFilter getParameter brukes.");
            var value = super.getParameter(paramName);
            value = hvitvaksKunBokstaver(value, "parameter");
            return value;
        }

        @Override
        public String[] getParameterValues(String paramName) {//ikke i bruk??
            LOG.info("XSSFilter getParameterVaules brukes.");
            var values = super.getParameterValues(paramName);
            for (var index = 0; index < values.length; index++) {
                values[index] = hvitvaksKunBokstaver(values[index], "parameter");
            }
            return values;
        }

        @Override
        public String getQueryString() {
            LOG.info("XSSFilter getQueryString brukes.");
            return hvitvaskBokstaverOgVanligeTegn(super.getQueryString(), "query");
        }

        @Override
        public Cookie[] getCookies() {
            LOG.info("XSSFilter getCookies brukes.");
            var cookies = super.getCookies();
            if (cookies != null) {
                for (var cooky : cookies) {
                    cooky.setValue(hvitvaskCookie(cooky.getValue(), "cookie")); //NOSONAR
                }
            }
            return cookies;
        }

        @Override
        public String getHeader(String name) {//ikke i bruk?? .. brukes av swagger
            LOG.info("XSSFilter getHeader brukes.");
            return hvitvaksKunBokstaver(super.getHeader(name), "header");
        }

        private String hvitvaksKunBokstaver(String unsanitizedString, String type) {
            var result = SimpelHvitvasker.hvitvaskKunBokstaver(unsanitizedString);
            return logHvisForskjellig(unsanitizedString, type, result);
        }

        private String hvitvaskBokstaverOgVanligeTegn(String unsanitizedString, String type) {
            var result = SimpelHvitvasker.hvitvaskBokstaverOgVanligeTegn(unsanitizedString);
            return logHvisForskjellig(unsanitizedString, type, result);
        }

        private String hvitvaskCookie(String unsanitizedString, String type) {
            var result = SimpelHvitvasker.hvitvaskCookie(unsanitizedString);
            return logHvisForskjellig(unsanitizedString, type, result);
        }

        private String logHvisForskjellig(String unsanitizedString, String type, String result) {
            if (unsanitizedString == null || unsanitizedString.equals(result)) {
                return unsanitizedString;
            }
            LOG.info(removeLineBreaks("Sanitization av {}: fra '{}' til '{}'"), removeLineBreaks(type), removeLineBreaks(unsanitizedString), removeLineBreaks(result));//TODO (LIBELLE): flytt til sikkerhetslog //NOSONAR
            return result;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        //Denne er her kun fordi den er påkravd
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new FilteredRequest((HttpServletRequest) request), response);//NOSONAR //$NON-NLS-1$
    }


    @Override
    public void destroy() {
        //Trenges kanskje ikke??
    }
}
