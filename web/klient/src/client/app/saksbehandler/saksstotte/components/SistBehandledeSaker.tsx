import React, { useCallback, Fragment, FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';
import Lenke from 'nav-frontend-lenker';
import { Undertittel, Normaltekst } from 'nav-frontend-typografi';

import { restApiHooks, RestApiPathsKeys, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { åpneFagsak } from 'app/paths';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import Oppgave from 'types/saksbehandler/oppgaveTsType';

const getClickEvent = (openFpsak: (oppgave: Oppgave) => void, oppgave: Oppgave) => () => openFpsak(oppgave);

const EMPTY_ARRAY: Oppgave[] = [];

/**
 * SistBehandledeSaker
 *
 * Denne komponenten viser de tre siste fagsakene en nav-ansatt har behandlet.
 */
const SistBehandledeSaker: FunctionComponent = () => {
  const { data: sistBehandledeSaker = EMPTY_ARRAY } = restApiHooks.useRestApi(RestApiPathsKeys.BEHANDLEDE_OPPGAVER);
  const fpsakUrl = restApiHooks.useGlobalStateRestApiData(RestApiGlobalStatePathsKeys.FPSAK_URL);
  const fptilbakeUrl = restApiHooks.useGlobalStateRestApiData(RestApiGlobalStatePathsKeys.FPTILBAKE_URL);

  const openFpsak = useCallback((oppgave: Oppgave) => {
    åpneFagsak(fpsakUrl.verdi, fptilbakeUrl.verdi, oppgave.system, oppgave.saksnummer, oppgave.behandlingId);
  }, [fpsakUrl, fptilbakeUrl]);

  return (
    <>
      <Undertittel><FormattedMessage id="SistBehandledeSaker.SistBehandledeSaker" /></Undertittel>
      <VerticalSpacer eightPx />
      {sistBehandledeSaker.length === 0
      && <Normaltekst><FormattedMessage id="SistBehandledeSaker.IngenBehandlinger" /></Normaltekst>}
      {sistBehandledeSaker.map((sbs, index) => (
        <Fragment key={sbs.behandlingId}>
          <Normaltekst>
            {sbs.navn
              ? (
                <Lenke
                  href="#"
                  onClick={getClickEvent(openFpsak, sbs)}
                >
                  {`${sbs.navn} ${sbs.personnummer}`}
                </Lenke>
              )
              : (
                <Lenke href="#" onClick={getClickEvent(openFpsak, sbs)}>
                  <FormattedMessage id="SistBehandledeSaker.Behandling" values={{ index: index + 1 }} />
                </Lenke>
              )}
          </Normaltekst>
          <VerticalSpacer eightPx />
        </Fragment>
      ))}
    </>
  );
};

export default SistBehandledeSaker;
