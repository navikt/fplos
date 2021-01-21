import React from 'react';
import { Form } from 'react-final-form';
import { action } from '@storybook/addon-actions';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import AndreKriterierVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/AndreKriterierVelger';
import andreKriterierType from 'kodeverk/andreKriterierType';

import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withRestApiProvider from '../../../decorators/withRestApi';

export default {
  title: 'avdelingsleder/behandlingskoer/AndreKriterierVelger',
  component: AndreKriterierVelger,
  decorators: [withIntl, withRestApiProvider],
};

export const skalViseVelgerAvAndreKriterier = () => {
  const verdier = {
    [andreKriterierType.TIL_BESLUTTER]: true,
    [`${andreKriterierType.TIL_BESLUTTER}_inkluder`]: true,
  };

  requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

  return (
    <Form
      onSubmit={() => undefined}
      initialValues={verdier}
      render={({ values }) => (
        <AndreKriterierVelger
          valgtSakslisteId={1}
          valgtAvdelingEnhet="NAV Viken"
          values={values}
          hentAvdelingensSakslister={action('button-click')}
          hentAntallOppgaver={action('button-click')}
        />
      )}
    />
  );
};
