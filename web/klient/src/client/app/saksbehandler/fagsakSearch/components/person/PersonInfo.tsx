import React, { FunctionComponent } from 'react';
import { injectIntl, WrappedComponentProps } from 'react-intl';
import { Undertittel, Undertekst } from 'nav-frontend-typografi';

import Image from 'sharedComponents/Image';
import urlMann from 'images/mann.svg';
import urlKvinne from 'images/kvinne.svg';

import Person from '../../personTsType';
import AlderVisning from './Aldervisning';
import MerkePanel from './Merkepanel';

import styles from './personInfo.less';

interface OwnProps {
  person: Person;
}

/**
 * PersonInfo
 *
 * Presentasjonskomponent. Definerer visning av personen relatert til fagsak. (Søker)
 *
 * Eksempel:
 * ```html
 * <PersonInfo person={navn:"Ola" alder:{40} personnummer:"12345678910" erKvinne:false
 * erDod:false diskresjonskode:"6" dodsdato:"1990.03.03"} medPanel />
 * ```
 */
const PersonInfo: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  person,
  intl,
}) => {
  const {
    erKvinne, dodsdato, diskresjonskode, alder, navn, personnummer,
  } = person;
  return (
    <div>
      <Image
        className={styles.icon}
        src={erKvinne ? urlKvinne : urlMann}
        alt={intl.formatMessage({ id: 'Person.ImageText' })}
      />
      <div className={styles.infoPlaceholder}>
        <div>
          <Undertittel>
            {navn}
            {' '}
            <AlderVisning erDod={!!dodsdato} alder={alder} dodsdato={dodsdato} />
          </Undertittel>
          <Undertekst>
            {personnummer}
          </Undertekst>
        </div>
        <div>
          <MerkePanel erDod={!!dodsdato} diskresjonskode={diskresjonskode} />
        </div>
      </div>
    </div>
  );
};

export default injectIntl(PersonInfo);
