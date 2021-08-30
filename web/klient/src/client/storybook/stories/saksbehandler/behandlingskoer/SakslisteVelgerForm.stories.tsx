import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiPathsKeys } from 'data/fplosRestApi';
import SakslisteVelgerForm from 'saksbehandler/behandlingskoer/components/SakslisteVelgerForm';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import koSortering from 'kodeverk/KoSortering';
import andreKriterierType from 'kodeverk/andreKriterierType';
import Saksliste from 'types/saksbehandler/sakslisteTsType';
import Saksbehandler from 'types/saksbehandler/saksbehandlerTsType';

import withIntl from '../../../decorators/withIntl';
import withRestApiProvider from '../../../decorators/withRestApi';
import RestApiMock from '../../../utils/RestApiMock';

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
    behandlingTyper: [{
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: 'Førstegangssøknad',
    }, {
      kode: behandlingType.REVURDERING,
      navn: 'Revurdering',
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
    }],
    sortering: {
      sorteringType: {
        kode: koSortering.BEHANDLINGSFRIST,
        navn: 'Behandlingsfrist',
      },
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
    behandlingTyper: [{
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: 'Førstegangssøknad',
    }, {
      kode: behandlingType.REVURDERING,
      navn: 'Revurdering',
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
    }],
    sortering: {
      sorteringType: {
        kode: koSortering.BEHANDLINGSFRIST,
        navn: 'Behandlingsfrist',
      },
      fra: 2,
      til: 4,
      erDynamiskPeriode: true,
    },
  }, {
    sakslisteId: 2,
    navn: 'Saksliste 2',
    behandlingTyper: [{
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: 'Førstegangssøknad',
    }, {
      kode: behandlingType.KLAGE,
      navn: 'Klage',
    }],
    fagsakYtelseTyper: [{
      kode: fagsakYtelseType.SVANGERSKAPPENGER,
      navn: 'Svangerskapspenger',
    }],
    andreKriterier: [{
      andreKriterierType: {
        kode: andreKriterierType.UTBETALING_TIL_BRUKER,
        navn: 'Utbetaling til bruker',
      },
      inkluder: true,
    }],
    sortering: {
      sorteringType: {
        kode: koSortering.BEHANDLINGSFRIST,
        navn: 'Behandlingsfrist',
      },
      fra: 2,
      til: 4,
      erDynamiskPeriode: true,
    },
  }],
};
