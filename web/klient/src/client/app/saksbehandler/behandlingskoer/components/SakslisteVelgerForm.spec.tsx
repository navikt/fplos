import React from 'react';
import { render, screen } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import * as stories from 'stories/saksbehandler/behandlingskoer/SakslisteVelgerForm.stories';

const { Default } = composeStories(stories);

describe('<SakslisteVelgerForm>', () => {
  it('skal vise dropdown med en saksliste', async () => {
    render(<Default />);

    expect(await screen.findByText('Utvalgskriterier')).toBeInTheDocument();
  });
});
