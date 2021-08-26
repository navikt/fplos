import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import userEvent from '@testing-library/user-event';
import * as stories from 'stories/avdelingsleder/behandlingskoer/AndreKriterierVelger.stories';

const { Default } = composeStories(stories);

describe('<AndreKriterierVelger>', () => {
  it('skal vise checkboxer for andre kriterier der Til beslutter er valgt fra før', async () => {
    const { getByLabelText } = render(<Default />);
    expect(await screen.findByText('Til beslutter')).toBeInTheDocument();
    expect(getByLabelText('Til beslutter')).toBeChecked();
    expect(getByLabelText('Ta med i køen')).not.toBeChecked();
    expect(getByLabelText('Fjern fra køen')).toBeChecked();
  });

  xit('skal velge Registrer papirsøknad og fjerne dette fra køen', async () => {
    render(<Default />);
    expect(await screen.findByText('Registrer papirsøknad')).toBeInTheDocument();

    userEvent.click(screen.getByText('Registrer papirsøknad'));

    expect(screen.getAllByText('Ta med i køen')[1]).not.toBeChecked();
    expect(screen.getAllByText('Fjern fra køen')[1]).not.toBeChecked();

    userEvent.click(screen.getAllByText('Fjern fra køen')[1]);

    await waitFor(() => expect(screen.getAllByText('Fjern fra køen')[1]).toBeChecked());
    expect(screen.getAllByText('Ta med i køen')[1]).not.toBeChecked();
  });
});
