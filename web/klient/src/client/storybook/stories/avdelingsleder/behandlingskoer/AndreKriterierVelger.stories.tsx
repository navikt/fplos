import React, { useState, useCallback } from 'react';
import { Form } from 'react-final-form';

import { RestApiPathsKeys } from 'data/restApiPaths';
import { RestDataProvider } from 'data/RestDataContext';
import AndreKriterierVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/AndreKriterierVelger';
import andreKriterierType from 'kodeverk/andreKriterierType';

import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';

const initialState = {
  [RestApiPathsKeys.KODEVERK]: alleKodeverk,
};

export default {
  title: 'avdelingsleder/behandlingskoer/AndreKriterierVelger',
  component: AndreKriterierVelger,
  decorators: [
    withIntl,
    (getStory) => <RestDataProvider initialState={initialState as {[key in RestApiPathsKeys]: any}}>{getStory()}</RestDataProvider>,
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
