import React from 'react';
import { render, screen } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import * as stories from 'stories/avdelingsleder/nokkeltall/FordelingAvBehandlingstypePanel.stories';

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

describe('<FordelingAvBehandlingstypePanel>', () => {
  it('skal vise graffilter', async () => {
    const { getByLabelText } = render(<Default />);
    expect(await screen.findByText('Antall åpne behandlinger')).toBeInTheDocument();

    expect(getByLabelText('Foreldrepenger')).not.toBeChecked();
    expect(getByLabelText('Engangsstønad')).not.toBeChecked();
    expect(getByLabelText('Svangerskapspenger')).not.toBeChecked();
    expect(getByLabelText('Alle')).toBeChecked();
  });
});
