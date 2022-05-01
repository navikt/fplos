import React from 'react';
import { MemoryRouter } from 'react-router-dom';
import { render, screen } from '@testing-library/react';

import Home from './Home';

describe('<MissingPage>', () => {
  it('skal vise laste-side når ingen rute er valgt', async () => {
    render(
      <MemoryRouter>
        <Home headerHeight={1} />
      </MemoryRouter>,
    );
    expect(await screen.findByText('Venter...')).toBeInTheDocument();
  });
});
