import React from 'react';
import { render, screen } from '@testing-library/react';
import { composeStories } from '@storybook/testing-react';
import * as stories from './Modal.stories';

const { Default } = composeStories(stories);

describe('<Modal>', () => {
  it('skal rendre modal', async () => {
    render(<Default />);

    expect(await screen.findByText('Lukk')).toBeInTheDocument();
  });
});
