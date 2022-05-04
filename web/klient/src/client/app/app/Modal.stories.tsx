import React from 'react';

import Modal from './Modal';

export default {
  title: 'sharedComponents/Modal',
  component: Modal,
};

export const Default = () => (
  <Modal
    contentLabel="Dette er en advarsel"
    isOpen
    closeButton
    onRequestClose={() => undefined}
  />
);
