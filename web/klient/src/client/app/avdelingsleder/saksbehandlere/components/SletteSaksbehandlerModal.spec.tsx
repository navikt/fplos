import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import userEvent from '@testing-library/user-event';
import * as stories from 'stories/avdelingsleder/saksbehandlere/SletteSaksbehandlerModal.stories';

const { Default } = composeStories(stories);

describe('<SletteSaksbehandlerModal>', () => {
  it('skal vise modal og slette ved å trykk ja', async () => {
    const fjernSaksbehandler = jest.fn();
    render(<Default fjernSaksbehandler={fjernSaksbehandler} />);
    expect(await screen.findByText('Ønsker du å slette Espen Utvikler?')).toBeInTheDocument();

    userEvent.click(screen.getByText('Ja'));

    await waitFor(() => expect(fjernSaksbehandler).toHaveBeenCalledTimes(1));
  });
});
