#!/usr/bin/env bash

if test -f /var/run/secrets/nais.io/defaultDS/username;
then
   export  DEFAULTDS_USERNAME=$(cat /var/run/secrets/nais.io/defaultDS/username)
   echo "Setting DEFAULTDS_USERNAME to $DEFAULTDS_USERNAME"
fi

if test -f /var/run/secrets/nais.io/defaultDS/password;
then
    export  DEFAULTDS_PASSWORD=$(cat /var/run/secrets/nais.io/defaultDS/password)
    echo "Setting DEFAULTDS_PASSWORD"
fi

if test -f /var/run/secrets/nais.io/defaultDSconfig/jdbc_url;
then
    export  DEFAULTDS_URL=$(cat /var/run/secrets/nais.io/defaultDSconfig/jdbc_url)
    echo "Setting DEFAULTDS_URL til $DEFAULTDS_URL"
fi

if test -f /var/run/secrets/nais.io/srvfplos/password;
then
    export  SYSTEMBRUKER_PASSWORD=$(cat /var/run/secrets/nais.io/srvfplos/password)
    echo "Setting SYSTEMBRUKER_PASSWORD"
fi

if test -f /var/run/secrets/nais.io/srvfplos/username;
then
    export  SYSTEMBRUKER_USERNAME=$(cat /var/run/secrets/nais.io/srvfplos/username)
    echo "Setting SYSTEMBRUKER_USERNAME"
fi

if test -f /var/run/secrets/nais.io/ldap/username;
then
  export LDAP_USERNAME=$(cat /var/run/secrets/nais.io/ldap/username)
  echo "Setting LDAP_USERNAME"
fi

if test -f /var/run/secrets/nais.io/ldap/password;
then
  export LDAP_PASSWORD=$(cat /var/run/secrets/nais.io/ldap/password)
  echo "Setting LDAP_PASSWORD"
fi
