import React, { useState, useCallback } from 'react';
import { Form } from 'react-final-form';

import FagsakYtelseTypeVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/FagsakYtelseTypeVelger';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';

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
    <Form
      onSubmit={() => undefined}
      initialValues={verdier}
      render={() => (
        <FagsakYtelseTypeVelger
          alleKodeverk={alleKodeverk}
          valgtSakslisteId={1}
          lagreSakslisteFagsakYtelseType={lagre}
          valgtAvdelingEnhet="NAV Viken"
        />
      )}
    />
  );
};
