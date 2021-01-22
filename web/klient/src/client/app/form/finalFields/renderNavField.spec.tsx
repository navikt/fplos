import React from 'react';
import sinon from 'sinon';

import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import renderNavField from './renderNavField';

const metaMock = {};

const getInputMock = (input) => ({
  name: 'mockInput',
  onBlur: sinon.spy(),
  onChange: sinon.spy(),
  onDragStart: sinon.spy(),
  onDrop: sinon.spy(),
  onFocus: sinon.spy(),
  ...input,
});

const MockField = () => <div />;
const RenderedMockField = renderNavField(MockField).WrappedComponent;

describe('renderNavField', () => {
  it('skal ikke vise feil i utgangspunktet', () => {
    const meta = { ...metaMock, submitFailed: false, error: [{ id: 'ValidationMessage.NotEmpty' }] };

    const wrapper = shallowWithIntl(<RenderedMockField input={getInputMock({})} meta={meta} intl={intlMock} />);
    const mockField = wrapper.find(MockField);

    expect(mockField).toHaveLength(1);
    const props = mockField.at(0).props() as { feil: string };
    expect(props.feil).toBeUndefined();
  });

  it('skal vise feil hvis submit har feilet', () => {
    const meta = { ...metaMock, submitFailed: true, error: [{ id: 'ValidationMessage.NotEmpty' }] };

    const wrapper = shallowWithIntl(<RenderedMockField input={getInputMock({})} meta={meta} intl={intlMock} />);
    const mockField = wrapper.find(MockField);

    expect(mockField).toHaveLength(1);
    const props = mockField.at(0).props() as { feil: string };
    expect(props.feil).toEqual('Feltet må fylles ut');
  });
});
