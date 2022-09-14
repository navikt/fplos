import React from 'react';

import Modal from './Modal';

export default {
  title: 'sharedComponents/Modal',
  component: Modal,
};

export const Default = () => (
  <Modal
    aria-label="Dette er en advarsel"
    open
    closeButton
    onClose={() => undefined}
  >
    <div>test</div>
  </Modal>
);
