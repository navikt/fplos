import React, { useState, useCallback } from 'react';
import { Form } from 'react-final-form';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import { RestApiGlobalDataProvider } from 'data/rest-api-hooks';
import AndreKriterierVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/AndreKriterierVelger';
import andreKriterierType from 'kodeverk/andreKriterierType';

import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';

const initialState = {
  [RestApiGlobalStatePathsKeys.KODEVERK]: alleKodeverk,
};

export default {
  title: 'avdelingsleder/behandlingskoer/AndreKriterierVelger',
  component: AndreKriterierVelger,
  decorators: [
    withIntl,
    (getStory) => (
      <RestApiGlobalDataProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}}>
        {getStory()}
      </RestApiGlobalDataProvider>
    ),
  ],
};

export const skalViseVelgerAvAndreKriterier = () => {
  const [verdier, leggTilVerdi] = useState({
    [andreKriterierType.TIL_BESLUTTER]: true,
    [`${andreKriterierType.TIL_BESLUTTER}_inkluder`]: true,
  });
  const lagre = useCallback((_sakslisteId, akType, isChecked, skalInkludere) => {
    leggTilVerdi((oldState) => ({
      ...oldState,
      [akType.kode]: isChecked,
      [`${akType.kode}_inkluder`]: skalInkludere,
    }));
  }, []);

  return (
    <Form
      onSubmit={() => undefined}
      initialValues={verdier}
      render={({ values }) => (
        <AndreKriterierVelger
          valgtSakslisteId={1}
          lagreSakslisteAndreKriterier={lagre}
          valgtAvdelingEnhet="NAV Viken"
          values={values}
        />
      )}
    />
  );
};
