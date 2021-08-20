import React from 'react';
import { Router } from 'react-router-dom';
import { createBrowserHistory } from 'history';
import { render, screen } from '@testing-library/react';

import Home from './Home';

const history = createBrowserHistory<any>({
  basename: '/',
});

describe('<MissingPage>', () => {
  it('skal vise laste-side når ingen rute er valgt', async () => {
    render(
      <Router history={history}>
        <Home headerHeight={1} />
      </Router>,
    );
    expect(await screen.findByText('Venter...')).toBeInTheDocument();
  });
});
