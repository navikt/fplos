import React from 'react';
import sinon from 'sinon';
import { IntlShape } from 'react-intl';
import { Form } from 'react-final-form';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import AndreKriterierType from 'kodeverk/andreKriterierType';
import { InputField } from 'form/FinalFields';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import UtvalgskriterierForSakslisteForm from './UtvalgskriterierForSakslisteForm';
import AutoLagringVedBlur from './AutoLagringVedBlur';
import BehandlingstypeVelger from './BehandlingstypeVelger';

describe('<UtvalgskriterierForSakslisteForm>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };

  it('skal vise form som lar avdelingsleder endre navn på saksliste', () => {
    const saksliste = {
      sakslisteId: 1,
      navn: 'Nyansatte',
      sistEndret: '2017-08-31',
      andreKriterierTyper: [{
        kode: AndreKriterierType.TIL_BESLUTTER,
        navn: 'Til beslutter',
      }, {
        kode: AndreKriterierType.REGISTRER_PAPIRSOKNAD,
        navn: 'Registrer papirsøknad',
      }],
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
    };

    const wrapper = shallowWithIntl(<UtvalgskriterierForSakslisteForm.WrappedComponent
      intl={intl as IntlShape}
      valgtSaksliste={saksliste}
      valgtAvdelingEnhet="1"
      hentAvdelingensSakslister={sinon.spy()}
      hentOppgaverForAvdelingAntall={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: { erDynamiskPeriode: false } });

    expect(wrapper.find(AutoLagringVedBlur)).toHaveLength(1);
    expect(wrapper.find(BehandlingstypeVelger)).toHaveLength(1);
    expect(wrapper.find(InputField)).toHaveLength(1);
  });

  it(
    'skal vise default-navn for sakslisten når dette ikke er satt fra før',
    () => {
      const saksliste = {
        sakslisteId: 1,
        navn: undefined,
        sistEndret: '2017-08-31',
        saksbehandlerIdenter: [],
        antallBehandlinger: 1,
      };

      const wrapper = shallowWithIntl(<UtvalgskriterierForSakslisteForm.WrappedComponent
        intl={intl as IntlShape}
        valgtSaksliste={saksliste}
        valgtAvdelingEnhet="1"
        hentAvdelingensSakslister={sinon.spy()}
        hentOppgaverForAvdelingAntall={sinon.spy()}
      />);

      const initialValues = wrapper.prop('initialValues');
      expect(initialValues).toEqual({
        sakslisteId: 1,
        navn: 'Ny behandlingskø',
        sortering: undefined,
        fagsakYtelseType: '',
        fra: undefined,
        til: undefined,
        fomDato: undefined,
        tomDato: undefined,
        erDynamiskPeriode: undefined,
      });
    },
  );

  it('skal vise navn for sakslisten når dette er satt fra før', () => {
    const saksliste = {
      sakslisteId: 1,
      navn: 'Nyansatte',
      sistEndret: '2017-08-31',
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
    };

    const wrapper = shallowWithIntl(<UtvalgskriterierForSakslisteForm.WrappedComponent
      intl={intl as IntlShape}
      valgtSaksliste={saksliste}
      valgtAvdelingEnhet="1"
      hentAvdelingensSakslister={sinon.spy()}
      hentOppgaverForAvdelingAntall={sinon.spy()}
    />);

    const initialValues = wrapper.prop('initialValues');
    expect(initialValues).toEqual({
      sakslisteId: 1,
      navn: 'Nyansatte',
      sortering: undefined,
      fagsakYtelseType: '',
      fra: undefined,
      til: undefined,
      fomDato: undefined,
      tomDato: undefined,
      erDynamiskPeriode: undefined,
    });
  });

  it('skal lagre sakslistenavn ved blur i navnefelt', () => {
    const saksliste = {
      sakslisteId: 1,
      navn: 'Nyansatte',
      sistEndret: '2017-08-31',
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
    };

    requestApi.mock(RestApiPathsKeys.OPPGAVE_ANTALL.name);
    requestApi.mock(RestApiPathsKeys.LAGRE_SAKSLISTE_NAVN.name);

    const wrapper = shallowWithIntl(<UtvalgskriterierForSakslisteForm.WrappedComponent
      intl={intl as IntlShape}
      valgtSaksliste={saksliste}
      valgtAvdelingEnhet="1"
      hentAvdelingensSakslister={sinon.spy()}
      hentOppgaverForAvdelingAntall={sinon.spy()}
      // @ts-ignore
    />).find(Form).renderProp('render')({ values: { erDynamiskPeriode: false } });

    const lagreComp = wrapper.find(AutoLagringVedBlur);

    lagreComp.prop('lagre')({
      sakslisteId: 1,
      navn: 'Foreldrepenger',
    });

    const lagreSakslisteNavnCallData = requestApi.getRequestMockData(RestApiPathsKeys.LAGRE_SAKSLISTE_NAVN.name);
    expect(lagreSakslisteNavnCallData).toHaveLength(1);
    expect(lagreSakslisteNavnCallData[0].params.sakslisteId).toEqual(1);
    expect(lagreSakslisteNavnCallData[0].params.navn).toEqual('Foreldrepenger');
    expect(lagreSakslisteNavnCallData[0].params.avdelingEnhet).toEqual('1');
  });

  it('skal sette opp korrekt formstate for andrekriterier', () => {
    const saksliste = {
      sakslisteId: 1,
      navn: 'Nyansatte',
      sistEndret: '2017-08-31',
      saksbehandlerIdenter: [],
      andreKriterier: [{
        andreKriterierType: {
          kode: AndreKriterierType.TIL_BESLUTTER,
          navn: 'Til beslutter',
        },
        inkluder: true,
      }, {
        andreKriterierType: {
          kode: AndreKriterierType.REGISTRER_PAPIRSOKNAD,
          navn: 'Registrer papirsoknad',
        },
        inkluder: false,
      }],
      antallBehandlinger: 1,
    };

    const wrapper = shallowWithIntl(<UtvalgskriterierForSakslisteForm.WrappedComponent
      intl={intl as IntlShape}
      valgtSaksliste={saksliste}
      valgtAvdelingEnhet="1"
      hentAvdelingensSakslister={sinon.spy()}
      hentOppgaverForAvdelingAntall={sinon.spy()}
    />);

    const initialValues = wrapper.prop('initialValues');
    expect(initialValues).toEqual({
      sakslisteId: 1,
      navn: 'Nyansatte',
      sortering: undefined,
      fagsakYtelseType: '',
      fra: undefined,
      til: undefined,
      fomDato: undefined,
      tomDato: undefined,
      erDynamiskPeriode: undefined,
      [AndreKriterierType.REGISTRER_PAPIRSOKNAD]: true,
      [`${AndreKriterierType.REGISTRER_PAPIRSOKNAD}_inkluder`]: false,
      [AndreKriterierType.TIL_BESLUTTER]: true,
      [`${AndreKriterierType.TIL_BESLUTTER}_inkluder`]: true,
    });
  });
});
