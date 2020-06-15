import React, { useState, useCallback } from 'react';
import { Form } from 'react-final-form';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import { RestApiGlobalDataProvider } from 'data/rest-api-hooks';
import FagsakYtelseTypeVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/FagsakYtelseTypeVelger';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';

const initialState = {
  [RestApiGlobalStatePathsKeys.KODEVERK]: alleKodeverk,
};

export default {
  title: 'avdelingsleder/behandlingskoer/FagsakYtelseTypeVelger',
  component: FagsakYtelseTypeVelger,
  decorators: [
    withIntl,
    (getStory) => (
      <RestApiGlobalDataProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}}>
        {getStory()}
      </RestApiGlobalDataProvider>
    ),
  ],
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
    <Form
      onSubmit={() => undefined}
      initialValues={verdier}
      render={() => (
        <FagsakYtelseTypeVelger
          valgtSakslisteId={1}
          lagreSakslisteFagsakYtelseType={lagre}
          valgtAvdelingEnhet="NAV Viken"
        />
      )}
    />
  );
};
