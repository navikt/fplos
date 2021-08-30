import React from 'react';
import { render, screen } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import * as stories from 'stories/avdelingsleder/nokkeltall/OppgaverSomErApneEllerPaVentPanel.stories';

const { Default } = composeStories(stories);

// TODO Dette skal fjernast når ein har fått erstatta react-vis
// eslint-disable-next-line no-console
const originalWarn = console.warn.bind(console.warn);
beforeAll(() => {
  // eslint-disable-next-line no-console
  console.warn = (msg) => !msg.toString().includes('componentWillReceiveProps') && originalWarn(msg);
});
afterAll(() => {
  // eslint-disable-next-line no-console
  console.warn = originalWarn;
});

describe('<OppgaverSomErApneEllerPaVentPanel>', () => {
  it('skal vise graffilter', async () => {
    const { getByLabelText } = render(<Default />);
    expect(await screen.findByText('Status - åpne behandlinger')).toBeInTheDocument();

    expect(getByLabelText('Førstegangsbehandling')).toBeChecked();
    expect(getByLabelText('Klage')).toBeChecked();
    expect(getByLabelText('Revurdering')).toBeChecked();
    expect(getByLabelText('Innsyn')).toBeChecked();
    expect(getByLabelText('Anke')).toBeChecked();
  });
});
