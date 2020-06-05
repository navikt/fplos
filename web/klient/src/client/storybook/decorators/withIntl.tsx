import React from 'react';
import { createIntl, createIntlCache, RawIntlProvider } from 'react-intl';

import data from '../../app/sprak/nb_NO.json';

const intl = createIntl({
  locale: 'nb-NO',
  messages: data,
}, createIntlCache());

const withIntlProvider = (story) => (
  <RawIntlProvider value={intl}>
    { story(intl) }
  </RawIntlProvider>
);

export default withIntlProvider;
