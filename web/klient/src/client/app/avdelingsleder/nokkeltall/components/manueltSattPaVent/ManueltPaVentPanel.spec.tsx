import React from 'react';
import sinon from 'sinon';
import { Form } from 'react-final-form';
import dayjs from 'dayjs';
import { IntlShape } from 'react-intl';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import KodeverkType from 'kodeverk/kodeverkTyper';
import { ISO_DATE_FORMAT } from 'utils/formats';
import { RadioOption, SelectField } from 'form/FinalFields';
import FagsakYtelseType from 'kodeverk/fagsakYtelseType';
import OppgaverManueltPaVent from 'types/avdelingsleder/oppgaverManueltPaVentTsType';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import ManueltPaVentPanel, { ALLE_YTELSETYPER_VALGT } from './ManueltPaVentPanel';
import ManueltPaVentGraf from './ManueltPaVentGraf';

describe('<ManueltPaVentPanel>', () => {
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

  const alleKodeverk = {
    [KodeverkType.FAGSAK_YTELSE_TYPE]: fagsakYtelseTyper,
  };

  it(
    'skal vise ukevalg i dropdown og valg av ytelsetype i radioknapper',
    () => {
      const valuesMock = {
        valgtYtelsetype: ALLE_YTELSETYPER_VALGT,
        ukevalg: '4',
      };
      const oppgaverManueltPaVent: OppgaverManueltPaVent[] = [];

      requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);

      const wrapper = shallowWithIntl(<ManueltPaVentPanel.WrappedComponent
        intl={intl as IntlShape}
        width={300}
        height={200}
        oppgaverManueltPaVent={oppgaverManueltPaVent}
        getValueFromLocalStorage={sinon.spy()}
        // @ts-ignore
      />).find(Form).renderProp('render')({ values: valuesMock });

      const select = wrapper.find(SelectField);
      expect(select).toHaveLength(1);

      const options = select.prop('selectValues') as { props: { value: string; children: string }}[];
      expect(options).toHaveLength(2);
      expect(options[0].props.value).toEqual('4');
      expect(options[0].props.children).toEqual('4 uker frem');
      expect(options[1].props.value).toEqual('8');
      expect(options[1].props.children).toEqual('8 uker frem');

      const radioOptions = wrapper.find(RadioOption);
      expect(radioOptions).toHaveLength(4);
      expect(radioOptions.first().prop('value')).toEqual('FP');
      expect(radioOptions.first().prop('label')).toEqual('Foreldrepenger');
      expect(radioOptions.at(1).prop('value')).toEqual('ES');
      expect(radioOptions.at(1).prop('label')).toEqual('Engangsstønad');
      expect(radioOptions.last().prop('value')).toEqual('ALLE');
    },
  );

  it('skal filtrere bort alt som er lengre frem i tid enn fire uker', () => {
    const valuesMock = {
      valgtYtelsetype: ALLE_YTELSETYPER_VALGT,
      ukevalg: '4',
    };
    const oppgaverManueltPaVent = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingFrist: dayjs().add(5, 'w').format(ISO_DATE_FORMAT),
      antall: 1,
    }, {
      fagsakYtelseType: fagsakYtelseTyper[1],
      behandlingFrist: dayjs().add(3, 'w').format(ISO_DATE_FORMAT),
      antall: 1,
    }];

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);

    const wrapper = shallowWithIntl(<ManueltPaVentPanel.WrappedComponent
      intl={intl as IntlShape}
      width={300}
      height={200}
      oppgaverManueltPaVent={oppgaverManueltPaVent}
      getValueFromLocalStorage={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: valuesMock });

    const graf = wrapper.find(ManueltPaVentGraf);
    expect(graf).toHaveLength(1);
    expect(graf.prop('isFireUkerValgt')).toBe(true);
    expect(graf.prop('oppgaverManueltPaVent')).toEqual([oppgaverManueltPaVent[1]]);
  });

  it(
    'skal ikke filtrere bort alt som er lengre frem i tid enn fire uker når åtte uker er valgt i filter',
    () => {
      const valuesMock = {
        valgtYtelsetype: ALLE_YTELSETYPER_VALGT,
        ukevalg: '8',
      };
      const oppgaverManueltPaVent = [{
        fagsakYtelseType: fagsakYtelseTyper[0],
        behandlingFrist: dayjs().add(4, 'w').format(ISO_DATE_FORMAT),
        antall: 1,
      }, {
        fagsakYtelseType: fagsakYtelseTyper[1],
        behandlingFrist: dayjs().add(7, 'w').format(ISO_DATE_FORMAT),
        antall: 1,
      }];

      requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);

      const wrapper = shallowWithIntl(<ManueltPaVentPanel.WrappedComponent
        intl={intl as IntlShape}
        width={300}
        height={200}
        oppgaverManueltPaVent={oppgaverManueltPaVent}
        getValueFromLocalStorage={sinon.spy()}
        // @ts-ignore
      />).find(Form).renderProp('render')({ values: valuesMock });

      const graf = wrapper.find(ManueltPaVentGraf);
      expect(graf).toHaveLength(1);
      expect(graf.prop('isFireUkerValgt')).toBe(false);
      expect(graf.prop('oppgaverManueltPaVent')).toEqual(oppgaverManueltPaVent);
    },
  );

  it('skal filtrere bort engangsstønader', () => {
    const valuesMock = {
      valgtYtelsetype: FagsakYtelseType.FORELDREPRENGER,
      ukevalg: '4',
    };
    const oppgaverManueltPaVent = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingFrist: dayjs().format(ISO_DATE_FORMAT),
      antall: 1,
    }, {
      fagsakYtelseType: fagsakYtelseTyper[1],
      behandlingFrist: dayjs().format(ISO_DATE_FORMAT),
      antall: 1,
    }];

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);

    const wrapper = shallowWithIntl(<ManueltPaVentPanel.WrappedComponent
      intl={intl as IntlShape}
      width={300}
      height={200}
      oppgaverManueltPaVent={oppgaverManueltPaVent}
      getValueFromLocalStorage={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: valuesMock });

    const graf = wrapper.find(ManueltPaVentGraf);
    expect(graf).toHaveLength(1);
    expect(graf.prop('oppgaverManueltPaVent')).toEqual([oppgaverManueltPaVent[1]]);
  });

  it('skal filtrere bort foreldrepenger', () => {
    const valuesMock = {
      valgtYtelsetype: FagsakYtelseType.ENGANGSSTONAD,
      ukevalg: '4',
    };
    const oppgaverManueltPaVent = [{
      fagsakYtelseType: fagsakYtelseTyper[0],
      behandlingFrist: dayjs().format(ISO_DATE_FORMAT),
      antall: 1,
    }, {
      fagsakYtelseType: fagsakYtelseTyper[1],
      behandlingFrist: dayjs().format(ISO_DATE_FORMAT),
      antall: 1,
    }];

    requestApi.mock(RestApiGlobalStatePathsKeys.KODEVERK.name, alleKodeverk);

    const wrapper = shallowWithIntl(<ManueltPaVentPanel.WrappedComponent
      intl={intl as IntlShape}
      width={300}
      height={200}
      oppgaverManueltPaVent={oppgaverManueltPaVent}
      getValueFromLocalStorage={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: valuesMock });

    const graf = wrapper.find(ManueltPaVentGraf);
    expect(graf).toHaveLength(1);
    expect(graf.prop('oppgaverManueltPaVent')).toEqual([oppgaverManueltPaVent[0]]);
  });
});
