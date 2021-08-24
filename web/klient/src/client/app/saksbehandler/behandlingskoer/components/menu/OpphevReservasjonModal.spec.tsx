import React from 'react';
import { render, screen } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import userEvent from '@testing-library/user-event';
import * as stories from 'stories/saksbehandler/behandlingskoer/OpphevReservasjonModal.stories';

const { Default } = composeStories(stories);

describe('<OpphevReservasjonModal>', () => {
  it('skal vise modal for oppheving av reservasjon', async () => {
    const begrunnelse = 'Dette er en begrunnelse';

    const utils = render(<Default opphevData={{ oppgaveId: 1, begrunnelse }} />);

    expect(await screen.findByText('Når en reservert sak frigjøres er begrunnelse obligatorisk')).toBeInTheDocument();

    const begrunnelseInput = utils.getByLabelText('Når en reservert sak frigjøres er begrunnelse obligatorisk');
    userEvent.type(begrunnelseInput, begrunnelse);

    expect(await screen.findByText('OK')).toBeInTheDocument();
    userEvent.click(screen.getByText('OK'));
  });
});
