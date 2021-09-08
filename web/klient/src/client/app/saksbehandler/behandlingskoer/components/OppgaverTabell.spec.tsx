import React from 'react';
import { render, screen } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import * as stories from './OppgaverTabell.stories';

const { Default, TomOppgaveTabell } = composeStories(stories);

describe('<OppgaverTabell>', () => {
  it('skal vise tabell med behandlinger', async () => {
    render(<Default />);

    expect(await screen.findByText('Neste behandlinger (0 i køen)')).toBeInTheDocument();
    expect(screen.getByText('Espen Utvikler 1212')).toBeInTheDocument();
    expect(screen.getByText('Førstegangssøknad')).toBeInTheDocument();
  });

  it('skal vise tom tabell når det ikke er behandlinger for køen', async () => {
    render(<TomOppgaveTabell />);
    expect(await screen.findByText('Det er ingen behandlinger i denne køen')).toBeInTheDocument();
  });
});
