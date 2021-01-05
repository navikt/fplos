import { Location } from 'history';

import { formatQueryString, parseQueryString } from 'utils/urlUtils';

export const BASE_PATH = '/fplos';
export const AVDELINGSLEDER_PATH = 'avdelingsleder';

const emptyQueryString = (queryString) => queryString === '?' || !queryString;

const updateQueryParams = (queryString, nextParams) => {
  const prevParams = emptyQueryString(queryString) ? {} : parseQueryString(queryString);
  return formatQueryString({
    ...prevParams,
    ...nextParams,
  });
};

const getLocationWithQueryParams = (location, queryParams) => ({ ...location, search: updateQueryParams(location.search, queryParams) });

export const getAvdelingslederPanelLocationCreator = (location: Location) => (avdelingslederPanel: string) => getLocationWithQueryParams(
  location, { fane: avdelingslederPanel },
);

export const getFpsakHref = (fpsakUrl: string, saksnummer: number, fagsystemInternId?: number) => (fagsystemInternId
  ? `${fpsakUrl}/fagsak/${saksnummer}/behandling/${fagsystemInternId}/?punkt=default&fakta=default` : `${fpsakUrl}/fagsak/${saksnummer}/`);

export const getFptilbakeHref = (fptilbakeUrl: string, path: string) => `${fptilbakeUrl}${path}`;
