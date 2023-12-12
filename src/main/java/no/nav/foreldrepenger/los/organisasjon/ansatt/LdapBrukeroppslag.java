package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.util.regex.Matcher;
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


public class LdapBrukeroppslag {

    private final LdapContext context;
    private final LdapName searchBase;

    private static final Pattern IDENT_PATTERN = Pattern.compile("^\\p{LD}+$");

    public LdapBrukeroppslag() {
        this(LdapInnlogging.lagLdapContext(), lagLdapSearchBase());
    }

    LdapBrukeroppslag(LdapContext context, LdapName searcBase) {
        this.context = context;
        this.searchBase = searcBase;
    }

    public String hentBrukersNavn(String ident) {
        var result = ldapSearch(ident);
        return getDisplayName(result);
    }

    private SearchResult ldapSearch(String ident) {
        if (ident == null || ident.isEmpty()) {
            throw new TekniskException("F-344885", "Kan ikke slå opp brukernavn uten å ha ident");
        }
        Matcher matcher = IDENT_PATTERN.matcher(ident);
        if (!matcher.matches()) {
            throw new TekniskException("F-271934", String.format("Mulig LDAP-injection forsøk. Søkte med ugyldig ident '%s'", ident));
        }

        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setCountLimit(1);
        String søkestreng = String.format("(cn=%s)", ident);
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

    protected String getDisplayName(SearchResult result) {
        String attributeName = "displayName";
        Attribute displayName = find(result, attributeName);
        try {
            return displayName.get().toString();
        } catch (NamingException e) {
            throw new TekniskException("F-314006", String.format("Kunne ikke hente ut attributtverdi %s fra %s", attributeName, attributeName), e);
        }
    }

    private static Attribute find(SearchResult element, String attributeName) {
        Attribute attribute = element.getAttributes().get(attributeName);
        if (attribute == null) {
            throw new IntegrasjonException("F-828846", String.format("Resultat fra LDAP manglet påkrevet attributtnavn %s", attributeName));
        }
        return attribute;
    }

    private static LdapName lagLdapSearchBase() {
        String userBaseDn = LdapInnlogging.getRequiredProperty("ldap.user.basedn");
        try {
            return new LdapName(userBaseDn); // NOSONAR
        } catch (InvalidNameException e) {
            throw new IntegrasjonException("F-703197", String.format("Kunne ikke definere base-søk mot LDAP %s", userBaseDn), e);
        }
    }
}
