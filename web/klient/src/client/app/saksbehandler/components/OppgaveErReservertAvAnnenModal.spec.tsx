import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import userEvent from '@testing-library/user-event';
import * as stories from 'stories/saksbehandler/OppgaveErReservertAvAnnenModal.stories';

const { Default } = composeStories(stories);

describe('<OppgaveErReservertAvAnnenModal>', () => {
  it('skal modal og lukke den ved trykk på Ok-knappen', async () => {
    const lukkErReservertModalOgOpneOppgave = jest.fn();
    render(<Default lukkErReservertModalOgOpneOppgave={lukkErReservertModalOgOpneOppgave} />);
    expect(await screen.findByText(
      'Espen Utvikler (E232323) arbeider nå med denne behandlingen (reservert fram t.o.m 01.01.2020 - 00:00)',
    )).toBeInTheDocument();

    userEvent.click(screen.getByText('OK'));

    await waitFor(() => expect(lukkErReservertModalOgOpneOppgave).toHaveBeenCalledTimes(1));
    expect(lukkErReservertModalOgOpneOppgave).toHaveBeenNthCalledWith(1, stories.oppgaveForResevertAvAnnenModal);
  });
});
