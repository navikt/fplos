import React from 'react';
import { render, screen } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import * as stories from './MissingPage.stories';

const { Default } = composeStories(stories);

describe('<MissingPage>', () => {
  it('skal vise feilside med teksten Side finnes ikke', async () => {
    render(<Default />);

    expect(await screen.findByText('Side finnes ikke')).toBeInTheDocument();

    expect(screen.getByText('Hjem')).toBeInTheDocument();
  });
});
