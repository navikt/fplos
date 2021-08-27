import React from 'react';
import { render, screen } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import * as stories from 'stories/avdelingsleder/nokkeltall/ManueltPaVentPanel.stories';

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

describe('<ManueltPaVentPanel>', () => {
  it('skal vise graffilter', async () => {
    const { getByLabelText } = render(<Default />);
    expect(await screen.findByText('Antall behandlinger satt på vent manuelt')).toBeInTheDocument();

    expect((screen.getByText('4 uker frem') as HTMLOptionElement).selected).toBeTruthy();
    expect((screen.getByText('8 uker frem') as HTMLOptionElement).selected).toBeFalsy();

    expect(getByLabelText('Foreldrepenger')).not.toBeChecked();
    expect(getByLabelText('Engangsstønad')).not.toBeChecked();
    expect(getByLabelText('Svangerskapspenger')).not.toBeChecked();
    expect(getByLabelText('Alle')).toBeChecked();
  });
});
