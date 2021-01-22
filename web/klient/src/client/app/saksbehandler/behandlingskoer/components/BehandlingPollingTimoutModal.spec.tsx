import React from 'react';
import { FormattedMessage, IntlShape } from 'react-intl';

import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import Modal from 'sharedComponents/Modal';
import Image from 'sharedComponents/Image';

import BehandlingPollingTimoutModal from './BehandlingPollingTimoutModal';

describe('<BehandlingPollingTimoutModal>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };
  it('skal rendre modal', () => {
    const wrapper = shallowWithIntl(
      <BehandlingPollingTimoutModal.WrappedComponent
        intl={intl as IntlShape}
      />,
    );

    const modal = wrapper.find(Modal);
    expect(modal).toHaveLength(1);
    expect(modal.prop('isOpen')).toBe(true);
    expect(modal.prop('contentLabel')).toEqual('Din økt er gått ut på tid, trykk Fortsett');

    const image = wrapper.find(Image);
    expect(image).toHaveLength(1);
    expect(image.prop('alt')).toEqual('Din økt er gått ut på tid, trykk Fortsett');

    const message = wrapper.find(FormattedMessage);
    expect(message).toHaveLength(1);
    expect(message.prop('id')).toEqual('BehandlingPollingTimoutModal.TimeoutMelding');
  });
});
