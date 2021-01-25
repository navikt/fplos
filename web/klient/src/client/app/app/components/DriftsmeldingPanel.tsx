import React, { FunctionComponent } from 'react';
import { Row, Column } from 'nav-frontend-grid';
import { Undertekst } from 'nav-frontend-typografi';
import advarselImageUrl from 'images/advarsel-sirkel-fyll.svg';
import Driftsmelding from 'app/driftsmeldingTsType';

import Image from 'sharedComponents/Image';
import styles from './driftsmeldingPanel.less';


interface OwnProps {
  driftsmeldinger: Driftsmelding[];
}


/**
 * DriftsmeldingPanel
 *
 * Presentasjonskomponent. Definerer hvordan driftsmeldinger vises.
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
        <Row key={message.id} className={styles.driftsmelding}>
          <Column xs="11" className={styles.column}>
            <Image
              className={styles.driftsInfo}
              src={advarselImageUrl}
            />
            <Undertekst className={styles.wordWrap}>
              {`${message.melding}`}
            </Undertekst>
          </Column>
        </Row>
      ))}
    </div>
  );
};

export default DriftsmeldingPanel;
