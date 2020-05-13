
import React from 'react';
import { expect } from 'chai';
import { Undertittel, Undertekst } from 'nav-frontend-typografi';

import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import Image from 'sharedComponents/Image';
import PersonInfo from './PersonInfo';
import AlderVisning from './Aldervisning';
import MerkePanel from './Merkepanel';

describe('<PersonInfo>', () => {
  it('skal sjekke at props blir brukt korrekt', () => {
    const person = {
      navn: 'frida',
      alder: 40,
      personnummer: '12345678910',
      erKvinne: true,
      erDod: false,
      erVerge: true,
      diskresjonskode: '6',
      dodsdato: '2017.01.01',
      personstatusType: {
        kode: 'test',
        navn: 'test',
      },
    };
    const wrapper = shallowWithIntl(<PersonInfo.WrappedComponent
      intl={intlMock}
      person={person}
    />);

    const image = wrapper.find(Image);
    expect(image.prop('alt')).to.eql('Personinformasjon');

    const innholdstittel = wrapper.find(Undertittel);
    expect(innholdstittel.childAt(0).text()).to.eql('frida');

    const aldervisning = wrapper.find(AlderVisning);
    expect(aldervisning.prop('alder')).to.eql(40);

    const normaltekst = wrapper.find(Undertekst);
    expect(normaltekst.childAt(0).text()).to.eql('12345678910');

    const merkepanel = wrapper.find(MerkePanel);
    expect(merkepanel.prop('diskresjonskode')).to.eql('6');
  });
});
