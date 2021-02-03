import { useHistory, useRouteMatch, match as RouterMatch } from 'react-router-dom';
import { Location } from 'history';

import { parseQueryString } from 'utils/urlUtils';

const defaultConfig = {
  paramName: '',
  parse: (a: any) => a,
  isQueryParam: false,
};

interface Config {
  paramName?: string,
  parse?: (a: any) => any,
  isQueryParam?: boolean,
}

const mapMatchToParam = (match: RouterMatch, location: Location, trackingConfig: Required<Config>) => {
  const params = trackingConfig.isQueryParam ? parseQueryString(location.search) : match.params as Record<string, string>;
  return trackingConfig.parse(params[trackingConfig.paramName]);
};

function useTrackRouteParam<T>(config: Config): { location: Location; selected: T } {
  const trackingConfig = { ...defaultConfig, ...config };

  const history = useHistory();
  const { location } = history;
  const match = useRouteMatch();

  const paramFromUrl = mapMatchToParam(match, location, trackingConfig);
  return {
    location,
    selected: paramFromUrl,
  };
}

export default useTrackRouteParam;
