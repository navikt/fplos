import React from 'react';

import withIntl from 'storybookUtils/decorators/withIntl';
import DateLabel from './DateLabel';

export default {
  title: 'sharedComponents/DateLabel',
  component: DateLabel,
  decorators: [withIntl],
};

export const Default = () => (
  <DateLabel
    dateString="2017-10-02"
  />
);
