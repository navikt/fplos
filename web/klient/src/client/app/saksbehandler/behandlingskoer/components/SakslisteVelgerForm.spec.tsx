import React from 'react';
import { render, screen } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import * as stories from './SakslisteVelgerForm.stories';

const { Default } = composeStories(stories);

describe('<SakslisteVelgerForm>', () => {
  it('skal vise dropdown med en saksliste', async () => {
    const { getByText } = render(<Default />);

    expect(await screen.findByText('Utvalgskriterier')).toBeInTheDocument();

    expect(await screen.findByText('Saksliste 1')).toBeInTheDocument();
    expect(screen.queryByText('Saksliste 2')).not.toBeInTheDocument();

    expect((getByText('Saksliste 1') as HTMLOptionElement).selected).toBeTruthy();

    expect(screen.getByText('Stønadstype')).toBeInTheDocument();
    expect(screen.getByText('Foreldrepenger')).toBeInTheDocument();

    expect(screen.getByText('Behandlingstype')).toBeInTheDocument();
    expect(screen.getByText('Førstegangsbehandling')).toBeInTheDocument();
    expect(screen.getByText('Revurdering')).toBeInTheDocument();

    expect(screen.getByText('Andre filter')).toBeInTheDocument();
    expect(screen.getByText('Til beslutter')).toBeInTheDocument();

    expect(screen.getByText('Sortering')).toBeInTheDocument();
    expect(screen.getByText(/Behandlingsfrist/i)).toBeInTheDocument();
    expect(screen.getByText(/Gjeldende intervall:/i)).toBeInTheDocument();
  });
});
