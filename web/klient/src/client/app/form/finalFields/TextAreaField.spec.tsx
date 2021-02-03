import React from 'react';
import { Form } from 'react-final-form';

import { mountWithIntl } from 'testHelpers/intl-enzyme-test-helper';
import TextAreaField from './TextAreaField';

const mountFieldInForm = (field: any, initialValues = {}) => mountWithIntl(
  <Form
    onSubmit={() => undefined}
    initialValues={initialValues}
    render={() => (
      <>
        {field}
      </>
    )}
  />,
);

describe('<TextAreaField>', () => {
  it('Skal rendre TextAreaField', () => {
    const wrapper = mountFieldInForm(<TextAreaField name="text" label="name" />);
    expect(wrapper.find('textarea')).toHaveLength(1);
  });

  it('Skal rendre TextAreaField som ren tekst hvis readonly', () => {
    const wrapper = mountFieldInForm(<TextAreaField name="text" label="name" readOnly />, { text: 'tekst' });
    expect(wrapper.find('textarea')).toHaveLength(0);
    expect(wrapper.find('div')).toHaveLength(1);
    expect(wrapper.find('Label')).toHaveLength(1);
    expect(wrapper.find('Label').prop('input')).toEqual('name');
    expect(wrapper.find('Normaltekst')).toHaveLength(1);
    expect(wrapper.find('Normaltekst').text()).toEqual('tekst');
  });
});
