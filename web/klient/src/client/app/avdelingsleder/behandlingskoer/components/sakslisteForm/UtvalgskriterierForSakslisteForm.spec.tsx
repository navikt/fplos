import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { IntlShape } from 'react-intl';
import { Form } from 'react-final-form';

import RestApiTestMocker from 'testHelpers/RestApiTestMocker';
import andreKriterierType from 'kodeverk/andreKriterierType';
import { InputField } from 'form/FinalFields';
import { RestApiPathsKeys } from 'data/restApiPaths';
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
        kode: andreKriterierType.TIL_BESLUTTER,
        navn: 'Til beslutter',
      }, {
        kode: andreKriterierType.REGISTRER_PAPIRSOKNAD,
        navn: 'Registrer papirsøknad',
      }],
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
    };

    new RestApiTestMocker()
      .withDummyRunner()
      .runTest(() => {
        const wrapper = shallowWithIntl(<UtvalgskriterierForSakslisteForm.WrappedComponent
          intl={intl as IntlShape}
          valgtSaksliste={saksliste}
          valgtAvdelingEnhet="1"
          hentAvdelingensSakslister={sinon.spy()}
          hentOppgaverForAvdelingAntall={sinon.spy()}
          // @ts-ignore
        />).find(Form).renderProp('render')({ values: { erDynamiskPeriode: false } });

        expect(wrapper.find(AutoLagringVedBlur)).to.have.length(1);
        expect(wrapper.find(BehandlingstypeVelger)).to.have.length(1);
        expect(wrapper.find(InputField)).to.have.length(1);
      });
  });

  it('skal vise default-navn for sakslisten når dette ikke er satt fra før', () => {
    const saksliste = {
      sakslisteId: 1,
      navn: undefined,
      sistEndret: '2017-08-31',
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
    };

    new RestApiTestMocker()
      .withDummyRunner()
      .runTest(() => {
        const wrapper = shallowWithIntl(<UtvalgskriterierForSakslisteForm.WrappedComponent
          intl={intl as IntlShape}
          valgtSaksliste={saksliste}
          valgtAvdelingEnhet="1"
          hentAvdelingensSakslister={sinon.spy()}
          hentOppgaverForAvdelingAntall={sinon.spy()}
        />);

        const initialValues = wrapper.prop('initialValues');
        expect(initialValues).to.eql({
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
      });
  });

  it('skal vise navn for sakslisten når dette er satt fra før', () => {
    const saksliste = {
      sakslisteId: 1,
      navn: 'Nyansatte',
      sistEndret: '2017-08-31',
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
    };

    new RestApiTestMocker()
      .withDummyRunner()
      .runTest(() => {
        const wrapper = shallowWithIntl(<UtvalgskriterierForSakslisteForm.WrappedComponent
          intl={intl as IntlShape}
          valgtSaksliste={saksliste}
          valgtAvdelingEnhet="1"
          hentAvdelingensSakslister={sinon.spy()}
          hentOppgaverForAvdelingAntall={sinon.spy()}
        />);

        const initialValues = wrapper.prop('initialValues');
        expect(initialValues).to.eql({
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
  });

  it('skal lagre sakslistenavn ved blur i navnefelt', () => {
    const saksliste = {
      sakslisteId: 1,
      navn: 'Nyansatte',
      sistEndret: '2017-08-31',
      saksbehandlerIdenter: [],
      antallBehandlinger: 1,
    };

    const lagreSakslisteNavnFn = sinon.spy();

    new RestApiTestMocker()
      .withRestCallRunner(RestApiPathsKeys.OPPGAVE_ANTALL, {})
      .withRestCallRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_NAVN, { startRequest: (params) => { lagreSakslisteNavnFn(params); return Promise.resolve(); } })
      .runTest(() => {
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

        expect(lagreSakslisteNavnFn.calledOnce).to.be.true;
        const { args } = lagreSakslisteNavnFn.getCalls()[0];
        expect(args).to.have.length(1);
        expect(args[0].sakslisteId).to.eql(1);
        expect(args[0].navn).to.eql('Foreldrepenger');
        expect(args[0].avdelingEnhet).to.eql('1');
      });
  });

  it('skal sette opp korrekt formstate for andrekriterier', () => {
    const saksliste = {
      sakslisteId: 1,
      navn: 'Nyansatte',
      sistEndret: '2017-08-31',
      saksbehandlerIdenter: [],
      andreKriterier: [{
        andreKriterierType: {
          kode: andreKriterierType.TIL_BESLUTTER,
          navn: 'Til beslutter',
        },
        inkluder: true,
      }, {
        andreKriterierType: {
          kode: andreKriterierType.REGISTRER_PAPIRSOKNAD,
          navn: 'Registrer papirsoknad',
        },
        inkluder: false,
      }],
      antallBehandlinger: 1,
    };

    new RestApiTestMocker()
      .withDummyRunner()
      .runTest(() => {
        const wrapper = shallowWithIntl(<UtvalgskriterierForSakslisteForm.WrappedComponent
          intl={intl as IntlShape}
          valgtSaksliste={saksliste}
          valgtAvdelingEnhet="1"
          hentAvdelingensSakslister={sinon.spy()}
          hentOppgaverForAvdelingAntall={sinon.spy()}
        />);

        const initialValues = wrapper.prop('initialValues');
        expect(initialValues).to.eql({
          sakslisteId: 1,
          navn: 'Nyansatte',
          sortering: undefined,
          fagsakYtelseType: '',
          fra: undefined,
          til: undefined,
          fomDato: undefined,
          tomDato: undefined,
          erDynamiskPeriode: undefined,
          [andreKriterierType.REGISTRER_PAPIRSOKNAD]: true,
          [`${andreKriterierType.REGISTRER_PAPIRSOKNAD}_inkluder`]: false,
          [andreKriterierType.TIL_BESLUTTER]: true,
          [`${andreKriterierType.TIL_BESLUTTER}_inkluder`]: true,
        });
      });
  });
});
