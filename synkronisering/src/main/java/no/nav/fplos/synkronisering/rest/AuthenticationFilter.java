package no.nav.fplos.uuid.rest;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.StringTokenizer;

public class AuthenticationFilter implements javax.servlet.Filter {

    public static final String AUTHENTICATION_HEADER = "Authorization";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String authCredentials = httpServletRequest
                    .getHeader(AUTHENTICATION_HEADER);

            if (authenticate(authCredentials)) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                if (servletResponse instanceof HttpServletResponse) {
                    HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
                    httpServletResponse
                            .setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
        }
    }

    public boolean authenticate(String authCredentials) {
        if (null == authCredentials)
            return false;

        final String encodedUserPassword = authCredentials.replaceFirst("Basic"
                + " ", "");
        String usernameAndPassword = null;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(
                    encodedUserPassword);
            usernameAndPassword = new String(decodedBytes, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final StringTokenizer tokenizer = new StringTokenizer(
                usernameAndPassword, ":");
        final String username = tokenizer.nextToken();
        final String password = tokenizer.nextToken();

        boolean authenticationStatus = "tempService".equals(username)
                && "blueberry".equals(password);
        return authenticationStatus;
    }

    @Override
    public void destroy() {

    }
}
