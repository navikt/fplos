import React, {
  ReactElement, FunctionComponent,
} from 'react';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import LoadingPanel from 'sharedComponents/LoadingPanel';
import { RestApiState, useGlobalStateRestApi } from '../data/rest-api-hooks';

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
  const { state: stateDriftsmeldinger } = useGlobalStateRestApi(RestApiGlobalStatePathsKeys.DRIFTSMELDINGER);

  if (stateNavAnsatt === RestApiState.LOADING
    || stateKodeverk === RestApiState.LOADING
    || stateFpsakUrl === RestApiState.LOADING
    || stateFptilbakeUrl === RestApiState.LOADING
    || stateDriftsmeldinger === RestApiState.LOADING) {
    return <LoadingPanel />;
  }
  return children;
};

export default AppConfigResolver;
