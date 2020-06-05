import React, {
  ReactElement, FunctionComponent,
} from 'react';

import { RestApiPathsKeys } from 'data/restApiPaths';
import LoadingPanel from 'sharedComponents/LoadingPanel';
import useRestApi, { ApiState } from '../data/useRestApi';

interface OwnProps {
  children: ReactElement;
}

const AppConfigResolver: FunctionComponent<OwnProps> = ({
  children,
}) => {
  const { state: stateNavAnsatt } = useRestApi(RestApiPathsKeys.NAV_ANSATT);
  const { state: stateKodeverk } = useRestApi(RestApiPathsKeys.KODEVERK);
  const { state: stateFpsakUrl } = useRestApi(RestApiPathsKeys.FPSAK_URL);
  const { state: stateFptilbakeUrl } = useRestApi(RestApiPathsKeys.FPTILBAKE_URL);

  if (stateNavAnsatt === ApiState.LOADING
    || stateKodeverk === ApiState.LOADING
    || stateFpsakUrl === ApiState.LOADING
    || stateFptilbakeUrl === ApiState.LOADING) {
    return <LoadingPanel />;
  }
  return children;
};

export default AppConfigResolver;
