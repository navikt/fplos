import React, {
  ReactElement, FunctionComponent,
} from 'react';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import LoadingPanel from 'sharedComponents/LoadingPanel';
import useGlobalStateRestApi from '../data/rest-api-hooks/useGlobalStateRestApi';
import RestApiState from '../data/rest-api-hooks/RestApiState';

interface OwnProps {
  children: ReactElement;
}

const AppConfigResolver: FunctionComponent<OwnProps> = ({
  children,
}) => {
  const { state: stateNavAnsatt } = useGlobalStateRestApi(RestApiGlobalStatePathsKeys.NAV_ANSATT);
  const { state: stateKodeverk } = useGlobalStateRestApi(RestApiGlobalStatePathsKeys.KODEVERK);
  const { state: stateFpsakUrl } = useGlobalStateRestApi(RestApiGlobalStatePathsKeys.FPSAK_URL);
  const { state: stateFptilbakeUrl } = useGlobalStateRestApi(RestApiGlobalStatePathsKeys.FPTILBAKE_URL);

  if (stateNavAnsatt === RestApiState.LOADING
    || stateKodeverk === RestApiState.LOADING
    || stateFpsakUrl === RestApiState.LOADING
    || stateFptilbakeUrl === RestApiState.LOADING) {
    return <LoadingPanel />;
  }
  return children;
};

export default AppConfigResolver;
