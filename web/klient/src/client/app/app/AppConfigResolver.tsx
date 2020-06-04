import React, {
  ReactElement, FunctionComponent,
} from 'react';

import { fpLosApiKeys } from 'data/fpLosApi';
import LoadingPanel from 'sharedComponents/LoadingPanel';
import useRestApi, { apiStates } from '../data/useRestApi';

interface OwnProps {
  children: ReactElement;
}

const AppConfigResolver: FunctionComponent<OwnProps> = ({
  children,
}) => {
  const { state: stateNavAnsatt } = useRestApi(fpLosApiKeys.NAV_ANSATT);
  const { state: stateKodeverk } = useRestApi(fpLosApiKeys.KODEVERK);
  const { state: stateFpsakUrl } = useRestApi(fpLosApiKeys.FPSAK_URL);
  const { state: stateFptilbakeUrl } = useRestApi(fpLosApiKeys.FPTILBAKE_URL);

  if (stateNavAnsatt === apiStates.LOADING
    || stateKodeverk === apiStates.LOADING
    || stateFpsakUrl === apiStates.LOADING
    || stateFptilbakeUrl === apiStates.LOADING) {
    return <LoadingPanel />;
  }
  return children;
};

export default AppConfigResolver;
