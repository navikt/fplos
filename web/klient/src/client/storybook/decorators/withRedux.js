import React from 'react';
import { Provider } from 'react-redux';
import { createBrowserHistory } from 'history';

import configureStore from '../../app/store';

const history = createBrowserHistory({
  basename: '/fpsak/',
});

const withReduxProvider = (story) => {
  const store = configureStore(history);
  return (
    <Provider store={store}>
      { story() }
    </Provider>
  );
};

export default withReduxProvider;
