import React from 'react';
import sinon from 'sinon';
import { Form } from 'react-final-form';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { shallowWithIntl } from 'testHelpers/intl-enzyme-test-helper';
import behandlingType from 'kodeverk/behandlingType';
import { CheckboxField } from 'form/FinalFields';
import behandlingVenteStatus from 'kodeverk/behandlingVenteStatus';

import { OppgaverSomErApneEllerPaVentPanel } from './OppgaverSomErApneEllerPaVentPanel';
import OppgaverSomErApneEllerPaVentGraf from './OppgaverSomErApneEllerPaVentGraf';

describe('<OppgaverSomErApneEllerPaVentPanel>', () => {
  const behandlingTyper = [{
    kode: behandlingType.FORSTEGANGSSOKNAD,
    navn: 'Førstegangssøknad',
  }, {
    kode: behandlingType.KLAGE,
    navn: 'Klage',
  }, {
    kode: behandlingType.DOKUMENTINNSYN,
    navn: 'Dokumentinnsyn',
  }, {
    kode: behandlingType.REVURDERING,
    navn: 'Revurdering',
  }, {
    kode: behandlingType.TILBAKEBETALING,
    navn: 'Tilbakebetaling',
  }, {
    kode: behandlingType.TILBAKEBETALING_REVURDERING,
    navn: 'Tilbakebet-rev',
  }];

  const alleKodeverk = {
    [kodeverkTyper.BEHANDLING_TYPE]: behandlingTyper,
  };

  const oppgaverApneEllerPaVent = [{
    antall: 3,
    behandlingType: {
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: 'Førstegangssøknad',
      kodeverk: '',
    },
    behandlingVenteStatus: {
      kode: behandlingVenteStatus.IKKE_PA_VENT,
      navn: 'Ikke på vent',
      kodeverk: '',
    },
    førsteUttakMåned: '2020.01.02',
  }, {
    antall: 5,
    behandlingType: {
      kode: behandlingType.REVURDERING,
      navn: 'Revurdering',
      kodeverk: '',
    },
    behandlingVenteStatus: {
      kode: behandlingVenteStatus.PA_VENT,
      navn: 'På vent',
      kodeverk: '',
    },
    førsteUttakMåned: '2020.10.02',
  }];

  it('skal vise alle behandlingstyper utenom tilbakekrevingsrelaterte i checkboxer', () => {
    const valuesMock = {};

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const wrapper = shallowWithIntl(<OppgaverSomErApneEllerPaVentPanel
      width={300}
      height={200}
      oppgaverApneEllerPaVent={oppgaverApneEllerPaVent}
      getValueFromLocalStorage={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: valuesMock });

    const checkboxFields = wrapper.find(CheckboxField);
    expect(checkboxFields).toHaveLength(4);
    expect(checkboxFields.first().prop('name')).toEqual(behandlingTyper[0].kode);
    expect(checkboxFields.first().prop('label')).toEqual(behandlingTyper[0].navn);
    expect(checkboxFields.at(1).prop('name')).toEqual(behandlingTyper[1].kode);
    expect(checkboxFields.at(1).prop('label')).toEqual(behandlingTyper[1].navn);
    expect(checkboxFields.at(2).prop('name')).toEqual(behandlingTyper[2].kode);
    expect(checkboxFields.at(2).prop('label')).toEqual(behandlingTyper[2].navn);
    expect(checkboxFields.last().prop('name')).toEqual(behandlingTyper[3].kode);
    expect(checkboxFields.last().prop('label')).toEqual(behandlingTyper[3].navn);
  });

  it('skal ikke filtrere bort data når alle checkboxer er valgt', () => {
    const valuesMock = {
      [behandlingType.FORSTEGANGSSOKNAD]: true,
      [behandlingType.REVURDERING]: true,
    };

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const wrapper = shallowWithIntl(<OppgaverSomErApneEllerPaVentPanel
      width={300}
      height={200}
      oppgaverApneEllerPaVent={oppgaverApneEllerPaVent}
      getValueFromLocalStorage={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: valuesMock });

    const graf = wrapper.find(OppgaverSomErApneEllerPaVentGraf);
    expect(graf).toHaveLength(1);
    expect(graf.prop('oppgaverApneEllerPaVent')).toEqual(oppgaverApneEllerPaVent);
  });

  it('skal filtrere bort revurderinger når kun førstegangsbehandling-checkbox er valgt', () => {
    const valuesMock = {
      [behandlingType.FORSTEGANGSSOKNAD]: true,
      [behandlingType.REVURDERING]: false,
    };

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK, alleKodeverk);

    const wrapper = shallowWithIntl(<OppgaverSomErApneEllerPaVentPanel
      width={300}
      height={200}
      oppgaverApneEllerPaVent={oppgaverApneEllerPaVent}
      getValueFromLocalStorage={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: valuesMock });

    const graf = wrapper.find(OppgaverSomErApneEllerPaVentGraf);
    expect(graf).toHaveLength(1);
    expect(graf.prop('oppgaverApneEllerPaVent')).toEqual(oppgaverApneEllerPaVent
      .filter((oppgave) => oppgave.behandlingType.kode !== behandlingType.REVURDERING));
  });
});
