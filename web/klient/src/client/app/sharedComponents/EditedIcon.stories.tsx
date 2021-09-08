import React from 'react';

import withIntl from 'storybookUtils/decorators/withIntl';
import EditedIcon from './EditedIcon';

export default {
  title: 'sharedComponents/EditedIcon',
  component: EditedIcon,
  decorators: [withIntl],
};

export const Default = () => (
  <div>
    Dette er en tekst
    <EditedIcon />
  </div>
);
