import React from 'react';
import { shallow } from 'enzyme';
import sinon from 'sinon';
import { Form } from 'react-final-form';
import { FormattedMessage } from 'react-intl';
import { Column } from 'nav-frontend-grid';

import andreKriterierType from 'kodeverk/andreKriterierType';
import { CheckboxField } from 'form/FinalFields';
import SaksbehandlereForSakslisteForm from './SaksbehandlereForSakslisteForm';

describe('<SaksbehandlereForSakslisteForm>', () => {
  const saksliste = {
    sakslisteId: 1,
    navn: 'Nyansatte',
    sistEndret: '2017-08-31',
    andreKriterier: [{
      andreKriterierType: {
        kode: andreKriterierType.TIL_BESLUTTER,
        navn: 'Til beslutter',
      },
      inkluder: true,
    }, {
      andreKriterierType: {
        kode: andreKriterierType.REGISTRER_PAPIRSOKNAD,
        navn: 'Registrer papirsøknad',
      },
      inkluder: true,
    }],
    saksbehandlerIdenter: [],
    antallBehandlinger: 1,
  };

  it(
    'skal vise tekst når avdelingen ikke har tilordnede saksbehandlere',
    () => {
      const wrapper = shallow(<SaksbehandlereForSakslisteForm
        valgtSaksliste={saksliste}
        hentAvdelingensSakslister={sinon.spy()}
        avdelingensSaksbehandlere={[]}
        valgtAvdelingEnhet="1"
        // @ts-ignore
      />).find(Form).renderProp('render')({});

      const melding = wrapper.find(FormattedMessage);
      expect(melding).toHaveLength(2);
      expect(melding.last().prop('id')).toEqual('SaksbehandlereForSakslisteForm.IngenSaksbehandlere');
    },
  );

  it(
    'skal vise kun en kolonne med saksbehandlere når det er tilordnet en saksbehandler',
    () => {
      const saksbehandlere = [{
        brukerIdent: 'TEST1',
        navn: 'Espen Utvikler',
        avdelingsnavn: ['NAV Oslo'],
      }];

      const wrapper = shallow(<SaksbehandlereForSakslisteForm
        valgtSaksliste={saksliste}
        avdelingensSaksbehandlere={saksbehandlere}
        hentAvdelingensSakslister={sinon.spy()}
        valgtAvdelingEnhet="1"
        // @ts-ignore
      />).find(Form).renderProp('render')({});

      expect(wrapper.find(FormattedMessage)).toHaveLength(1);

      const kolonner = wrapper.find(Column);
      expect(kolonner).toHaveLength(2);

      const checkBox = kolonner.first().find(CheckboxField);
      expect(checkBox).toHaveLength(1);
      expect(checkBox.prop('name')).toEqual('TEST1');
      expect(checkBox.prop('label')).toEqual('Espen Utvikler');

      expect(kolonner.last().find(CheckboxField)).toHaveLength(0);
    },
  );

  it(
    'skal vise to kolonner med saksbehandlere når det er tilordnet to saksbehandler',
    () => {
      const saksbehandlere = [{
        brukerIdent: 'TEST1',
        navn: 'Espen Utvikler',
        avdelingsnavn: ['NAV Oslo'],
      }, {
        brukerIdent: 'TEST2',
        navn: 'Auto Joachim',
        avdelingsnavn: ['NAV Bærum'],
      }];

      const wrapper = shallow(<SaksbehandlereForSakslisteForm
        valgtSaksliste={saksliste}
        avdelingensSaksbehandlere={saksbehandlere}
        hentAvdelingensSakslister={sinon.spy()}
        valgtAvdelingEnhet="1"
        // @ts-ignore
      />).find(Form).renderProp('render')({});

      expect(wrapper.find(FormattedMessage)).toHaveLength(1);

      const kolonner = wrapper.find(Column);
      expect(kolonner).toHaveLength(2);

      const checkBox1 = kolonner.first().find(CheckboxField);
      expect(checkBox1).toHaveLength(1);
      expect(checkBox1.prop('name')).toEqual('TEST2');
      expect(checkBox1.prop('label')).toEqual('Auto Joachim');

      const checkBox2 = kolonner.last().find(CheckboxField);
      expect(checkBox2).toHaveLength(1);
      expect(checkBox2.prop('name')).toEqual('TEST1');
      expect(checkBox2.prop('label')).toEqual('Espen Utvikler');
    },
  );
});
