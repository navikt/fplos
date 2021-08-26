import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import UtvalgskriterierForSakslisteForm from 'avdelingsleder/behandlingskoer/components/sakslisteForm/UtvalgskriterierForSakslisteForm';
import koSortering from 'kodeverk/KoSortering';
import andreKriterierType from 'kodeverk/andreKriterierType';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import RestApiMock from '../../../utils/RestApiMock';
import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withRestApiProvider from '../../../decorators/withRestApi';

export default {
  title: 'avdelingsleder/behandlingskoer/UtvalgskriterierForSakslisteForm',
  component: UtvalgskriterierForSakslisteForm,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{ sakslisteNavn: string }> = ({
  sakslisteNavn,
}) => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
    { key: RestApiPathsKeys.OPPGAVE_ANTALL.name, data: 1 },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_NAVN.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_INTERVALL.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_BEHANDLINGSTYPE.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER.name, data: undefined },
  ];

  return (
    <RestApiMock data={data}>
      <UtvalgskriterierForSakslisteForm
        valgtSaksliste={{
          sakslisteId: 1,
          navn: sakslisteNavn,
          sistEndret: '2020-10-10',
          saksbehandlerIdenter: [],
          antallBehandlinger: 1,
          sortering: {
            sorteringType: {
              kode: koSortering.BEHANDLINGSFRIST,
              navn: 'Behandlingsfrist',
              felttype: '',
              feltkategori: '',
            },
            fra: 1,
            til: 4,
            erDynamiskPeriode: true,
          },
          behandlingTyper: [{
            kode: behandlingType.FORSTEGANGSSOKNAD,
            navn: 'Førstegangssøknad',
          }],
          fagsakYtelseTyper: [{
            kode: fagsakYtelseType.FORELDREPRENGER,
            navn: 'Foreldrepenger',
          }],
          andreKriterier: [{
            andreKriterierType: {
              kode: andreKriterierType.TIL_BESLUTTER,
              navn: 'Til beslutter',
            },
            inkluder: true,
          }, {
            andreKriterierType: {
              kode: andreKriterierType.REGISTRER_PAPIRSOKNAD,
              navn: 'Registrer papirsøknad',
            },
            inkluder: false,
          }],
        }}
        valgtAvdelingEnhet=""
        hentAvdelingensSakslister={action('button-click')}
        hentOppgaverForAvdelingAntall={action('button-click')}
      />
    </RestApiMock>
  );
};

export const MedGittNavn = Template.bind({});
MedGittNavn.args = {
  sakslisteNavn: 'Saksliste 1',
};

export const MedDefaultNavn = Template.bind({});
MedDefaultNavn.args = {
  sakslisteNavn: undefined,
};
