import React from 'react';

import MissingPage from 'app/components/MissingPage';

import withIntl from '../decorators/withIntl';
import withReduxAndRouter from '../decorators/withReduxAndRouter';

export default {
  title: 'MissingPage',
  component: MissingPage,
  decorators: [withIntl, withReduxAndRouter],
};

export const skalViseSideKanIkkeFinnes = () => (
  <MissingPage />
);
