import React from 'react';
import { shallow } from 'enzyme';
import { Link } from 'react-router-dom';
import { FormattedMessage } from 'react-intl';
import Panel from 'nav-frontend-paneler';

import MissingPage from './MissingPage';

describe('<MissingPage>', () => {
  it(
    'skal vise en feilmelding og en lenke som leder tilbake til hovedside',
    () => {
      const wrapper = shallow(<MissingPage />);

      expect(wrapper.find(Panel)).toHaveLength(1);
      expect(wrapper.find(FormattedMessage)).toHaveLength(2);
      const link = wrapper.find(Link);
      expect(link).toHaveLength(1);
      expect(link.prop('to')).toEqual('/');
    },
  );
});
