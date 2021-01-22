import React from 'react';
import '../src/client/styles/global.less';
import { switchOnTestMode } from 'data/rest-api';

export const decorators = [(Story) => <div style={{ margin: '40px'}}><Story/></div>];

switchOnTestMode();

