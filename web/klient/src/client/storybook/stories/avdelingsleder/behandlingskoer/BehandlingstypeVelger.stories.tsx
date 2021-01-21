import React from 'react';
import { Form } from 'react-final-form';
import { action } from '@storybook/addon-actions';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import BehandlingstypeVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/BehandlingstypeVelger';
import behandlingType from 'kodeverk/behandlingType';

import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withRestApiProvider from '../../../decorators/withRestApi';

export default {
  title: 'avdelingsleder/behandlingskoer/BehandlingstypeVelger',
  component: BehandlingstypeVelger,
  decorators: [withIntl, withRestApiProvider],
};

export const skalViseVelgerForBehandlingstyper = () => {
  const verdier = {
    [behandlingType.FORSTEGANGSSOKNAD]: true,
  };

  requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

  return (
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
  );
};
