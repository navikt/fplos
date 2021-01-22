import React from 'react';
import { Form } from 'react-final-form';

import { mountWithIntl } from 'testHelpers/intl-enzyme-test-helper';
import InputField from './InputField';

const mountFieldInForm = (field, initialValues) => mountWithIntl(
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

describe('<InputField>', () => {
  it('Skal rendre input', () => {
    const wrapper = mountFieldInForm(<InputField label="text" name="text" type="text" />, { text: 'Jeg er Batman' });
    expect(wrapper.find('input')).toHaveLength(1);
    expect(wrapper.find('input').prop('value')).toEqual('Jeg er Batman');
    expect(wrapper.find('input').prop('type')).toEqual('text');
    expect(wrapper.find('label').text()).toEqual('text');
  });

  it('Skal rendre Readonly hvis den er satt til true', () => {
    const wrapper = mountFieldInForm(<InputField readOnly name="text" />, { text: 'Jeg er Batman' });
    expect(wrapper.find('Normaltekst')).toHaveLength(1);
    expect(wrapper.find('Normaltekst').text()).toEqual('Jeg er Batman');
  });
});
