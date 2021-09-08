import React from 'react';
import { Router } from 'react-router-dom';
import { createBrowserHistory } from 'history';

const history = createBrowserHistory<any>({
  basename: '/',
});

const withRouterProvider = (story: any) => (
  <Router history={history}>
    { story() }
  </Router>
);

export default withRouterProvider;
