import React from 'react';
import { Form } from 'react-final-form';
import { action } from '@storybook/addon-actions';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import { RestApiProvider } from 'data/rest-api-hooks';
import SorteringVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/SorteringVelger';
import behandlingType from 'kodeverk/behandlingType';
import koSortering from 'kodeverk/KoSortering';

import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withIntl from '../../../decorators/withIntl';
import RequestMock from '../../../mocks/RequestMock';

const initialState = {
  [RestApiGlobalStatePathsKeys.KODEVERK]: alleKodeverk,
};

export default {
  title: 'avdelingsleder/behandlingskoer/SorteringVelger',
  component: SorteringVelger,
  decorators: [withIntl],
};

export const skalViseSorteringsvelgerNårMangeBehandlingstyperErValgt = () => {
  const verdier = {
    sortering: koSortering.BEHANDLINGSFRIST,
    fra: 2,
    til: 3,
    fomDato: undefined,
    tomDato: undefined,
    erDynamiskPeriode: true,
  };

  return (
    <RestApiProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}} requestApi={new RequestMock().build()}>
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
    </RestApiProvider>
  );
};

export const skalViseSorteringsvelgerNårKunTilbakekrevingErValgt = () => {
  const verdier = {
    sortering: koSortering.BEHANDLINGSFRIST,
    fra: 2,
    til: 3,
    fomDato: undefined,
    tomDato: undefined,
    erDynamiskPeriode: true,
  };

  return (
    <RestApiProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}} requestApi={new RequestMock().build()}>
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
    </RestApiProvider>
  );
};
