import React from 'react';
import { action } from '@storybook/addon-actions';
import { IntlShape } from 'react-intl';

import { RestApiPathsKeys } from 'data/restApiPaths';
import { RestApiProvider } from 'data/rest-api-hooks';
import { SakslisteVelgerForm } from 'saksbehandler/behandlingskoer/components/SakslisteVelgerForm';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import koSortering from 'kodeverk/KoSortering';
import andreKriterierType from 'kodeverk/andreKriterierType';

import withIntl from '../../../decorators/withIntl';
import RequestMock from '../../../mocks/RequestMock';

export default {
  title: 'saksbehandler/behandlingskoer/SakslisteVelgerForm',
  component: SakslisteVelgerForm,
  decorators: [withIntl],
};

export const skalViseValgtKøOgUtvalgskriterier = (intl: IntlShape) => {
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
  const requestApi = new RequestMock()
    .withKeyAndResult(RestApiPathsKeys.SAKSLISTE_SAKSBEHANDLERE, saksbehandlere)
    .build();

  return (
    <RestApiProvider requestApi={requestApi}>
      <SakslisteVelgerForm
        intl={intl}
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
      />
    </RestApiProvider>
  );
};
