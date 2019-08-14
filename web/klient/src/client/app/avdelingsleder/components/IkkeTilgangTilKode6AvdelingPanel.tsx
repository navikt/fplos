
import React from 'react';
import { FormattedMessage } from 'react-intl';
import { Panel } from 'nav-frontend-paneler';
import { Undertittel } from 'nav-frontend-typografi';

import styles from './ikkeTilgangTilKode6AvdelingPanel.less';

/**
 * IkkeTilgangTilKode6AvdelingPanel
 */
const IkkeTilgangTilKode6AvdelingPanel = () => (
  <Panel className={styles.container}>
    <Undertittel>
      <FormattedMessage id="IkkeTilgangTilKode6AvdelingPanel.HarIkkeTilgang" />
    </Undertittel>
  </Panel>
);

export default IkkeTilgangTilKode6AvdelingPanel;
