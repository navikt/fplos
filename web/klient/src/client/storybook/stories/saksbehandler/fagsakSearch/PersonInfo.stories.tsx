import React from 'react';

import PersonInfo from 'saksbehandler/fagsakSearch/components/person/PersonInfo';
import diskresjonskodeType from 'kodeverk/diskresjonskodeType';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/fagsakSearch/PersonInfo',
  component: PersonInfo,
  decorators: [withIntl],
};

export const skalPersonkortMedDiskresjonskodeForMann = () => (
  <PersonInfo
    person={{
      navn: 'Espen Utvikler',
      alder: 41,
      personnummer: '23232332',
      erKvinne: false,
      diskresjonskode: diskresjonskodeType.KODE7,
    }}
  />
);

export const skalPersonkortForDødKvinne = () => (
  <PersonInfo
    person={{
      navn: 'Olga Pettersen',
      alder: 41,
      personnummer: '23232332',
      erKvinne: true,
      dodsdato: '2020-10-10',
    }}
  />
);
