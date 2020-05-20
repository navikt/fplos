import React, { useState, useCallback } from 'react';
import { Form } from 'react-final-form';
import { action } from '@storybook/addon-actions';

import kodeverkTyper from 'kodeverk/kodeverkTyper';
import SorteringVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/SorteringVelger';
import behandlingType from 'kodeverk/behandlingType';
import koSortering from 'kodeverk/KoSortering';

import withIntl from '../../../decorators/withIntl';

const sorteringsTyper = {
  [kodeverkTyper.KO_SORTERING]: [{
    kode: koSortering.BEHANDLINGSFRIST,
    kodeverk: 'KO_SORTERING',
    navn: 'Dato for behandlingsfrist',
    felttype: 'DATO',
    feltkategori: 'UNIVERSAL',
  }, {
    kode: koSortering.OPPRETT_BEHANDLING,
    kodeverk: 'KO_SORTERING',
    navn: 'Dato for opprettelse av behandling',
    felttype: 'DATO',
    feltkategori: 'UNIVERSAL',
  }, {
    kode: koSortering.FORSTE_STONADSDAG,
    kodeverk: 'KO_SORTERING',
    navn: 'Dato for første stønadsdag',
    felttype: 'DATO',
    feltkategori: 'UNIVERSAL',
  }, {
    kode: koSortering.BELOP,
    kodeverk: 'KO_SORTERING',
    navn: 'Feilutbetalt beløp',
    felttype: 'HELTALL',
    feltkategori: 'TILBAKEKREVING',
  }, {
    kode: koSortering.FEILUTBETALINGSTART,
    kodeverk: 'KO_SORTERING',
    navn: 'Dato for første feilutbetaling',
    felttype: 'DATO',
    feltkategori: 'TILBAKEKREVING',
  }],
};

export default {
  title: 'avdelingsleder/behandlingskoer/SorteringVelger',
  component: SorteringVelger,
  decorators: [withIntl],
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
          alleKodeverk={sorteringsTyper}
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
          alleKodeverk={sorteringsTyper}
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
