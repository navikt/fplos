import React from 'react';
import sinon from 'sinon';

import { mountWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import { RenderCheckboxField } from './CheckboxField';

const getInputMock = (input: any) => ({
  name: 'mockInput',
  onBlur: sinon.spy(),
  onChange: sinon.spy(),
  onDragStart: sinon.spy(),
  onDrop: sinon.spy(),
  onFocus: sinon.spy(),
  ...input,
});

describe('<CheckboxField>', () => {
  it('skal kalle onChange med boolsk verdi for checked', () => {
    const onChange = sinon.spy();
    const wrapper = mountWithIntl(
      <RenderCheckboxField
        intl={intlMock}
        input={getInputMock({ onChange })}
        meta={{}}
        label="field"
      />,
    );

    const checkbox = wrapper.find('input');

    checkbox.simulate('change', { target: { checked: true } });

    expect(onChange.called).toBe(true);
    const { args } = onChange.getCalls()[0];
    expect(args).toHaveLength(1);
    expect(args[0]).toBe(true);

    checkbox.simulate('change', { target: { checked: false } });

    const args2 = onChange.getCalls()[0].args;
    expect(args2).toHaveLength(1);
    expect(args2[0]).toBe(true);
  });

  it('skal initialisere checked med verdi fra input', () => {
    const wrapperTrue = mountWithIntl(
      <RenderCheckboxField
        intl={intlMock}
        input={getInputMock({ value: true })}
        meta={{}}
        label="field"
      />,
    );

    const checkboxTrue = wrapperTrue.find('input');
    expect(checkboxTrue.props().checked).toBe(true);

    const wrapperFalse = mountWithIntl(
      <RenderCheckboxField
        intl={intlMock}
        input={getInputMock({ value: false })}
        meta={{}}
        label="field"
      />,
    );

    const checkboxFalse = wrapperFalse.find('input');
    expect(checkboxFalse.props().checked).toBe(false);
  });
});
