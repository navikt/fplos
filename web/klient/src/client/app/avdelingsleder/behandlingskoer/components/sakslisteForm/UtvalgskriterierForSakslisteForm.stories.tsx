import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import UtvalgskriterierForSakslisteForm from 'avdelingsleder/behandlingskoer/components/sakslisteForm/UtvalgskriterierForSakslisteForm';
import koSortering from 'kodeverk/KoSortering';
import andreKriterierType from 'kodeverk/andreKriterierType';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import RestApiMock from 'storybookUtils/RestApiMock';
import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';
import alleKodeverk from 'storybookUtils/mocks/alleKodeverk.json';

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
          behandlingTyper: [behandlingType.FORSTEGANGSSOKNAD],
          fagsakYtelseTyper: [fagsakYtelseType.FORELDREPRENGER],
          andreKriterier: [{
            andreKriterierType: andreKriterierType.TIL_BESLUTTER,
            inkluder: true,
          }, {
            andreKriterierType: andreKriterierType.REGISTRER_PAPIRSOKNAD,
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
  sakslisteNavn: 'liste',
};

export const MedDefaultNavn = Template.bind({});
MedDefaultNavn.args = {
  sakslisteNavn: undefined,
};
