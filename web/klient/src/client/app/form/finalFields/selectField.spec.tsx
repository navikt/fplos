import React from 'react';
import { Form } from 'react-final-form';

import { mountWithIntl } from 'testHelpers/intl-enzyme-test-helper';
import SelectField from './SelectField';

const selectValues = [
  <option value="true" key="option1">Ja</option>,
  <option value="false" key="option2">Nei</option>,
];

const mountFieldInForm = (field: any) => mountWithIntl(
  <Form
    onSubmit={() => undefined}
    render={() => (
      <>
        {field}
      </>
    )}
  />,
);

describe('<SelectField>', () => {
  it('Skal rendre select', () => {
    const wrapper = mountFieldInForm(<SelectField label="text" name="text" selectValues={selectValues} />);
    expect(wrapper.find('label').text()).toEqual('text');
    const select = wrapper.find('select');
    expect(select).toHaveLength(1);
    expect(select.find('option')).toHaveLength(3);
    expect(select.find('option').first().prop('value')).toEqual('');
    expect(select.find('option').first().text()).toEqual(' ');
  });

  it('Skal rendre disabled select', () => {
    const wrapper = mountFieldInForm(<SelectField label="text" name="text" disabled selectValues={selectValues} />);
    expect(wrapper.find('label').text()).toEqual('text');
    const select = wrapper.find('select');
    expect(select).toHaveLength(1);
    expect(select.prop('disabled')).toBe(true);
  });
});
