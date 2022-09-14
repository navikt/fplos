import React, { FunctionComponent } from 'react';
import { Link } from 'react-router-dom';
import { FormattedMessage } from 'react-intl';
import { Panel, BodyShort, Heading } from '@navikt/ds-react';

import styles from './missingPage.less';

/**
 * MissingPage
 *
 * Presentasjonskomponent. Denne komponenten vises når den NAV-ansatte prøver å aksessere en url som ikke finnes.
 * Det blir presentert en generell feilmelding og en lenke som tar NAV-ansatt tilbake til hovedsiden.
 */
const MissingPage: FunctionComponent = () => (
  <Panel className={styles.container}>
    <Heading size="small">
      <FormattedMessage id="MissingPage.PageIsMissing" />
    </Heading>
    <BodyShort size="small">
      <Link to="/">
        <FormattedMessage id="MissingPage.Home" />
      </Link>
    </BodyShort>
  </Panel>
);

export default MissingPage;
