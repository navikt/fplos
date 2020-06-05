import React, { useState, useCallback } from 'react';
import { Form } from 'react-final-form';

import { RestApiPathsKeys } from 'data/restApiPaths';
import { RestDataProvider } from 'data/RestDataContext';
import FagsakYtelseTypeVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/FagsakYtelseTypeVelger';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';

const initialState = {
  [RestApiPathsKeys.KODEVERK]: alleKodeverk,
};

export default {
  title: 'avdelingsleder/behandlingskoer/FagsakYtelseTypeVelger',
  component: FagsakYtelseTypeVelger,
  decorators: [
    withIntl,
    (getStory) => <RestDataProvider initialState={initialState as {[key in RestApiPathsKeys]: any}}>{getStory()}</RestDataProvider>,
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
