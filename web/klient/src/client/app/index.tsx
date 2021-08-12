import React from 'react';
import { createBrowserHistory } from 'history';
import { render } from 'react-dom';
import { Router } from 'react-router-dom';
import { init } from '@sentry/browser';

import AppIndex from 'app/AppIndex';
import { RestApiProvider, RestApiErrorProvider } from 'data/rest-api-hooks';
import polyfill from './polyfill';

/* eslint no-undef: "error" */
const environment = window.location.hostname;

polyfill().then(() => {
  init({
    dsn: 'https://d863105541bf4d0cb030dd4c6bfb4d05@sentry.gc.nav.no/10',
    environment,
  });

  const history = createBrowserHistory<any>({
    basename: '/fplos/',
  });

  const renderFunc = () => {
    const app = document.getElementById('app');
    if (app === null) {
      throw new Error('No app element');
    }
    render(
      <Router history={history}>
        <RestApiProvider>
          <RestApiErrorProvider>
            <AppIndex />
          </RestApiErrorProvider>
        </RestApiProvider>
      </Router>,
      app,
    );
  };

  renderFunc();
});
