import React, { FunctionComponent } from 'react';
import Panel from 'nav-frontend-paneler';

import { restApiHooks, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';

import FagsakSearchIndex from '../fagsakSearch/FagsakSearchIndex';
import BehandlingskoerIndex from '../behandlingskoer/BehandlingskoerIndex';
import SaksstotteIndex from '../saksstotte/SaksstotteIndex';

import styles from './saksbehandlerDashboard.less';

interface OwnProps {
  valgtSakslisteId?: number;
  setValgtSakslisteId: (valgSakslisteId: number) => void;
}

/**
 * SaksbehandlerDashboard
 */
export const SaksbehandlerDashboard: FunctionComponent<OwnProps> = ({
  valgtSakslisteId,
  setValgtSakslisteId,
}) => {
  const fpsakUrl = restApiHooks.useGlobalStateRestApiData(RestApiGlobalStatePathsKeys.FPSAK_URL);
  const fptilbakeUrl = restApiHooks.useGlobalStateRestApiData(RestApiGlobalStatePathsKeys.FPTILBAKE_URL);
  return (
    <div>
      <div className={styles.oppgaveContainer}>
        <div className={styles.gridContainer}>
          <div className={styles.leftColumn}>
            <div className={styles.sakslisteContent}>
              <Panel className={styles.sakslistePanel}>
                <BehandlingskoerIndex
                  fpsakUrl={fpsakUrl.verdi}
                  fptilbakeUrl={fptilbakeUrl.verdi}
                  valgtSakslisteId={valgtSakslisteId}
                  setValgtSakslisteId={setValgtSakslisteId}
                />
                <FagsakSearchIndex fpsakUrl={fpsakUrl.verdi} fptilbakeUrl={fptilbakeUrl.verdi} />
              </Panel>
            </div>
          </div>
          <div className={styles.rightColumn}>
            <Panel>
              <SaksstotteIndex valgtSakslisteId={valgtSakslisteId} />
            </Panel>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SaksbehandlerDashboard;
