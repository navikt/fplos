import React from 'react';
import { expect } from 'chai';
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
    expect(intlProvider).to.have.length(1);
    expect(intlProvider.prop('messages')).to.eql(data);
    const span = wrapper.find('span');
    expect(span).to.have.length(1);
    expect(span.text()).to.eql('Svangerskap, fødsel og adopsjon');
  });
});
