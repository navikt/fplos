import React from 'react';
import { render, screen } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import * as stories from 'stories/saksbehandler/saksstotte/SistBehandledeSaker.stories';

const { Default, IngenBehandlinger } = composeStories(stories);

describe('<SistBehandledeSaker>', () => {
  it('skal vise sist behandlede saker', async () => {
    render(<Default />);
    expect(await screen.findByText('Siste behandlinger')).toBeInTheDocument();
    expect(screen.getByText('Espen Utvikler 334342323')).toBeInTheDocument();
  });

  it('skal vise ingen behandlinger', async () => {
    render(<IngenBehandlinger />);
    expect(await screen.findByText('Siste behandlinger')).toBeInTheDocument();
    expect(screen.getByText('Ingen behandlinger')).toBeInTheDocument();
  });
});
