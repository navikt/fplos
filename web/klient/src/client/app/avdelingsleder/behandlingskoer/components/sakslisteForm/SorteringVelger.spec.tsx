import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { IntlShape } from 'react-intl';

import RestApiTestMocker from 'testHelpers/RestApiTestMocker';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import { RestApiPathsKeys } from 'data/restApiPaths';
import KoSortering from 'kodeverk/KoSortering';
import { RadioGroupField, RadioOption } from 'form/FinalFields';
import SorteringVelger from './SorteringVelger';

describe('<SorteringVelger>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };

  const koSorteringTyper = [{
    kode: KoSortering.OPPRETT_BEHANDLING,
    navn: 'opprett',
    felttype: '',
    feltkategori: '',
  }, {
    kode: KoSortering.BEHANDLINGSFRIST,
    navn: 'frist',
    felttype: '',
    feltkategori: '',
  }];

  it('skal vise radioknapper for alle sorteringsvalg', () => {
    new RestApiTestMocker()
      .withKodeverk(kodeverkTyper.KO_SORTERING, koSorteringTyper)
      .withDummyRunner()
      .runTest(() => {
        const wrapper = shallowWithIntl(<SorteringVelger.WrappedComponent
          intl={intl as IntlShape}
          valgtSakslisteId={1}
          valgtAvdelingEnhet="1"
          erDynamiskPeriode={false}
          valgteBehandlingtyper={[]}
          fra={10}
          til={10}
          hentAvdelingensSakslister={sinon.spy()}
          hentAntallOppgaver={sinon.spy()}
        />);

        const options = wrapper.find(RadioOption);
        expect(options).to.have.length(2);
        expect(options.first().prop('value')).to.eql(KoSortering.OPPRETT_BEHANDLING);
        expect(options.last().prop('value')).to.eql(KoSortering.BEHANDLINGSFRIST);
      });
  });

  it('skal lagre sortering ved klikk på radioknapp', () => {
    const lagreSorteringFn = sinon.spy();

    new RestApiTestMocker()
      .withKodeverk(kodeverkTyper.KO_SORTERING, koSorteringTyper)
      .withRestCallRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING,
        { startRequest: (params) => { lagreSorteringFn(params); return Promise.resolve(); } })
      .withRestCallRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_INTERVALL,
        { startRequest: () => undefined })
      .runTest(() => {
        const wrapper = shallowWithIntl(<SorteringVelger.WrappedComponent
          intl={intl as IntlShape}
          valgtSakslisteId={1}
          valgtAvdelingEnhet="3"
          erDynamiskPeriode={false}
          valgteBehandlingtyper={[]}
          fra={10}
          til={10}
          hentAvdelingensSakslister={sinon.spy()}
          hentAntallOppgaver={sinon.spy()}
        />);

        const felt = wrapper.find(RadioGroupField);
        felt.prop('onChange')(KoSortering.OPPRETT_BEHANDLING);

        expect(lagreSorteringFn.calledOnce).to.be.true;
        const { args } = lagreSorteringFn.getCalls()[0];
        expect(args).to.have.length(1);
        expect(args[0].sakslisteId).to.eql(1);
        expect(args[0].sakslisteSorteringValg).to.eql(KoSortering.OPPRETT_BEHANDLING);
        expect(args[0].avdelingEnhet).to.eql('3');
      });
  });
});
