import React from 'react';
import { Form } from 'react-final-form';
import { action } from '@storybook/addon-actions';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import FagsakYtelseTypeVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/FagsakYtelseTypeVelger';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withRestApiProvider from '../../../decorators/withRestApi';

export default {
  title: 'avdelingsleder/behandlingskoer/FagsakYtelseTypeVelger',
  component: FagsakYtelseTypeVelger,
  decorators: [withIntl, withRestApiProvider],
};

export const skalViseVelgerForFagsakYtelseTyper = () => {
  const initialValues = {
    fagsakYtelseType: fagsakYtelseType.FORELDREPRENGER,
  };

  requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

  return (
    <Form
      onSubmit={() => undefined}
      initialValues={initialValues}
      render={() => (
        <FagsakYtelseTypeVelger
          valgtSakslisteId={1}
          valgtAvdelingEnhet="NAV Viken"
          hentAvdelingensSakslister={action('button-click')}
          hentAntallOppgaver={action('button-click')}
        />
      )}
    />
  );
};
