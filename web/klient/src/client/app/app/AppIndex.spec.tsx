import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import AppIndex from './AppIndex';

describe('<AppIndex>', () => {
  it('skal vise hjem-skjermbildet', async () => {
    render(
      <MemoryRouter>
        <AppIndex />
      </MemoryRouter>,
    );

    expect(await screen.findByText('Svangerskap, fødsel og adopsjon')).toBeInTheDocument();
  });

  it('skal vise hjem-skjermbildet med feilmelding som kommer via url', async () => {
    render(
      <MemoryRouter initialEntries={['?errormessage=Det+finnes+ingen+sak+med+denne+referansen%3A+266']}>
        <AppIndex />
      </MemoryRouter>,
    );

    expect(await screen.findByText('Det finnes ingen sak med denne referansen: 266')).toBeInTheDocument();
  });
});
