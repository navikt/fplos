import React from 'react';

import EditedIcon from 'sharedComponents/EditedIcon';
import withIntl from '../../decorators/withIntl';

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
