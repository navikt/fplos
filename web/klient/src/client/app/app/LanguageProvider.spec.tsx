import React from 'react';
import { FormattedMessage } from 'react-intl';

import { mountWithIntl } from 'testHelpers/intl-enzyme-test-helper';

import data from '../sprak/nb_NO.json';
import LanguageProvider from './LanguageProvider';

describe('<LanguageProvider>', () => {
  it('skal sette opp react-intl', () => {
    const wrapper = mountWithIntl((
      <LanguageProvider>
        <FormattedMessage id="Header.Foreldrepenger" tagName="span" />
      </LanguageProvider>
    ));

    const intlProvider = wrapper.find('IntlProvider');
    expect(intlProvider).toHaveLength(1);
    expect(intlProvider.prop('messages')).toEqual(data);
    const span = wrapper.find('span');
    expect(span).toHaveLength(1);
    expect(span.text()).toEqual('Svangerskap, fødsel og adopsjon');
  });
});
