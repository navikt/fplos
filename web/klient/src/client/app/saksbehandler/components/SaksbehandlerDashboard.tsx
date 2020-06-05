
import React from 'react';
import Panel from 'nav-frontend-paneler';

import { RestApiPathsKeys } from 'data/restApiPaths';
import useRestApiData from 'data/useRestApiData';

import FagsakSearchIndex from '../fagsakSearch/FagsakSearchIndex';
import BehandlingskoerIndex from '../behandlingskoer/BehandlingskoerIndex';
import SaksstotteIndex from '../saksstotte/SaksstotteIndex';

import styles from './saksbehandlerDashboard.less';

/**
 * SaksbehandlerDashboard
 */
export const SaksbehandlerDashboard = () => {
  const fpsakUrl = useRestApiData<{ verdi?: string }>(RestApiPathsKeys.FPSAK_URL);
  const fptilbakeUrl = useRestApiData<{ verdi?: string }>(RestApiPathsKeys.FPTILBAKE_URL);
  return (
    <div>
      <div className={styles.oppgaveContainer}>
        <div className={styles.gridContainer}>
          <div className={styles.leftColumn}>
            <div className={styles.sakslisteContent}>
              <Panel className={styles.sakslistePanel}>
                <BehandlingskoerIndex fpsakUrl={fpsakUrl.verdi} fptilbakeUrl={fptilbakeUrl.verdi} />
                <FagsakSearchIndex fpsakUrl={fpsakUrl.verdi} fptilbakeUrl={fptilbakeUrl.verdi} />
              </Panel>
            </div>
          </div>
          <div className={styles.rightColumn}>
            <Panel>
              <SaksstotteIndex />
            </Panel>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SaksbehandlerDashboard;
