import React from 'react';

import DateLabel from 'sharedComponents/DateLabel';
import withIntl from '../../decorators/withIntl';

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
