import React from 'react';
import { configure, addParameters, addDecorator } from '@storybook/react';
import { themes } from '@storybook/theming';

import '../src/client/styles/global.less';

const withGlobalStyle = (story) => (
  <div style={{ margin: '40px'}}>
    { story() }
  </div>
);
addDecorator(withGlobalStyle);

addParameters({
  options: {
    theme: themes.dark,
  },
});

configure(require.context('../src/client/storybook/stories/', true, /\.stories\.tsx$/), module);
