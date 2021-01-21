import React from 'react';
import { action } from '@storybook/addon-actions';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import SakslisteVelgerForm from 'saksbehandler/behandlingskoer/components/SakslisteVelgerForm';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import koSortering from 'kodeverk/KoSortering';
import andreKriterierType from 'kodeverk/andreKriterierType';

import withIntl from '../../../decorators/withIntl';

import withRestApiProvider from '../../../decorators/withRestApi';

export default {
  title: 'saksbehandler/behandlingskoer/SakslisteVelgerForm',
  component: SakslisteVelgerForm,
  decorators: [withRestApiProvider, withIntl],
};

export const skalViseValgtKøOgUtvalgskriterier = () => {
  const saksbehandlere = [{
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
  }];

  requestApi.mock(RestApiPathsKeys.SAKSLISTE_SAKSBEHANDLERE, saksbehandlere);

  return (
    <SakslisteVelgerForm
      sakslister={[{
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
      }]}
      setValgtSakslisteId={action('button-click')}
      fetchAntallOppgaver={action('button-click')}
      getValueFromLocalStorage={() => ''}
      setValueInLocalStorage={action('button-click')}
      removeValueFromLocalStorage={action('button-click')}
    />
  );
};
