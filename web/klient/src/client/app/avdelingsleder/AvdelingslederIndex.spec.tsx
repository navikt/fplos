import React from 'react';
import { render, screen } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import userEvent from '@testing-library/user-event';
import * as stories from 'stories/avdelingsleder/AvdelingslederIndex.stories';

const { Default, LasteIkonFørValgtAvdelingErSatt, HarIkkeTilgang } = composeStories(stories);

// TODO Dette skal fjernast når ein har fått erstatta react-vis
// eslint-disable-next-line no-console
const originalWarn = console.warn.bind(console.warn);
beforeAll(() => {
  // eslint-disable-next-line no-console
  console.warn = (msg) => !msg.toString().includes('componentWillReceiveProps') && originalWarn(msg);
});
afterAll(() => {
  // eslint-disable-next-line no-console
  console.warn = originalWarn;
});

describe('<AvdelingslederIndex>', () => {
  it('skal vise lasteikon før valgt avdeling er satt', async () => {
    render(<LasteIkonFørValgtAvdelingErSatt />);
    expect(await screen.findByText('Venter...')).toBeInTheDocument();
  });

  it('skal vise avdelingsleder dashboard etter at valgt avdeling er satt', async () => {
    render(<Default />);
    expect(await screen.findByText('Gjeldende behandlingskøer')).toBeInTheDocument();
    expect(screen.getByText('Nøkkeltall')).toBeInTheDocument();
    expect(screen.getByText('Saksbehandlere')).toBeInTheDocument();
    expect(screen.getByText('Reservasjoner')).toBeInTheDocument();
    expect(screen.getByText('Gjeldende behandlingskøer')).toBeInTheDocument();
  });

  it('skal velge de ulike panelene', async () => {
    render(<Default />);
    expect(await screen.findByText('Gjeldende behandlingskøer')).toBeInTheDocument();
    expect(screen.getByText('Nøkkeltall')).toBeInTheDocument();

    userEvent.click(screen.getByText('Nøkkeltall'));

    expect(await screen.findByText('Antall til behandling')).toBeInTheDocument();

    userEvent.click(screen.getByText('Saksbehandlere'));

    expect(await screen.findByText('Tilgjengelige saksbehandlere')).toBeInTheDocument();

    userEvent.click(screen.getByText('Reservasjoner'));

    expect(await screen.findByText('Reservasjoner for avdelingen')).toBeInTheDocument();
  });

  it('skal vise at en ikke har tilgang til avdelingsleder-siden', async () => {
    render(<HarIkkeTilgang />);
    expect(await screen.findByText('Du har ikke tilgang til å bruke dette programmet')).toBeInTheDocument();
  });
});
