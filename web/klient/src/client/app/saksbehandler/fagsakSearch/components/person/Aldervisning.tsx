
import React, { FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';
import { Normaltekst } from 'nav-frontend-typografi';

import DateLabel from 'sharedComponents/DateLabel';

import styles from './aldervisning.less';

interface OwnProps {
  erDod: boolean;
  alder: number;
  dodsdato?: string;
}

/**
 * AlderVisning
 *
 * Presentasjonskomponent. Definerer visning av personens alder. (Søker)
 */
const AlderVisning: FunctionComponent<OwnProps> = ({
  erDod,
  alder,
  dodsdato,
}) => {
  if (erDod) {
    return (
      <Normaltekst className={styles.displayInline}>
        { dodsdato
          ? <DateLabel dateString={dodsdato} />
          : <FormattedMessage id="Person.ManglerDodsdato" />}
      </Normaltekst>
    );
  }

  return (
    <span>
      <FormattedMessage id="Person.Age" values={{ age: alder }} />
    </span>
  );
};

AlderVisning.defaultProps = {
  dodsdato: '',
};

export default AlderVisning;
