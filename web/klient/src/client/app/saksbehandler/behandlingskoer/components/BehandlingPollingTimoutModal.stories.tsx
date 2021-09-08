import React from 'react';

import BehandlingPollingTimoutModal from 'saksbehandler/behandlingskoer/components/BehandlingPollingTimoutModal';

import withIntl from 'storybookUtils/decorators/withIntl';

export default {
  title: 'saksbehandler/behandlingskoer/BehandlingPollingTimoutModal',
  component: BehandlingPollingTimoutModal,
  decorators: [withIntl],
};

export const Default = () => (
  <BehandlingPollingTimoutModal />
);
