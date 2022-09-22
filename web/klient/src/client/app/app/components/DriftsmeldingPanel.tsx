import React, { FunctionComponent } from 'react';
import { Detail } from '@navikt/ds-react';
import advarselImageUrl from 'images/advarsel-sirkel-fyll.svg';
import Driftsmelding from 'types/driftsmeldingTsType';

import {
  FlexColumn, FlexContainer, FlexRow, Image,
} from '@navikt/ft-ui-komponenter';
import styles from './driftsmeldingPanel.less';

interface OwnProps {
  driftsmeldinger: Driftsmelding[];
}

/**
 * DriftsmeldingPanel
 *
 * Definerer hvordan driftsmeldinger vises.
 */
const DriftsmeldingPanel: FunctionComponent<OwnProps> = ({
  driftsmeldinger,
}) => {
  if (driftsmeldinger === undefined || driftsmeldinger.length === 0) {
    return null;
  }

  return (
    <div className={styles.container}>
      {driftsmeldinger.map((message) => (
        <FlexContainer key={message.id}>
          <FlexRow>
            <FlexColumn>
              <Image
                className={styles.driftsInfo}
                src={advarselImageUrl}
              />
            </FlexColumn>
            <FlexColumn>
              <Detail size="small" className={styles.wordWrap}>
                {`${message.melding}`}
              </Detail>
            </FlexColumn>
          </FlexRow>
        </FlexContainer>
      ))}
    </div>
  );
};

export default DriftsmeldingPanel;
