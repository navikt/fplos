import React from 'react';
import { shallow } from 'enzyme';
import sinon from 'sinon';
import { Undertekst } from 'nav-frontend-typografi';

import MenuButton from './MenuButton';

describe('<MenuButton>', () => {
  it('skal rendre meny-knapp', () => {
    const onClick = sinon.spy();

    const wrapper = shallow(
      <MenuButton
        onClick={onClick}
      >
        <div>test</div>
      </MenuButton>,
    );

    const menuButton = wrapper.find('button');
    expect(menuButton).toHaveLength(1);
    expect(menuButton.prop('onClick')).toEqual(onClick);
    expect(menuButton.find(Undertekst).prop('children')).toEqual(<div>test</div>);
  });
});
