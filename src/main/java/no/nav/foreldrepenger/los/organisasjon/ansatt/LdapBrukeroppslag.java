package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.util.regex.Pattern;

import javax.naming.InvalidNameException;
import javax.naming.LimitExceededException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;

import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.TekniskException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LdapBrukeroppslag {

    private static final Logger LOG = LoggerFactory.getLogger(LdapBrukeroppslag.class);
    private static final Pattern IDENT_PATTERN = Pattern.compile("^\\p{LD}+$");

    private final LdapContext context;
    private final LdapName searchBase;
    private static final String GIVEN_NAME = "givenName";
    private static final String SURNAME = "sn";
    private static final String USER_PRINCIPAL_NAME = "userPrincipalName";
    private static final String STREET_ADDRESS = "streetAddress";
    private static final String DISPLAY_NAME_ATTR = "displayName";

    public LdapBrukeroppslag() {
        this(LdapInnlogging.lagLdapContext(), lagLdapSearchBase());
    }

    LdapBrukeroppslag(LdapContext context, LdapName searcBase) {
        this.context = context;
        this.searchBase = searcBase;
    }

    public BrukerProfilRespons hentBrukerProfil(String ident) {
        var result = ldapSearch(ident.trim());
        var displayName = find(result, DISPLAY_NAME_ATTR);
        var givenName = find(result, GIVEN_NAME);
        var surname = find(result, SURNAME);
        var upn = find(result, USER_PRINCIPAL_NAME);
        var addressCode = find(result, STREET_ADDRESS);
        try {
            var fornavn = givenName.get().toString();
            var etternavn = surname.get().toString();
            var fornavnEtternavn = fornavn + " " + etternavn;
            var navn = displayName.get().toString();
            var epostAdresse = upn.get().toString();
            var ansattKontornummer = addressCode.get().toString();
            if (!epostAdresse.contains("@nav.no")) {
                LOG.info("LDAP: fant ikke gyldig epostadresse for bruker {}", ident);
            }
            return new BrukerProfilRespons(ident, navn, fornavnEtternavn,  epostAdresse, ansattKontornummer);
        } catch (NamingException e) {
            throw new TekniskException("F-314006", String.format("Kunne ikke hente ut attributtverdi for ident %s", ident), e);
        }
    }

    private SearchResult ldapSearch(String ident) {
        if (ident == null || ident.isEmpty()) {
            throw new TekniskException("F-344885", "Kan ikke slå opp brukernavn uten å ha ident");
        }
        var matcher = IDENT_PATTERN.matcher(ident);
        if (!matcher.matches()) {
            throw new TekniskException("F-271934", String.format("Mulig LDAP-injection forsøk. Søkte med ugyldig ident '%s'", ident));
        }

        var controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setCountLimit(1);
        controls.setReturningAttributes(new String[]{DISPLAY_NAME_ATTR, GIVEN_NAME, SURNAME, USER_PRINCIPAL_NAME, STREET_ADDRESS});
        var søkestreng = String.format("(cn=%s)", ident);
        try {
            var result = context.search(searchBase, søkestreng, controls); // NOSONAR
            if (result.hasMoreElements()) {
                return result.nextElement();
            }
            throw new IntegrasjonException("F-418891", String.format("Fikk ingen treff på søk mot LDAP etter ident %s", ident));
        } catch (LimitExceededException lee) {
            throw new IntegrasjonException("F-137440",
                String.format("Forventet ett unikt resultat på søk mot LDAP etter ident %s, men fikk flere treff", ident), lee);
        } catch (NamingException e) {
            throw new IntegrasjonException("F-690609", String.format("Uventet feil ved LDAP-søk %s", søkestreng), e);
        }
    }

    private static Attribute find(SearchResult element, String attributeName) {
        var attribute = element.getAttributes().get(attributeName);
        if (attribute == null) {
            throw new IntegrasjonException("F-828846", String.format("Resultat fra LDAP manglet påkrevet attributtnavn %s", attributeName));
        }
        return attribute;
    }

    private static LdapName lagLdapSearchBase() {
        var userBaseDn = LdapInnlogging.getRequiredProperty("ldap.user.basedn");
        try {
            return new LdapName(userBaseDn); // NOSONAR
        } catch (InvalidNameException e) {
            throw new IntegrasjonException("F-703197", String.format("Kunne ikke definere base-søk mot LDAP %s", userBaseDn), e);
        }
    }

    public record BrukerProfilRespons(String ident, String navn, String fornavnEtternavn, String epostAdresse, String ansattEnhet) { }

}
