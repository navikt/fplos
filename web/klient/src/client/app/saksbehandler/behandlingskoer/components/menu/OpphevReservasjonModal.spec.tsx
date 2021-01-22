import React from 'react';
import sinon from 'sinon';
import { IntlShape } from 'react-intl';
import { Form } from 'react-final-form';

import behandlingStatus from 'kodeverk/behandlingStatus';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import { TextAreaField } from 'form/FinalFields';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import behandlingType from 'kodeverk/behandlingType';
import OpphevReservasjonModal from './OpphevReservasjonModal';

describe('<OpphevReservasjonModal>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };
  const oppgave = {
    id: 1,
    status: {
      erReservert: false,
      reservertTilTidspunkt: '2017-08-02T00:54:25.455',
    },
    saksnummer: 1,
    behandlingId: '2',
    personnummer: '1234567',
    navn: 'Espen Utvikler',
    behandlingstype: {
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: '',
    },
    opprettetTidspunkt: '2017-01-01',
    behandlingsfrist: '2017-01-01',
    erTilSaksbehandling: true,
    fagsakYtelseType: {
      kode: fagsakYtelseType.ENGANGSSTONAD,
      navn: '',
    },
    behandlingStatus: {
      kode: behandlingStatus.OPPRETTET,
      navn: '',
    },
    system: '',
    href: '',
  };

  it(
    'skal rendre modal for å oppgi begrunnelse for oppheving av reservasjon',
    () => {
      const wrapper = shallowWithIntl(
        <OpphevReservasjonModal.WrappedComponent
          intl={intl as IntlShape}
          showModal
          oppgave={oppgave}
          cancel={sinon.spy()}
          toggleMenu={sinon.spy()}
          hentReserverteOppgaver={sinon.spy()}
        />,
      );

      const form = wrapper.find(Form);
      expect(form).toHaveLength(1);

      const handleSubmitFn = sinon.spy();
      const renderFn = form.prop('render') as ({ handleSubmit: any }) => void;
      // @ts-ignore Fiks
      const formWrapper = shallowWithIntl(renderFn({
        handleSubmit: handleSubmitFn,
      }));

      expect(formWrapper.find(TextAreaField)).toHaveLength(1);

      formWrapper.find('form').simulate('submit');

      expect(handleSubmitFn.calledOnce).toBe(true);
    },
  );
});
