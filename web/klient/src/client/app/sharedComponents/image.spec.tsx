import React from 'react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { composeStories } from '@storybook/testing-react';
import * as stories from './Image.stories';

// @ts-ignore Ta vekk når fiksa
const { Default, KlikkbartIkon } = composeStories(stories);

describe('<Image>', () => {
  it('skal vise et ikon', async () => {
    render(<Default />);

    expect(await screen.findByRole('img')).toBeInTheDocument();
  });

  it('skal åpne en modal når en trykker på ikon', async () => {
    render(<KlikkbartIkon />);

    expect(await screen.findByRole('img')).toBeInTheDocument();

    userEvent.click(screen.getByRole('img'));

    expect(await screen.findByText('Lukk')).toBeInTheDocument();
  });
});
