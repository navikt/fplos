import React from 'react';
import { render, screen } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import * as stories from 'stories/saksbehandler/saksstotte/NyeOgFerdigstilteOppgaverForIdagPanel.stories';

const { Default } = composeStories(stories);

describe('<NyeOgFerdigstilteOppgaverForIdagPanel>', () => {
  it.skip('skal rendre graf', async () => {
    render(<Default />);
    expect(await screen.findByText('Nye og ferdigstilte behandlinger')).toBeInTheDocument();
  });
});
