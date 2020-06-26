import React from 'react';
import { Router } from 'react-router-dom';
import { createBrowserHistory } from 'history';

const history = createBrowserHistory({
  basename: '/fplos/',
});

const withRouterProvider = (story) => (
  <Router history={history}>
    { story() }
  </Router>
);

export default withRouterProvider;
