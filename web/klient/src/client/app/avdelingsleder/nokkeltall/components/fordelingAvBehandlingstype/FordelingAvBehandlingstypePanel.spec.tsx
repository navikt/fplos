import React from 'react';
import sinon from 'sinon';
import { Form } from 'react-final-form';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import KodeverkType from 'kodeverk/kodeverkTyper';
import { shallowWithIntl } from 'testHelpers/intl-enzyme-test-helper';
import BehandlingType from 'kodeverk/behandlingType';
import { RadioOption } from 'form/FinalFields';
import FagsakYtelseType from 'kodeverk/fagsakYtelseType';
import OppgaverForAvdeling from 'types/avdelingsleder/oppgaverForAvdelingTsType';
import { FordelingAvBehandlingstypePanel } from './FordelingAvBehandlingstypePanel';
import FordelingAvBehandlingstypeGraf from './FordelingAvBehandlingstypeGraf';

describe('<FordelingAvBehandlingstypePanel>', () => {
  const fagsakYtelseTyper = [{
    kode: FagsakYtelseType.ENGANGSSTONAD,
    navn: 'Engangsstønad',
  }, {
    kode: FagsakYtelseType.FORELDREPRENGER,
    navn: 'Foreldrepenger',
  }];
  const behandlingTyper = [{
    kode: BehandlingType.FORSTEGANGSSOKNAD,
    navn: 'Førstegangssøknad',
  }, {
    kode: BehandlingType.KLAGE,
    navn: 'Klage',
  }, {
    kode: BehandlingType.DOKUMENTINNSYN,
    navn: 'Dokumentinnsyn',
  }, {
    kode: BehandlingType.REVURDERING,
    navn: 'Revurdering',
  }, {
    kode: BehandlingType.TILBAKEBETALING,
    navn: 'Tilbakebetaling',
  }, {
    kode: BehandlingType.TILBAKEBETALING_REVURDERING,
    navn: 'Tilbakebet-rev',
  }];
  const forstegangssoknad = {
    kode: BehandlingType.FORSTEGANGSSOKNAD,
    navn: 'Førstegangssøknad',
  };

  const alleKodeverk = {
    [KodeverkType.BEHANDLING_TYPE]: behandlingTyper,
    [KodeverkType.FAGSAK_YTELSE_TYPE]: fagsakYtelseTyper,
  };

  it('skal vise ytelsetyper i radioknapper', () => {
    const valuesMock = {
      valgtYtelseType: 'ALLE',
    };
    const oppgaverForAvdeling: OppgaverForAvdeling[] = [];

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);

    const wrapper = shallowWithIntl(<FordelingAvBehandlingstypePanel
      width={300}
      height={200}
      oppgaverForAvdeling={oppgaverForAvdeling}
      getValueFromLocalStorage={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: valuesMock });

    const radioOptions = wrapper.find(RadioOption);
    expect(radioOptions).toHaveLength(4);
    expect(radioOptions.first().prop('value')).toEqual('FP');
    expect(radioOptions.first().prop('label')).toEqual('Foreldrepenger');
    expect(radioOptions.at(1).prop('value')).toEqual('ES');
    expect(radioOptions.at(1).prop('label')).toEqual('Engangsstønad');
    expect(radioOptions.last().prop('value')).toEqual('ALLE');
  });

  it('skal filtrere bort engangsstønader', () => {
    const valuesMock = {
      valgtYtelseType: FagsakYtelseType.FORELDREPRENGER,
    };
    const oppgaverForAvdeling = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingType: forstegangssoknad,
      tilBehandling: true,
      antall: 1,
    }, {
      fagsakYtelseType: fagsakYtelseTyper[1],
      behandlingType: forstegangssoknad,
      tilBehandling: true,
      antall: 1,
    }];

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);
    const wrapper = shallowWithIntl(<FordelingAvBehandlingstypePanel
      width={300}
      height={200}
      oppgaverForAvdeling={oppgaverForAvdeling}
      getValueFromLocalStorage={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: valuesMock });

    const graf = wrapper.find(FordelingAvBehandlingstypeGraf);
    expect(graf).toHaveLength(1);
    expect(graf.prop('oppgaverForAvdeling')).toEqual([oppgaverForAvdeling[1]]);
  });

  it('skal filtrere bort foreldrepenger', () => {
    const valuesMock = {
      valgtYtelseType: FagsakYtelseType.ENGANGSSTONAD,
    };
    const oppgaverForAvdeling = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingType: forstegangssoknad,
      tilBehandling: true,
      antall: 1,
    }, {
      fagsakYtelseType: fagsakYtelseTyper[1],
      behandlingType: forstegangssoknad,
      tilBehandling: true,
      antall: 1,
    }];

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);

    const wrapper = shallowWithIntl(<FordelingAvBehandlingstypePanel
      width={300}
      height={200}
      oppgaverForAvdeling={oppgaverForAvdeling}
      getValueFromLocalStorage={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: valuesMock });

    const graf = wrapper.find(FordelingAvBehandlingstypeGraf);
    expect(graf).toHaveLength(1);
    expect(graf.prop('oppgaverForAvdeling')).toEqual([oppgaverForAvdeling[0]]);
  });
});
