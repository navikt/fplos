import React from 'react';
import ReactModal from 'react-modal';

import BehandlingPollingTimoutModal from 'saksbehandler/behandlingskoer/components/BehandlingPollingTimoutModal';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/behandlingskoer/BehandlingPollingTimoutModal',
  component: BehandlingPollingTimoutModal,
  decorators: [withIntl],
};

ReactModal.setAppElement = () => null;

export const skalViseTimeoutModal = () => (
  <BehandlingPollingTimoutModal />
);
