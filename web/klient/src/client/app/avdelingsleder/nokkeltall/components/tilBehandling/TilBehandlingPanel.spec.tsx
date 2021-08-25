import React from 'react';
import dayjs from 'dayjs';
import sinon from 'sinon';
import { IntlShape } from 'react-intl';
import { Form } from 'react-final-form';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import KodeverkType from 'kodeverk/kodeverkTyper';
import { ISO_DATE_FORMAT } from 'utils/formats';
import { RadioOption, SelectField } from 'form/FinalFields';
import FagsakYtelseType from 'kodeverk/fagsakYtelseType';
import BehandlingType from 'kodeverk/behandlingType';
import OppgaveForDato from 'types/avdelingsleder/oppgaverForDatoTsType';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import { TilBehandlingPanel, ALLE_YTELSETYPER_VALGT } from './TilBehandlingPanel';
import TilBehandlingGraf from './TilBehandlingGraf';

describe('<TilBehandlingPanel>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };
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
  }];

  const forstegangssoknad = {
    kode: BehandlingType.FORSTEGANGSSOKNAD,
    navn: 'Førstegangssøknad',
  };

  const alleKodeverk = {
    [KodeverkType.BEHANDLING_TYPE]: behandlingTyper,
    [KodeverkType.FAGSAK_YTELSE_TYPE]: fagsakYtelseTyper,
  };

  it(
    'skal vise ukevalg i dropdown og valg av ytelsetype i radioknapper',
    () => {
      requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);

      const valuesMock = {
        [FagsakYtelseType.FORELDREPRENGER]: true,
        [FagsakYtelseType.ENGANGSSTONAD]: true,
        ukevalg: '2',
      };
      const oppgaverPerDato: OppgaveForDato[] = [];

      const wrapper = shallowWithIntl(<TilBehandlingPanel
        intl={intl as IntlShape}
        width={300}
        height={200}
        oppgaverPerDato={oppgaverPerDato}
        getValueFromLocalStorage={sinon.spy()}
        // @ts-ignore
      />).find(Form).renderProp('render')({ values: valuesMock });

      const select = wrapper.find(SelectField);
      expect(select).toHaveLength(1);

      const options = select.prop('selectValues') as { props: { value: string; children: string }}[];
      expect(options).toHaveLength(2);
      expect(options[0].props.value).toEqual('2');
      expect(options[0].props.children).toEqual('2 siste uker');
      expect(options[1].props.value).toEqual('4');
      expect(options[1].props.children).toEqual('4 siste uker');

      const radioOptions = wrapper.find(RadioOption);
      expect(radioOptions).toHaveLength(4);
      expect(radioOptions.first().prop('value')).toEqual('FP');
      expect(radioOptions.first().prop('label')).toEqual('Foreldrepenger');
      expect(radioOptions.at(1).prop('value')).toEqual('ES');
      expect(radioOptions.at(1).prop('label')).toEqual('Engangsstønad');
      expect(radioOptions.last().prop('value')).toEqual('ALLE');
    },
  );

  it('skal filtrere bort alt som er eldre enn to uker', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);

    const valuesMock = {
      ytelseType: 'ALLE',
      ukevalg: '2',
    };
    const oppgaverPerDato = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingType: forstegangssoknad,
      opprettetDato: dayjs().subtract(13, 'd').format(ISO_DATE_FORMAT),
      antall: 1,
    }, {
      fagsakYtelseType: fagsakYtelseTyper[1],
      behandlingType: forstegangssoknad,
      opprettetDato: dayjs().subtract(14, 'd').format(ISO_DATE_FORMAT),
      antall: 1,
    }];

    const wrapper = shallowWithIntl(<TilBehandlingPanel
      intl={intl as IntlShape}
      width={300}
      height={200}
      oppgaverPerDato={oppgaverPerDato}
      getValueFromLocalStorage={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: valuesMock });

    const graf = wrapper.find(TilBehandlingGraf);
    expect(graf).toHaveLength(1);
    expect(graf.prop('isToUkerValgt')).toBe(true);
    expect(graf.prop('oppgaverPerDato')).toEqual([oppgaverPerDato[0]]);
  });

  it(
    'skal ikke filtrere bort alt som er eldre enn to uker når fire uker er valgt i filter',
    () => {
      requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);

      const valuesMock = {
        ytelseType: 'ALLE',
        ukevalg: '4',
      };
      const oppgaverPerDato = [{
        fagsakYtelseType: fagsakYtelseTyper[0],
        behandlingType: forstegangssoknad,
        opprettetDato: dayjs().subtract(13, 'd').format(ISO_DATE_FORMAT),
        antall: 1,
      }, {
        fagsakYtelseType: fagsakYtelseTyper[1],
        behandlingType: forstegangssoknad,
        opprettetDato: dayjs().subtract(14, 'd').format(ISO_DATE_FORMAT),
        antall: 1,
      }];

      const wrapper = shallowWithIntl(<TilBehandlingPanel
        intl={intl as IntlShape}
        width={300}
        height={200}
        oppgaverPerDato={oppgaverPerDato}
        getValueFromLocalStorage={sinon.spy()}
        // @ts-ignore
      />).find(Form).renderProp('render')({ values: valuesMock });

      const graf = wrapper.find(TilBehandlingGraf);
      expect(graf).toHaveLength(1);
      expect(graf.prop('isToUkerValgt')).toBe(false);
      expect(graf.prop('oppgaverPerDato')).toEqual(oppgaverPerDato);
    },
  );

  it('skal filtrere bort engangsstønader', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);

    const valuesMock = {
      ytelseType: FagsakYtelseType.FORELDREPRENGER,
      ukevalg: '2',
    };
    const oppgaverPerDato = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingType: forstegangssoknad,
      opprettetDato: dayjs().format(ISO_DATE_FORMAT),
      antall: 1,
    }, {
      fagsakYtelseType: fagsakYtelseTyper[1],
      behandlingType: forstegangssoknad,
      opprettetDato: dayjs().format(ISO_DATE_FORMAT),
      antall: 1,
    }];

    const wrapper = shallowWithIntl(<TilBehandlingPanel
      intl={intl as IntlShape}
      width={300}
      height={200}
      oppgaverPerDato={oppgaverPerDato}
      getValueFromLocalStorage={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: valuesMock });

    const graf = wrapper.find(TilBehandlingGraf);
    expect(graf).toHaveLength(1);
    expect(graf.prop('oppgaverPerDato')).toEqual([oppgaverPerDato[1]]);
  });

  it('skal filtrere bort foreldrepenger', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);

    const valuesMock = {
      ytelseType: FagsakYtelseType.ENGANGSSTONAD,
      ukevalg: '2',
    };
    const oppgaverPerDato = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingType: forstegangssoknad,
      opprettetDato: dayjs().format(ISO_DATE_FORMAT),
      antall: 1,
    }, {
      fagsakYtelseType: fagsakYtelseTyper[1],
      behandlingType: forstegangssoknad,
      opprettetDato: dayjs().format(ISO_DATE_FORMAT),
      antall: 1,
    }];

    const wrapper = shallowWithIntl(<TilBehandlingPanel
      intl={intl as IntlShape}
      width={300}
      height={200}
      oppgaverPerDato={oppgaverPerDato}
      getValueFromLocalStorage={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: valuesMock });

    const graf = wrapper.find(TilBehandlingGraf);
    expect(graf).toHaveLength(1);
    expect(graf.prop('oppgaverPerDato')).toEqual([oppgaverPerDato[0]]);
  });

  it('skal slå sammen like behandlingstyper og opprettetDatoer', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);

    const valuesMock = {
      ytelseType: ALLE_YTELSETYPER_VALGT,
      ukevalg: '2',
    };
    const oppgaverPerDato = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingType: forstegangssoknad,
      opprettetDato: dayjs().format(ISO_DATE_FORMAT),
      antall: 1,
    }, {
      fagsakYtelseType: fagsakYtelseTyper[1],
      behandlingType: forstegangssoknad,
      opprettetDato: dayjs().format(ISO_DATE_FORMAT),
      antall: 1,
    }];

    const wrapper = shallowWithIntl(<TilBehandlingPanel
      intl={intl as IntlShape}
      width={300}
      height={200}
      oppgaverPerDato={oppgaverPerDato}
      getValueFromLocalStorage={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: valuesMock });

    const graf = wrapper.find(TilBehandlingGraf);
    expect(graf).toHaveLength(1);
    expect(graf.prop('oppgaverPerDato')).toEqual([{
      behandlingType: forstegangssoknad,
      opprettetDato: dayjs().format(ISO_DATE_FORMAT),
      antall: 2,
    }]);
  });
});
