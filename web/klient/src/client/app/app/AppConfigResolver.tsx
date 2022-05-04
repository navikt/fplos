import React, {
  ReactElement, FunctionComponent, useEffect,
} from 'react';

import { requestApi, restApiHooks, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { RestApiState, useRestApiErrorDispatcher } from 'data/rest-api-hooks';
import { LoadingPanel } from '@navikt/ft-ui-komponenter';

interface OwnProps {
  children: ReactElement;
}

const AppConfigResolver: FunctionComponent<OwnProps> = ({
  children,
}) => {
  const { addErrorMessage } = useRestApiErrorDispatcher();
  useEffect(() => {
    requestApi.setAddErrorMessageHandler(addErrorMessage);
  }, []);

  const { state: stateNavAnsatt } = restApiHooks.useGlobalStateRestApi(RestApiGlobalStatePathsKeys.NAV_ANSATT);
  const { state: stateKodeverk } = restApiHooks.useGlobalStateRestApi(RestApiGlobalStatePathsKeys.KODEVERK);
  const { state: stateFpsakUrl } = restApiHooks.useGlobalStateRestApi(RestApiGlobalStatePathsKeys.FPSAK_URL);
  const { state: stateDriftsmeldinger } = restApiHooks.useGlobalStateRestApi(RestApiGlobalStatePathsKeys.DRIFTSMELDINGER);

  if (stateNavAnsatt === RestApiState.LOADING
    || stateKodeverk === RestApiState.LOADING
    || stateFpsakUrl === RestApiState.LOADING
    || stateDriftsmeldinger === RestApiState.LOADING) {
    return <LoadingPanel />;
  }
  return children;
};

export default AppConfigResolver;
