import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { composeStories } from '@storybook/testing-react';
import * as stories from 'stories/avdelingsleder/behandlingskoer/UtvalgskriterierForSakslisteForm.stories';

const { MedGittNavn, MedDefaultNavn } = composeStories(stories);

describe('<UtvalgskriterierForSakslisteForm>', () => {
  it('skal vise sakslistenavn som saksbehandler har skrive inn', async () => {
    render(<MedGittNavn />);
    expect(await screen.findByText('Navn')).toBeInTheDocument();
    expect(screen.getByText('Saksliste 1')).toBeInTheDocument();
  });

  it('skal vise default sakslistenavn', async () => {
    render(<MedDefaultNavn />);
    expect(await screen.findByText('Navn')).toBeInTheDocument();
    expect(screen.getByText('Saksliste 1')).toBeInTheDocument();
  });

  it('skal vise feilmelding når en fjerner verdi fra navn-feltet', async () => {
    const { getByLabelText } = render(<MedDefaultNavn />);
    expect(await screen.findByText('Navn')).toBeInTheDocument();

    const navnInput = getByLabelText('Navn');
    userEvent.type(navnInput, '');
    fireEvent.blur(navnInput);

    expect(await screen.findByText('Feltet må fylles ut')).toBeInTheDocument();
  });
});
