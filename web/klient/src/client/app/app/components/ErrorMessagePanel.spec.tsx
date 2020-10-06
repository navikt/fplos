import React from 'react';
import { expect } from 'chai';
import { IntlShape } from 'react-intl';
import { Undertekst } from 'nav-frontend-typografi';

import EventType from 'data/rest-api/src/requestApi/eventType';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import ErrorMessagePanel from './ErrorMessagePanel';

describe('<ErrorMessagePanel>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };

  it('skal vise feilmelding', () => {
    const wrapper = shallowWithIntl(<ErrorMessagePanel.WrappedComponent
      intl={intl as IntlShape}
      queryStrings={{
        errormessage: 'Error!',
      }}
      removeErrorMessage={() => undefined}
    />);

    const div = wrapper.find(Undertekst);
    expect(div).to.have.length(1);
    expect(div.childAt(0).text()).to.eql('Error! ');

    expect(wrapper.find('a')).to.have.length(0);
  });

  it('skal erstatte spesialtegn i feilmelding', () => {
    const wrapper = shallowWithIntl(<ErrorMessagePanel.WrappedComponent
      intl={intl as IntlShape}
      queryStrings={{
        errormessage: 'Høna &amp; egget og &#34;test1&#34; og &#39;test2&#39;',
      }}
      removeErrorMessage={() => undefined}
    />);

    const div = wrapper.find(Undertekst);
    expect(div).to.have.length(1);
    expect(div.childAt(0).text()).to.eql('Høna & egget og "test1" og \'test2\' ');
  });

  it('skal sette sammen feil fra ulike kilder til en struktur', () => {
    const wrapper = shallowWithIntl(<ErrorMessagePanel.WrappedComponent
      intl={intl as IntlShape}
      errorMessages={[{
        type: EventType.REQUEST_ERROR,
        text: 'Feilet',
      }]}
      queryStrings={{
        errormessage: 'Dette er en feil',
      }}
      removeErrorMessage={() => undefined}
    />);

    const div = wrapper.find(Undertekst);
    expect(div).to.have.length(2);
    expect(div.at(0).childAt(0).text()).to.eql('Dette er en feil ');
    expect(div.at(1).childAt(0).text()).to.eql('Feilet ');
  });
});
