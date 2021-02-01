import React from 'react';
import { shallow } from 'enzyme';
import { IntlShape } from 'react-intl';
import { Undertekst } from 'nav-frontend-typografi';

import { intlMock } from 'testHelpers/intl-enzyme-test-helper';

import { Label } from './Label';

const FORMATTED_MESSAGE = 'En formatert melding';

describe('<Label>', () => {
  const intl: Partial<IntlShape> = { ...intlMock, formatMessage: () => FORMATTED_MESSAGE };

  it('skal ikke formatere input hvis den er en node', () => {
    const wrapper = shallow(<Label input="Hei" intl={intl as IntlShape} />);
    let typoElement = wrapper.find(Undertekst);

    expect(typoElement).toHaveLength(1);
    expect(typoElement.at(0).props().children).toEqual('Hei');

    const spanInput = <span>Hei</span>;
    wrapper.setProps({ input: spanInput });
    wrapper.update();
    typoElement = wrapper.find(Undertekst);

    expect(typoElement).toHaveLength(1);
    expect(typoElement.at(0).props().children).toEqual(spanInput);
  });

  it('skal formatere input hvis den er en meldingsdefinisjon', () => {
    const wrapper = shallow(<Label input={{ id: 'Hei' }} intl={intl as IntlShape} />);
    const typoElement = wrapper.find(Undertekst);

    expect(typoElement).toHaveLength(1);
    expect(typoElement.at(0).props().children).toEqual(FORMATTED_MESSAGE);
  });

  it('skal rendre null hvis input er tom', () => {
    const wrapper = shallow(<Label intl={intl as IntlShape} />);
    expect(wrapper.html()).toBeNull();
  });
});
