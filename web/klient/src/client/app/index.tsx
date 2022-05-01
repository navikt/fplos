import React from 'react';
import { render } from 'react-dom';
import { BrowserRouter } from 'react-router-dom';
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

  const renderFunc = () => {
    const app = document.getElementById('app');
    if (app === null) {
      throw new Error('No app element');
    }
    render(
      <BrowserRouter basename="/fplos/">
        <RestApiProvider>
          <RestApiErrorProvider>
            <AppIndex />
          </RestApiErrorProvider>
        </RestApiProvider>
      </BrowserRouter>,
      app,
    );
  };

  renderFunc();
});
