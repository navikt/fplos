import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { composeStories } from '@storybook/testing-react';
import * as stories from './UtvalgskriterierForSakslisteForm.stories';

const { MedGittNavn, MedDefaultNavn } = composeStories(stories);

describe('<UtvalgskriterierForSakslisteForm>', () => {
  it('skal vise sakslistenavn som saksbehandler har skrive inn', async () => {
    const { getByLabelText } = render(<MedGittNavn />);
    expect(await screen.findByText('Navn')).toBeInTheDocument();
    expect(getByLabelText('Navn')).toHaveValue('liste');
  });

  // TODO Fiks - Request feilar
  it.skip('skal vise default sakslistenavn', async () => {
    const { getByLabelText } = render(<MedDefaultNavn />);
    expect(await screen.findByText('Navn')).toBeInTheDocument();
    expect(getByLabelText('Navn')).toHaveValue('Ny behandlingskø');
  });

  // TODO Fiks - Request feilar
  it.skip('skal vise feilmelding når en fjerner nok tegn til at navnet blir færre enn 3 tegn langt', async () => {
    const { getByLabelText } = render(<MedGittNavn />);

    expect(await screen.findByText('Navn')).toBeInTheDocument();

    const navnInput = getByLabelText('Navn');
    await userEvent.type(navnInput, '{Backspace}{Backspace}{Backspace}');
    fireEvent.blur(navnInput);

    expect(await screen.findByText('Du må skrive minst 3 tegn')).toBeInTheDocument();
  });
});
