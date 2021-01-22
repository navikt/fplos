import React from 'react';
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
      removeErrorMessages={() => undefined}
    />);

    const div = wrapper.find(Undertekst);
    expect(div).toHaveLength(1);
    expect(div.childAt(0).text()).toEqual('Error! ');

    expect(wrapper.find('a')).toHaveLength(0);
  });

  it('skal erstatte spesialtegn i feilmelding', () => {
    const wrapper = shallowWithIntl(<ErrorMessagePanel.WrappedComponent
      intl={intl as IntlShape}
      queryStrings={{
        errormessage: 'Høna &amp; egget og &#34;test1&#34; og &#39;test2&#39;',
      }}
      removeErrorMessages={() => undefined}
    />);

    const div = wrapper.find(Undertekst);
    expect(div).toHaveLength(1);
    expect(div.childAt(0).text()).toEqual('Høna & egget og "test1" og \'test2\' ');
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
      removeErrorMessages={() => undefined}
    />);

    const div = wrapper.find(Undertekst);
    expect(div).toHaveLength(2);
    expect(div.at(0).childAt(0).text()).toEqual('Dette er en feil ');
    expect(div.at(1).childAt(0).text()).toEqual('Feilet ');
  });
});
