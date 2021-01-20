import React from 'react';
import { Form } from 'react-final-form';
import { action } from '@storybook/addon-actions';

import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { RestApiProvider } from 'data/rest-api-hooks';
import BehandlingstypeVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/BehandlingstypeVelger';
import behandlingType from 'kodeverk/behandlingType';

import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';
import RequestMock from '../../../mocks/RequestMock';

const initialState = {
  [RestApiGlobalStatePathsKeys.KODEVERK]: alleKodeverk,
};

export default {
  title: 'avdelingsleder/behandlingskoer/BehandlingstypeVelger',
  component: BehandlingstypeVelger,
  decorators: [withIntl],
};

export const skalViseVelgerForBehandlingstyper = () => {
  const verdier = {
    [behandlingType.FORSTEGANGSSOKNAD]: true,
  };

  return (
    <RestApiProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}} requestApi={new RequestMock().build()}>
      <Form
        onSubmit={() => undefined}
        initialValues={verdier}
        render={() => (
          <BehandlingstypeVelger
            valgtSakslisteId={1}
            valgtAvdelingEnhet="NAV Viken"
            hentAvdelingensSakslister={action('button-click')}
            hentAntallOppgaver={action('button-click')}
          />
        )}
      />
    </RestApiProvider>
  );
};
