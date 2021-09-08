import React, { ChangeEvent, useState } from 'react';

import Datepicker from 'sharedComponents/datepicker/Datepicker';
import withIntl from 'storybookUtils/decorators/withIntl';

export default {
  title: 'sharedComponents/Datepicker',
  component: Datepicker,
  decorators: [withIntl],
};

export const Default = () => {
  const [value, setValue] = useState('');
  const setDate = (dato: string | ChangeEvent) => {
    if (typeof dato === 'string') {
      setValue(dato);
    }
  };
  return <Datepicker value={value} onChange={setDate} onBlur={() => undefined} />;
};
