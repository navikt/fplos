
import React, { useCallback, Fragment, FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';
import Lenke from 'nav-frontend-lenker';
import { Undertittel, Normaltekst } from 'nav-frontend-typografi';

import { useRestApi, useRestApiRunner, useGlobalStateRestApiData } from 'data/rest-api-hooks';
import { RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/restApiPaths';
import { getFpsakHref, getFptilbakeHref } from 'app/paths';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import Oppgave from '../../oppgaveTsType';

const getClickEvent = (openFpsak, oppgave) => () => openFpsak(oppgave);

const EMPTY_ARRAY = [];

/**
 * SistBehandledeSaker
 *
 * Denne komponenten viser de tre siste fagsakene en nav-ansatt har behandlet.
 */
const SistBehandledeSaker: FunctionComponent = () => {
  const { data: sistBehandledeSaker = EMPTY_ARRAY } = useRestApi<Oppgave[]>(RestApiPathsKeys.BEHANDLEDE_OPPGAVER);
  const fpsakUrl = useGlobalStateRestApiData<{ verdi?: string }>(RestApiGlobalStatePathsKeys.FPSAK_URL);
  const fptilbakeUrl = useGlobalStateRestApiData<{ verdi?: string }>(RestApiGlobalStatePathsKeys.FPTILBAKE_URL);
  const { startRequest: hentFpsakInternBehandlingId } = useRestApiRunner<number>(RestApiPathsKeys.FPSAK_BEHANDLING_ID);

  const openFpsak = useCallback((oppgave: Oppgave) => {
    if (oppgave.system === 'FPSAK') {
      hentFpsakInternBehandlingId({ uuid: oppgave.behandlingId })
        .then((fagsystemInternId) => window.location.assign(getFpsakHref(fpsakUrl.verdi, oppgave.saksnummer, fagsystemInternId)));
    } else if (oppgave.system === 'FPTILBAKE') {
      window.location.assign(getFptilbakeHref(fptilbakeUrl.verdi, oppgave.href));
    } else throw new Error('Fagsystemet for oppgaven er ukjent');
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
