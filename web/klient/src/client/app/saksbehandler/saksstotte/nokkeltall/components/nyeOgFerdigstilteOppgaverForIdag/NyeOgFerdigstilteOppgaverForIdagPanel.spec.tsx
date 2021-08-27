import React from 'react';
import { render, screen } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import * as stories from 'stories/saksbehandler/saksstotte/NyeOgFerdigstilteOppgaverForIdagPanel.stories';

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

describe('<NyeOgFerdigstilteOppgaverForIdagPanel>', () => {
  it('skal rendre graf', async () => {
    render(<Default />);
    expect(await screen.findByText('Nye og ferdigstilte behandlinger')).toBeInTheDocument();
  });
});
