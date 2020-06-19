import React, { useState, useCallback } from 'react';
import { Form } from 'react-final-form';
import { action } from '@storybook/addon-actions';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import { RestApiProvider } from 'data/rest-api-hooks';
import FagsakYtelseTypeVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/FagsakYtelseTypeVelger';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';
import RequestMock from '../../../mocks/RequestMock';

const initialState = {
  [RestApiGlobalStatePathsKeys.KODEVERK]: alleKodeverk,
};

export default {
  title: 'avdelingsleder/behandlingskoer/FagsakYtelseTypeVelger',
  component: FagsakYtelseTypeVelger,
  decorators: [withIntl],
};

export const skalViseVelgerForFagsakYtelseTyper = () => {
  const [verdier, leggTilVerdi] = useState({
    fagsakYtelseType: fagsakYtelseType.FORELDREPRENGER,
  });
  const lagre = useCallback((_sakslisteId, fyt) => {
    leggTilVerdi((oldState) => ({
      ...oldState,
      fagsakYtelseType: fyt,
    }));
  }, []);

  return (
    <RestApiProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}} requestApi={new RequestMock().build()}>
      <Form
        onSubmit={() => undefined}
        initialValues={verdier}
        render={() => (
          <FagsakYtelseTypeVelger
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
