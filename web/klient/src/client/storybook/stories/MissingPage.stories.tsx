import React from 'react';

import MissingPage from 'app/components/MissingPage';

import withIntl from '../decorators/withIntl';
import withRouter from '../decorators/withRouter';

export default {
  title: 'MissingPage',
  component: MissingPage,
  decorators: [withIntl, withRouter],
};

export const Default = () => (
  <MissingPage />
);
