import React, { useState, useCallback } from 'react';
import { Form } from 'react-final-form';
import { action } from '@storybook/addon-actions';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import { RestApiGlobalDataProvider } from 'data/rest-api-hooks';
import SorteringVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/SorteringVelger';
import behandlingType from 'kodeverk/behandlingType';
import koSortering from 'kodeverk/KoSortering';

import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withIntl from '../../../decorators/withIntl';

const initialState = {
  [RestApiGlobalStatePathsKeys.KODEVERK]: alleKodeverk,
};

export default {
  title: 'avdelingsleder/behandlingskoer/SorteringVelger',
  component: SorteringVelger,
  decorators: [
    withIntl,
    (getStory) => (
      <RestApiGlobalDataProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}}>
        {getStory()}
      </RestApiGlobalDataProvider>
    ),
  ],
};

export const skalViseSorteringsvelgerNårMangeBehandlingstyperErValgt = () => {
  const [verdier, leggTilVerdi] = useState({
    sortering: koSortering.BEHANDLINGSFRIST,
    fra: 2,
    til: 3,
    fomDato: undefined,
    tomDato: undefined,
    erDynamiskPeriode: true,
  });
  const lagre = useCallback((_sakslisteId, sorteringType) => {
    leggTilVerdi((oldState) => ({
      ...oldState,
      sortering: sorteringType,
    }));
  }, []);
  const lagreDynamiskPeriode = useCallback(() => {
    leggTilVerdi((oldState) => ({
      ...oldState,
      erDynamiskPeriode: !oldState.erDynamiskPeriode,
    }));
  }, []);

  return (
    <Form
      onSubmit={() => undefined}
      initialValues={verdier}
      render={() => (
        <SorteringVelger
          valgtSakslisteId={1}
          valgteBehandlingtyper={[{
            kode: behandlingType.FORSTEGANGSSOKNAD,
            navn: 'Førstegang',
          }, {
            kode: behandlingType.DOKUMENTINNSYN,
            navn: 'Innsyn',
          }]}
          lagreSakslisteSortering={lagre}
          lagreSakslisteSorteringErDynamiskPeriode={lagreDynamiskPeriode}
          valgtAvdelingEnhet="NAV Viken"
          erDynamiskPeriode={verdier.erDynamiskPeriode}
          lagreSakslisteSorteringTidsintervallDato={action('button-click')}
          lagreSakslisteSorteringNumeriskIntervall={action('button-click')}
          fra={verdier.fra}
          til={verdier.til}
          fomDato={verdier.fomDato}
          tomDato={verdier.tomDato}
        />
      )}
    />
  );
};

export const skalViseSorteringsvelgerNårKunTilbakekrevingErValgt = () => {
  const [verdier, leggTilVerdi] = useState({
    sortering: koSortering.BEHANDLINGSFRIST,
    fra: 2,
    til: 3,
    fomDato: undefined,
    tomDato: undefined,
    erDynamiskPeriode: true,
  });
  const lagre = useCallback((_sakslisteId, sorteringType) => {
    leggTilVerdi((oldState) => ({
      ...oldState,
      sortering: sorteringType,
    }));
  }, []);
  const lagreDynamiskPeriode = useCallback(() => {
    leggTilVerdi((oldState) => ({
      ...oldState,
      erDynamiskPeriode: !oldState.erDynamiskPeriode,
    }));
  }, []);

  return (
    <Form
      onSubmit={() => undefined}
      initialValues={verdier}
      render={() => (
        <SorteringVelger
          valgtSakslisteId={1}
          valgteBehandlingtyper={[{
            kode: behandlingType.TILBAKEBETALING,
            navn: 'Tilbakekreving',
          }]}
          lagreSakslisteSortering={lagre}
          lagreSakslisteSorteringErDynamiskPeriode={lagreDynamiskPeriode}
          valgtAvdelingEnhet="NAV Viken"
          erDynamiskPeriode={verdier.erDynamiskPeriode}
          lagreSakslisteSorteringTidsintervallDato={action('button-click')}
          lagreSakslisteSorteringNumeriskIntervall={action('button-click')}
          fra={verdier.fra}
          til={verdier.til}
          fomDato={verdier.fomDato}
          tomDato={verdier.tomDato}
        />
      )}
    />
  );
};
