import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import SakslisteVelgerForm from 'saksbehandler/behandlingskoer/components/SakslisteVelgerForm';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import koSortering from 'kodeverk/KoSortering';
import andreKriterierType from 'kodeverk/andreKriterierType';
import Saksliste from 'types/saksbehandler/sakslisteTsType';
import Saksbehandler from 'types/saksbehandler/saksbehandlerTsType';
import alleKodeverk from 'storybookUtils/mocks/alleKodeverk.json';

import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';
import RestApiMock from 'storybookUtils//RestApiMock';

export default {
  title: 'saksbehandler/behandlingskoer/SakslisteVelgerForm',
  component: SakslisteVelgerForm,
  decorators: [withRestApiProvider, withIntl],
};

const Template: Story<{ saksbehandlere: Saksbehandler[], sakslister: Saksliste[] }> = ({
  saksbehandlere,
  sakslister,
}) => {
  const data = [
    { key: RestApiPathsKeys.SAKSLISTE_SAKSBEHANDLERE.name, data: saksbehandlere },
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
  ];

  return (
    <RestApiMock data={data}>
      <SakslisteVelgerForm
        sakslister={sakslister}
        setValgtSakslisteId={action('button-click')}
        fetchAntallOppgaver={action('button-click')}
        getValueFromLocalStorage={() => ''}
        setValueInLocalStorage={action('button-click')}
        removeValueFromLocalStorage={action('button-click')}
      />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
Default.args = {
  saksbehandlere: [{
    brukerIdent: {
      brukerIdent: '32434',
      verdi: '32434',
    },
    navn: 'Espen Utvikler',
    avdelingsnavn: [],
  }, {
    brukerIdent: {
      brukerIdent: '31111',
      verdi: '32111',
    },
    navn: 'Auto Joakim',
    avdelingsnavn: [],
  }],
  sakslister: [{
    sakslisteId: 1,
    navn: 'Saksliste 1',
    behandlingTyper: [behandlingType.FORSTEGANGSSOKNAD, behandlingType.REVURDERING],
    fagsakYtelseTyper: [fagsakYtelseType.FORELDREPRENGER],
    andreKriterier: [{
      andreKriterierType: andreKriterierType.TIL_BESLUTTER,
      inkluder: true,
    }],
    sortering: {
      sorteringType: koSortering.BEHANDLINGSFRIST,
      fra: 2,
      til: 4,
      erDynamiskPeriode: true,
    },
  }],
};

export const MedToSakslister = Template.bind({});
MedToSakslister.args = {
  saksbehandlere: [{
    brukerIdent: {
      brukerIdent: '32434',
      verdi: '32434',
    },
    navn: 'Espen Utvikler',
    avdelingsnavn: [],
  }, {
    brukerIdent: {
      brukerIdent: '31111',
      verdi: '32111',
    },
    navn: 'Auto Joakim',
    avdelingsnavn: [],
  }],
  sakslister: [{
    sakslisteId: 1,
    navn: 'Saksliste 1',
    behandlingTyper: [behandlingType.FORSTEGANGSSOKNAD, behandlingType.REVURDERING],
    fagsakYtelseTyper: [fagsakYtelseType.FORELDREPRENGER],
    andreKriterier: [{
      andreKriterierType: andreKriterierType.TIL_BESLUTTER,
      inkluder: true,
    }],
    sortering: {
      sorteringType: koSortering.BEHANDLINGSFRIST,
      fra: 2,
      til: 4,
      erDynamiskPeriode: true,
    },
  }, {
    sakslisteId: 2,
    navn: 'Saksliste 2',
    behandlingTyper: [behandlingType.FORSTEGANGSSOKNAD, behandlingType.KLAGE],
    fagsakYtelseTyper: [fagsakYtelseType.SVANGERSKAPPENGER],
    andreKriterier: [{
      andreKriterierType: andreKriterierType.UTBETALING_TIL_BRUKER,
      inkluder: true,
    }],
    sortering: {
      sorteringType: koSortering.BEHANDLINGSFRIST,
      fra: 2,
      til: 4,
      erDynamiskPeriode: true,
    },
  }],
};
