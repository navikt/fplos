import React from 'react';
import { expect } from 'chai';
import { shallow } from 'enzyme';
import Modal from './Modal';

const otherProps = {
  className: '',
  closeButton: true,
  isOpen: true,
  contentLabel: 'test',
  onRequestClose: () => undefined,
};

describe('<Modal>', () => {
  it('skal rendre modal med children', () => {
    const wrapper = shallow(
      <div id="app">
        <Modal
          {...otherProps}
          shouldCloseOnOverlayClick={false}
        >
          <div className="content">test</div>
        </Modal>
      </div>,
    );
    expect(wrapper.find('div.content')).to.have.length(1);
    expect(wrapper.find(Modal).prop('shouldCloseOnOverlayClick')).to.be.false;
  });
});
