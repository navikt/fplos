import React, { ReactNode, FunctionComponent } from 'react';
import { Normaltekst, Undertekst } from 'nav-frontend-typografi';

import styles from './labelWithHeader.less';

interface OwnProps {
  header: string | ReactNode;
  texts: string[] | ReactNode[];
}

/**
 * LabelWithHeader
 *
 * Presentasjonskomponent. Presenterer tekst med en overskrift. (På samme måte som input-felter med overskrifter)
 */
const LabelWithHeader: FunctionComponent<OwnProps> = ({
  header,
  texts,
}) => (
  <div className={styles.container}>
    <Undertekst>
      {header}
    </Undertekst>
    <div className={styles.text}>
      {texts.map((text) => (
        <Normaltekst key={text}>
          {text}
        </Normaltekst>
      ))}
    </div>
  </div>
);

export default LabelWithHeader;
