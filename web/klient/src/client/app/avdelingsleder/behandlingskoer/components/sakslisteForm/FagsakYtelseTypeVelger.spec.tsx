import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import userEvent from '@testing-library/user-event';
import * as stories from 'stories/avdelingsleder/behandlingskoer/FagsakYtelseTypeVelger.stories';

const { Default } = composeStories(stories);

describe('<FagsakYtelseTypeVelger>', () => {
  it('skal vise checkboxer for stønadstyper og så velge engangsstønad', async () => {
    const { getByLabelText } = render(<Default />);
    expect(await screen.findByText('Stønadstype')).toBeInTheDocument();
    expect(getByLabelText('Foreldrepenger')).toBeChecked();
    expect(getByLabelText('Engangsstønad')).not.toBeChecked();

    userEvent.click(screen.getByText('Engangsstønad'));

    await waitFor(() => expect(getByLabelText('Engangsstønad')).toBeChecked());
    expect(getByLabelText('Foreldrepenger')).not.toBeChecked();
  });
});
