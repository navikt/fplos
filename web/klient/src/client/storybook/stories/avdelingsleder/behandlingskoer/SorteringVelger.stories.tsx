import React from 'react';
import { Form } from 'react-final-form';
import { action } from '@storybook/addon-actions';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import SorteringVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/SorteringVelger';
import behandlingType from 'kodeverk/behandlingType';
import koSortering from 'kodeverk/KoSortering';

import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withIntl from '../../../decorators/withIntl';
import withRestApiProvider from '../../../decorators/withRestApi';

export default {
  title: 'avdelingsleder/behandlingskoer/SorteringVelger',
  component: SorteringVelger,
  decorators: [withIntl, withRestApiProvider],
};

export const skalViseSorteringsvelgerNårMangeBehandlingstyperErValgt = () => {
  const verdier = {
    sortering: koSortering.BEHANDLINGSFRIST,
    fra: 2,
    til: 3,
    fomDato: '2020.01.10',
    tomDato: '2020.10.01',
    erDynamiskPeriode: true,
  };

  requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

  return (
    <Form
      onSubmit={() => undefined}
      initialValues={verdier}
      render={() => (
        <SorteringVelger
          valgtSakslisteId={1}
          valgteBehandlingtyper={[{
            kode: behandlingType.FORSTEGANGSSOKNAD,
            navn: 'Førstegang',
          }, {
            kode: behandlingType.DOKUMENTINNSYN,
            navn: 'Innsyn',
          }]}
          valgtAvdelingEnhet="NAV Viken"
          erDynamiskPeriode={verdier.erDynamiskPeriode}
          fra={verdier.fra}
          til={verdier.til}
          fomDato={verdier.fomDato}
          tomDato={verdier.tomDato}
          hentAvdelingensSakslister={action('button-click')}
          hentAntallOppgaver={action('button-click')}
        />
      )}
    />
  );
};

export const skalViseSorteringsvelgerNårKunTilbakekrevingErValgt = () => {
  const verdier = {
    sortering: koSortering.BEHANDLINGSFRIST,
    fra: 2,
    til: 3,
    fomDato: '2020.01.10',
    tomDato: '2020.10.01',
    erDynamiskPeriode: true,
  };

  requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

  return (
    <Form
      onSubmit={() => undefined}
      initialValues={verdier}
      render={() => (
        <SorteringVelger
          valgtSakslisteId={1}
          valgteBehandlingtyper={[{
            kode: behandlingType.TILBAKEBETALING,
            navn: 'Tilbakekreving',
          }]}
          valgtAvdelingEnhet="NAV Viken"
          erDynamiskPeriode={verdier.erDynamiskPeriode}
          fra={verdier.fra}
          til={verdier.til}
          fomDato={verdier.fomDato}
          tomDato={verdier.tomDato}
          hentAvdelingensSakslister={action('button-click')}
          hentAntallOppgaver={action('button-click')}
        />
      )}
    />
  );
};
