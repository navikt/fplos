import React from 'react';

import withIntl from 'storybookUtils/decorators/withIntl';
import DateTimeLabel from './DateTimeLabel';

export default {
  title: 'sharedComponents/DateTimeLabel',
  component: DateTimeLabel,
  decorators: [withIntl],
};

export const Default = () => (
  <DateTimeLabel
    dateTimeString="2017-08-02T00:54:25.455"
  />
);

export const NyttFormat = () => (
  <DateTimeLabel
    dateTimeString="2017-08-02T00:54:25.455"
    useNewFormat
  />
);
