import React from 'react';
import { expect } from 'chai';
import { IntlShape } from 'react-intl';
import { shallow } from 'enzyme';
import { Form } from 'react-final-form';
import sinon from 'sinon';
import { Knapp } from 'nav-frontend-knapper';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import { InputField } from 'form/FinalFields';
import SearchForm from './SearchForm';

describe('<SearchForm>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };
  it('skal ha et søkefelt og en søkeknapp', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT, { kanSaksbehandle: true });
    const formProps = { handleSubmit: sinon.spy(), values: { searchString: '' } };
    const wrapper = shallow(<SearchForm.WrappedComponent
      intl={intl as IntlShape}
      onSubmit={sinon.spy()}
      searchStarted
      resetSearch={sinon.spy()}
    // @ts-ignore
    />).find(Form).renderProp('render')(formProps);

    expect(wrapper.find(InputField)).to.have.length(1);
    expect(wrapper.find(Knapp)).to.have.length(1);
  });

  it('skal utføre søk når en trykker på søkeknapp', () => {
    requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT, { kanSaksbehandle: true });
    const onButtonClick = sinon.spy();
    const formProps = { handleSubmit: onButtonClick, values: { searchString: '' } };

    const wrapper = shallowWithIntl(<SearchForm.WrappedComponent
      intl={intl as IntlShape}
      onSubmit={onButtonClick}
      searchStarted
      resetSearch={sinon.spy()}
    // @ts-ignore
    />).find(Form).renderProp('render')(formProps);

    const form = wrapper.find('form');
    const preventDefault = () => undefined;
    form.simulate('submit', { preventDefault });

    expect(onButtonClick).to.have.property('callCount', 1);
  });
});
