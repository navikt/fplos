import { shallow } from 'enzyme';
import React from 'react';
import { FormattedMessage } from 'react-intl';
import { expect } from 'chai';
import Table from 'sharedComponents/Table';
import { ReservasjonerTabell } from './ReservasjonerTabell';

describe('<ReservasjonerTabell>', () => {
  it('skal vise tekst som viser at ingen saksbehandlere er lagt til', () => {
    const wrapper = shallow(<ReservasjonerTabell
      reservasjoner={[]}
      valgtAvdelingEnhet="2"
    />);

    const message = wrapper.find(FormattedMessage);
    expect(message).to.have.length(2);
    expect(message.last().prop('id')).to.eql('ReservasjonerTabell.IngenReservasjoner');

    expect(wrapper.find(Table)).to.have.length(0);
  });
});
