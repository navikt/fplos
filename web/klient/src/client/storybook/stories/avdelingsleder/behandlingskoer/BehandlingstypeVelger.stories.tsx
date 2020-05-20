import React, { useState, useCallback } from 'react';
import { Form } from 'react-final-form';

import BehandlingstypeVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/BehandlingstypeVelger';
import behandlingType from 'kodeverk/behandlingType';

import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';

export default {
  title: 'avdelingsleder/behandlingskoer/BehandlingstypeVelger',
  component: BehandlingstypeVelger,
  decorators: [withIntl],
};

export const skalViseVelgerForBehandlingstyper = () => {
  const [verdier, leggTilVerdi] = useState({
    [behandlingType.FORSTEGANGSSOKNAD]: true,
  });
  const lagre = useCallback((_sakslisteId, bt, isChecked) => {
    leggTilVerdi((oldState) => ({
      ...oldState,
      [bt.kode]: isChecked,
    }));
  }, []);

  return (
    <Form
      onSubmit={() => undefined}
      initialValues={verdier}
      render={() => (
        <BehandlingstypeVelger
          alleKodeverk={alleKodeverk}
          valgtSakslisteId={1}
          lagreSakslisteBehandlingstype={lagre}
          valgtAvdelingEnhet="NAV Viken"
        />
      )}
    />
  );
};
