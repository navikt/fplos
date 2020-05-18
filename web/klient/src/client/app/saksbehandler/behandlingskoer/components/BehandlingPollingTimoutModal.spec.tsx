import React from 'react';
import { expect } from 'chai';
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
    expect(modal).to.have.length(1);
    expect(modal.prop('isOpen')).is.true;
    expect(modal.prop('contentLabel')).is.eql('Din økt er gått ut på tid, trykk Fortsett');

    const image = wrapper.find(Image);
    expect(image).to.have.length(1);
    expect(image.prop('alt')).is.eql('Din økt er gått ut på tid, trykk Fortsett');

    const message = wrapper.find(FormattedMessage);
    expect(message).to.have.length(1);
    expect(message.prop('id')).is.eql('BehandlingPollingTimoutModal.TimeoutMelding');
  });
});
