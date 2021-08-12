import React from 'react';
import '@formatjs/intl-datetimeformat/polyfill-force';
import '@formatjs/intl-datetimeformat/locale-data/nb';
import '@formatjs/intl-numberformat/polyfill-force';
import '@formatjs/intl-numberformat/locale-data/nb';
import '../src/client/styles/global.less';

import { switchOnTestMode } from 'data/rest-api';

export const decorators = [(Story) => <div style={{ margin: '40px'}}><Story/></div>];

switchOnTestMode();

