import React, {
  ReactElement, FunctionComponent,
} from 'react';

import { restApiHooks, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import LoadingPanel from 'sharedComponents/LoadingPanel';
import { RestApiState } from '../data/rest-api-hooks';

interface OwnProps {
  children: ReactElement;
}

const AppConfigResolver: FunctionComponent<OwnProps> = ({
  children,
}) => {
  const { state: stateNavAnsatt } = restApiHooks.useGlobalStateRestApi(RestApiGlobalStatePathsKeys.NAV_ANSATT);
  const { state: stateKodeverk } = restApiHooks.useGlobalStateRestApi(RestApiGlobalStatePathsKeys.KODEVERK);
  const { state: stateFpsakUrl } = restApiHooks.useGlobalStateRestApi(RestApiGlobalStatePathsKeys.FPSAK_URL);
  const { state: stateFptilbakeUrl } = restApiHooks.useGlobalStateRestApi(RestApiGlobalStatePathsKeys.FPTILBAKE_URL);
  const { state: stateDriftsmeldinger } = restApiHooks.useGlobalStateRestApi(RestApiGlobalStatePathsKeys.DRIFTSMELDINGER);

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
