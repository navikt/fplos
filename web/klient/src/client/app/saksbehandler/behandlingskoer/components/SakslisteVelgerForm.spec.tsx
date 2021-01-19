import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { IntlShape, FormattedMessage } from 'react-intl';
import { Form } from 'react-final-form';

import { RestApiPathsKeys } from 'data/restApiPaths';
import RestApiTestMocker from 'testHelpers/RestApiTestMocker';
import Image from 'sharedComponents/Image';
import LabelWithHeader from 'sharedComponents/LabelWithHeader';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import andreKriterierType from 'kodeverk/andreKriterierType';
import { SelectField } from 'form/FinalFields';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import SakslisteVelgerForm from './SakslisteVelgerForm';

describe('<SakslisteVelgerForm>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };
  it('skal vise dropdown med to sakslister', () => {
    const formProps = { };
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Testliste 1',
      behandlingTyper: [],
      fagsakYtelseTyper: [],
      andreKriterier: [],
      sortering: {
        sorteringType: {
          kode: 'test',
          navn: 'test',
        },
        fra: 1,
        til: 2,
        fomDato: '2019-01-01',
        tomDato: '2019-01-10',
        erDynamiskPeriode: false,
      },
    }, {
      sakslisteId: 2,
      navn: 'Testliste 2',
      behandlingTyper: [],
      fagsakYtelseTyper: [],
      andreKriterier: [],
      sortering: {
        sorteringType: {
          kode: 'test',
          navn: 'test',
        },
        fra: 1,
        til: 2,
        fomDato: '2019-01-01',
        tomDato: '2019-01-10',
        erDynamiskPeriode: false,
      },
    }];

    new RestApiTestMocker()
      .withDummyRunner()
      .runTest(() => {
        const wrapper = shallowWithIntl(<SakslisteVelgerForm.WrappedComponent
          intl={intl as IntlShape}
          sakslister={sakslister}
          setValgtSakslisteId={sinon.spy()}
          fetchAntallOppgaver={sinon.spy()}
          getValueFromLocalStorage={sinon.spy()}
          setValueInLocalStorage={sinon.spy()}
          removeValueFromLocalStorage={sinon.spy()}
          // @ts-ignore
        />).find(Form).renderProp('render')(formProps);

        const select = wrapper.find(SelectField);
        expect(select).to.have.length(1);
        const options = select.prop('selectValues') as { key: number; props: { value: string; children: string }}[];
        expect(options[0].key).to.eql('1');
        expect(options[0].props.value).to.eql('1');
        expect(options[0].props.children).to.eql('Testliste 1');
        expect(options[1].key).to.eql('2');
        expect(options[1].props.value).to.eql('2');
        expect(options[1].props.children).to.eql('Testliste 2');
      });
  });

  it('skal ikke vise informasjon om saksliste når ingen saksliste er valgt', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Testliste 1',
      behandlingTyper: [],
      fagsakYtelseTyper: [],
      andreKriterier: [],
      sortering: {
        sorteringType: {
          kode: 'test',
          navn: 'test',
        },
        fra: 1,
        til: 2,
        fomDato: '2019-01-01',
        tomDato: '2019-01-10',
        erDynamiskPeriode: false,
      },
    }];

    const formProps = { values: { sakslisteId: undefined } };

    new RestApiTestMocker()
      .withDummyRunner()
      .runTest(() => {
        const wrapper = shallowWithIntl(<SakslisteVelgerForm.WrappedComponent
          intl={intl as IntlShape}
          sakslister={sakslister}
          setValgtSakslisteId={sinon.spy()}
          fetchAntallOppgaver={sinon.spy()}
          getValueFromLocalStorage={sinon.spy()}
          setValueInLocalStorage={sinon.spy()}
          removeValueFromLocalStorage={sinon.spy()}
          // @ts-ignore
        />).find(Form).renderProp('render')(formProps);

        expect(wrapper.find(LabelWithHeader)).to.have.length(0);
      });
  });

  it('skal vise at alle behandlingstyper og fagsakYtelseTyper er valgt når ingen verdier er oppgitt', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Testliste 1',
      behandlingTyper: [],
      fagsakYtelseTyper: [],
      andreKriterier: [],
      sortering: {
        sorteringType: {
          kode: 'test',
          navn: 'Sortert på noko',
        },
        fra: 1,
        til: 2,
        fomDato: '2019-01-01',
        tomDato: '2019-01-10',
        erDynamiskPeriode: false,
      },
    }];

    const formProps = { values: { sakslisteId: '1' } };

    new RestApiTestMocker()
      .withDummyRunner()
      .runTest(() => {
        const wrapper = shallowWithIntl(<SakslisteVelgerForm.WrappedComponent
          intl={intl as IntlShape}
          sakslister={sakslister}
          setValgtSakslisteId={sinon.spy()}
          fetchAntallOppgaver={sinon.spy()}
          getValueFromLocalStorage={sinon.spy()}
          setValueInLocalStorage={sinon.spy()}
          removeValueFromLocalStorage={sinon.spy()}
          // @ts-ignore
        />).find(Form).renderProp('render')(formProps);

        const labels = wrapper.find(LabelWithHeader);
        expect(labels).to.have.length(4);
        expect(labels.first().prop('texts')).to.eql(['Alle']);
        expect(labels.at(0).prop('texts')).to.eql(['Alle']);
        expect(labels.at(1).prop('texts')).to.eql(['Alle']);
      });
  });

  it('skal vise at alle behandlingstyper er valgt når alle verdiene er oppgitt', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Testliste 1',
      behandlingTyper: [{
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangssøknad',
      }],
      fagsakYtelseTyper: [],
      andreKriterier: [],
      sortering: {
        sorteringType: {
          kode: 'test',
          navn: 'Sortert på noko',
        },
        fra: 1,
        til: 2,
        fomDato: '2019-01-01',
        tomDato: '2019-01-10',
        erDynamiskPeriode: false,
      },
    }];

    const formProps = { values: { sakslisteId: '1' } };

    new RestApiTestMocker()
      .withDummyRunner()
      .runTest(() => {
        // totaltBehandlingTypeAntall er satt til 1 som er lik antall behandlingstypar satt på sakslisten
        const wrapper = shallowWithIntl(<SakslisteVelgerForm.WrappedComponent
          intl={intl as IntlShape}
          sakslister={sakslister}
          setValgtSakslisteId={sinon.spy()}
          fetchAntallOppgaver={sinon.spy()}
          getValueFromLocalStorage={sinon.spy()}
          setValueInLocalStorage={sinon.spy()}
          removeValueFromLocalStorage={sinon.spy()}
          // @ts-ignore
        />).find(Form).renderProp('render')(formProps);

        const labels = wrapper.find(LabelWithHeader);
        expect(labels).to.have.length(4);
        expect(labels.first().prop('texts')).to.eql(['Alle']);
        expect(labels.at(1).prop('texts')).to.eql(['Førstegangssøknad']);
      });
  });

  it('skal vise valgte behandlingstyper og fagsakYtelseTyper', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Testliste 1',
      behandlingTyper: [{
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangssøknad',
      }, {
        kode: behandlingType.KLAGE,
        navn: 'Klage',
      }],
      fagsakYtelseTyper: [{
        kode: fagsakYtelseType.ENGANGSSTONAD,
        navn: 'Engangsstønad',
      }],
      andreKriterier: [],
      sortering: {
        sorteringType: {
          kode: 'test',
          navn: 'Sortert på noko',
        },
        fra: 1,
        til: 2,
        fomDato: '2019-01-01',
        tomDato: '2019-01-10',
        erDynamiskPeriode: false,
      },
    }];

    const formProps = { values: { sakslisteId: '1' } };

    new RestApiTestMocker()
      .withDummyRunner()
      .runTest(() => {
        const wrapper = shallowWithIntl(<SakslisteVelgerForm.WrappedComponent
          intl={intl as IntlShape}
          sakslister={sakslister}
          setValgtSakslisteId={sinon.spy()}
          fetchAntallOppgaver={sinon.spy()}
          getValueFromLocalStorage={sinon.spy()}
          setValueInLocalStorage={sinon.spy()}
          removeValueFromLocalStorage={sinon.spy()}
          // @ts-ignore
        />).find(Form).renderProp('render')(formProps);

        const labels = wrapper.find(LabelWithHeader);
        expect(labels).to.have.length(4);
        expect(labels.first().prop('texts')).to.eql(['Engangsstønad']);
        expect(labels.at(1).prop('texts')).to.eql(['Førstegangssøknad', 'Klage']);
      });
  });

  it('skal vise valgte andre kriterier som er inkluderte', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Testliste 1',
      behandlingTyper: [],
      fagsakYtelseTyper: [],
      andreKriterier: [{
        andreKriterierType: {
          kode: andreKriterierType.TIL_BESLUTTER,
          navn: 'Til beslutter',
        },
        inkluder: true,
      }],
      sortering: {
        sorteringType: {
          kode: 'test',
          navn: 'test',
        },
        fra: 1,
        til: 2,
        fomDato: '2019-01-01',
        tomDato: '2019-01-10',
        erDynamiskPeriode: false,
      },
    }];

    const formProps = { values: { sakslisteId: '1' } };
    new RestApiTestMocker()
      .withDummyRunner()
      .runTest(() => {
        const wrapper = shallowWithIntl(<SakslisteVelgerForm.WrappedComponent
          intl={intl as IntlShape}
          sakslister={sakslister}
          setValgtSakslisteId={sinon.spy()}
          fetchAntallOppgaver={sinon.spy()}
          getValueFromLocalStorage={sinon.spy()}
          setValueInLocalStorage={sinon.spy()}
          removeValueFromLocalStorage={sinon.spy()}
          // @ts-ignore
        />).find(Form).renderProp('render')(formProps);

        const labels = wrapper.find(LabelWithHeader);
        expect(labels).to.have.length(4);
        expect(labels.at(2).prop('texts')).to.eql(['Til beslutter']);
      });
  });

  it('skal vise valgte andre kriterier som er ekskludert', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Testliste 1',
      behandlingTyper: [],
      fagsakYtelseTyper: [],
      andreKriterier: [{
        andreKriterierType: {
          kode: andreKriterierType.TIL_BESLUTTER,
          navn: 'Til beslutter',
        },
        inkluder: false,
      }],
      sortering: {
        sorteringType: {
          kode: 'test',
          navn: 'test',
        },
        fra: 1,
        til: 2,
        fomDato: '2019-01-01',
        tomDato: '2019-01-10',
        erDynamiskPeriode: false,
      },
    }];

    const formProps = { values: { sakslisteId: '1' } };

    new RestApiTestMocker()
      .withDummyRunner()
      .runTest(() => {
        const wrapper = shallowWithIntl(<SakslisteVelgerForm.WrappedComponent
          intl={intl as IntlShape}
          sakslister={sakslister}
          setValgtSakslisteId={sinon.spy()}
          fetchAntallOppgaver={sinon.spy()}
          getValueFromLocalStorage={sinon.spy()}
          setValueInLocalStorage={sinon.spy()}
          removeValueFromLocalStorage={sinon.spy()}
          // @ts-ignore
        />).find(Form).renderProp('render')(formProps);

        const labels = wrapper.find(LabelWithHeader);
        expect(labels).to.have.length(4);
        expect(labels.at(2).prop('texts')).to.eql(['Uten: Til beslutter']);
      });
  });

  it('skal vise at alle andre kriterier er valgte', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Testliste 1',
      behandlingTyper: [],
      fagsakYtelseTyper: [],
      andreKriterier: [],
      sortering: {
        sorteringType: {
          kode: 'test',
          navn: 'test',
        },
        fra: 1,
        til: 2,
        fomDato: '2019-01-01',
        tomDato: '2019-01-10',
        erDynamiskPeriode: false,
      },
    }];

    const formProps = { values: { sakslisteId: '1' } };

    new RestApiTestMocker()
      .withDummyRunner()
      .runTest(() => {
        const wrapper = shallowWithIntl(<SakslisteVelgerForm.WrappedComponent
          intl={intl as IntlShape}
          sakslister={sakslister}
          setValgtSakslisteId={sinon.spy()}
          fetchAntallOppgaver={sinon.spy()}
          getValueFromLocalStorage={sinon.spy()}
          setValueInLocalStorage={sinon.spy()}
          removeValueFromLocalStorage={sinon.spy()}
          // @ts-ignore
        />).find(Form).renderProp('render')(formProps);

        const labels = wrapper.find(LabelWithHeader);
        expect(labels).to.have.length(4);
        expect(labels.at(2).prop('texts')).to.eql(['Alle']);
      });
  });

  it('skal vise køens saksbehandlere i tooltip', () => {
    const sakslister = [{
      sakslisteId: 1,
      navn: 'Testliste 1',
      behandlingTyper: [],
      fagsakYtelseTyper: [],
      andreKriterier: [],
      sortering: {
        sorteringType: {
          kode: 'test',
          navn: 'test',
        },
        fra: 1,
        til: 2,
        fomDato: '2019-01-01',
        tomDato: '2019-01-10',
        erDynamiskPeriode: false,
      },
    }];

    const saksbehandlere = [{
      brukerIdent: {
        brukerIdent: 'T120101',
        verdi: 'T120101',
      },
      navn: 'Espen Utvikler',
      avdelingsnavn: [],
    }, {
      brukerIdent: {
        brukerIdent: 'A120102',
        verdi: 'A120102',
      },
      navn: 'Auto Joachim',
      avdelingsnavn: [],
    }, {
      brukerIdent: {
        brukerIdent: 'T120102',
        verdi: 'T120102',
      },
      navn: 'Helge Ingstad',
      avdelingsnavn: [],
    }];

    const formProps = { values: { sakslisteId: '1' } };

    new RestApiTestMocker()
      .withRestCallRunner(RestApiPathsKeys.SAKSLISTE_SAKSBEHANDLERE, { data: saksbehandlere })
      .runTest(() => {
        const wrapper = shallowWithIntl(<SakslisteVelgerForm.WrappedComponent
          intl={intl as IntlShape}
          sakslister={sakslister}
          setValgtSakslisteId={sinon.spy()}
          fetchAntallOppgaver={sinon.spy()}
          getValueFromLocalStorage={sinon.spy()}
          setValueInLocalStorage={sinon.spy()}
          removeValueFromLocalStorage={sinon.spy()}
          // @ts-ignore
        />).find(Form).renderProp('render')(formProps);

        const image = wrapper.find(Image);
        expect(image).to.have.length(1);
        const tooltip = shallowWithIntl(image.first().prop('tooltip'));
        expect(tooltip.find(FormattedMessage).prop('id')).to.eql('SakslisteVelgerForm.SaksbehandlerToolip');
      });
  });
});
