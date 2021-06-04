import { Location } from 'history';

import { formatQueryString, parseQueryString } from 'utils/urlUtils';

export const BASE_PATH = '/fplos';
export const AVDELINGSLEDER_PATH = 'avdelingsleder';

const emptyQueryString = (queryString: string) => queryString === '?' || !queryString;

const updateQueryParams = (queryString: string, nextParams: Record<string, string>) => {
  const prevParams = emptyQueryString(queryString) ? {} : parseQueryString(queryString);
  return formatQueryString({
    ...prevParams,
    ...nextParams,
  });
};

const getLocationWithQueryParams = (location: Location, queryParams: Record<string, string>) => ({
  ...location,
  search: updateQueryParams(location.search, queryParams),
});

export const getAvdelingslederPanelLocationCreator = (location: Location) => (avdelingslederPanel: string) => getLocationWithQueryParams(
  location, { fane: avdelingslederPanel },
);

export const getHref = (fpsakUrl: string, saksnummer: number, fagsystemInternId?: string) => (fagsystemInternId
  ? `${fpsakUrl}/fagsak/${saksnummer}/behandling/${fagsystemInternId}/?punkt=default&fakta=default` : `${fpsakUrl}/fagsak/${saksnummer}/`);

export const åpneFagsak = (
  fpsakUrl: string,
  fptilbakeUrl: string,
  system: string,
  saksnummer: number,
  behandlingId?: string,
) => {
  if (system === 'FPSAK' || system === 'FPTILBAKE') {
    window.location.assign(getHref(system === 'FPSAK' ? fpsakUrl : fptilbakeUrl, saksnummer, behandlingId));
  } else {
    throw new Error('Fagsystemet for oppgaven er ukjent');
  }
};
