import React from 'react';
import { render, screen } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import * as stories from 'stories/avdelingsleder/nokkeltall/OppgaverPerForsteStonadsdagPanel.stories';

const { Default } = composeStories(stories);

describe('<OppgaverPerForsteStonadsdagPanel>', () => {
  it.skip('skal rendre graf', async () => {
    render(<Default />);
    expect(await screen.findByText('Antall førstegangsbehandlinger fordelt på første stønadsdag')).toBeInTheDocument();
  });
});
