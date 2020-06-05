import React from 'react';
import { Provider } from 'react-redux';
import { ConnectedRouter } from 'connected-react-router';
import { createBrowserHistory } from 'history';

import configureStore from '../../app/store';

const history = createBrowserHistory({
  basename: '/fplos/',
});

const withReduxAndRouterProvider = (story) => {
  const store = configureStore(history);

  return (
    <Provider store={store}>
      <ConnectedRouter history={history}>
        { story() }
      </ConnectedRouter>
    </Provider>
  );
};

export default withReduxAndRouterProvider;
