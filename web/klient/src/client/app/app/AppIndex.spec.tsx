import React from 'react';
import { render, screen } from '@testing-library/react';
import { act } from 'react-dom/test-utils';
import { MemoryRouter } from 'react-router-dom';
import AppIndex from './AppIndex';

describe('<AppIndex>', () => {
  it('skal vise hjem-skjermbildet', async () => {
    await act(async () => {
      render(
        <MemoryRouter>
          <AppIndex />
        </MemoryRouter>,
      );
    });

    expect(await screen.findByText('Svangerskap, fødsel og adopsjon')).toBeInTheDocument();
  });

  it('skal vise hjem-skjermbildet med feilmelding som kommer via url', async () => {
    await act(async () => {
      render(
        <MemoryRouter initialEntries={['?errormessage=Det+finnes+ingen+sak+med+denne+referansen%3A+266']}>
          <AppIndex />
        </MemoryRouter>,
      );
    });

    expect(await screen.findByText('Det finnes ingen sak med denne referansen: 266')).toBeInTheDocument();
  });
});
