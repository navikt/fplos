import React from 'react';

import MissingPage from 'app/components/MissingPage';

import withIntl from 'storybookUtils/decorators/withIntl';
import withRouter from 'storybookUtils/decorators/withRouter';

export default {
  title: 'MissingPage',
  component: MissingPage,
  decorators: [withIntl, withRouter],
};

export const Default = () => (
  <MissingPage />
);
