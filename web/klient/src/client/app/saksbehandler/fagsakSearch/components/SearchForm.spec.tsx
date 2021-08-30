import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { composeStories } from '@storybook/testing-react';
import * as stories from 'stories/saksbehandler/fagsakSearch/SearchForm.stories';

const { Søkeskjema } = composeStories(stories);

describe('<SearchForm>', () => {
  // TODO Fiks test
  it.skip('skal skrive inn ugyldig fødselsnummer og vise feilmelding', async () => {
    const onSubmitMock = jest.fn();
    const utils = render(<Søkeskjema onSubmit={onSubmitMock} />);

    expect(await screen.findByText('Søk på sak eller person')).toBeInTheDocument();
    expect(screen.getByText('Reserver behandlingen ved søk')).toBeInTheDocument();
    expect(screen.queryByText('Ugyldig saksnummer eller fødselsnummer')).not.toBeInTheDocument();

    expect(screen.getByText('Saksnummer eller fødselsnummer/D-nummer')).toBeInTheDocument();

    const saksnrEllerFødselsnrInput = utils.getByLabelText('Saksnummer eller fødselsnummer/D-nummer');
    userEvent.type(saksnrEllerFødselsnrInput, 'Dette er ikke et gyldig nr');

    userEvent.click(screen.getAllByRole('button')[0]);

    expect(await screen.findByText('Ugyldig saksnummer eller fødselsnummer')).toBeInTheDocument();

    expect(onSubmitMock).toHaveBeenCalledTimes(0);
  });

  it('skal skrive inn gyldig fødselsnummer og sende inn resultat', async () => {
    const onSubmitMock = jest.fn();
    const utils = render(<Søkeskjema onSubmit={onSubmitMock} />);

    expect(await screen.findByText('Søk på sak eller person')).toBeInTheDocument();
    expect(screen.getByText('Reserver behandlingen ved søk')).toBeInTheDocument();
    expect(screen.queryByText('Ugyldig saksnummer eller fødselsnummer')).not.toBeInTheDocument();

    expect(screen.getByText('Saksnummer eller fødselsnummer/D-nummer')).toBeInTheDocument();

    const saksnrEllerFødselsnrInput = utils.getByLabelText('Saksnummer eller fødselsnummer/D-nummer');
    userEvent.type(saksnrEllerFødselsnrInput, '07078518434');

    userEvent.click(screen.getAllByRole('button')[0]);

    await waitFor(() => expect(screen.queryByText('Ugyldig saksnummer eller fødselsnummer')).not.toBeInTheDocument());

    expect(onSubmitMock).toHaveBeenCalledTimes(1);
    expect(onSubmitMock).toHaveBeenNthCalledWith(1, {
      searchString: '07078518434',
      skalReservere: undefined,
    });
  });

  it('skal reservere behandling ved søk', async () => {
    const onSubmitMock = jest.fn();
    const utils = render(<Søkeskjema onSubmit={onSubmitMock} />);

    expect(await screen.findByText('Søk på sak eller person')).toBeInTheDocument();

    userEvent.click(screen.getByText('Reserver behandlingen ved søk'));

    expect(await screen.findByText('Saksnummer eller fødselsnummer/D-nummer')).toBeInTheDocument();
    const saksnrEllerFødselsnrInput = utils.getByLabelText('Saksnummer eller fødselsnummer/D-nummer');
    userEvent.type(saksnrEllerFødselsnrInput, '07078518434');

    userEvent.click(screen.getAllByRole('button')[0]);

    await waitFor(() => expect(screen.queryByText('Ugyldig saksnummer eller fødselsnummer')).not.toBeInTheDocument());

    expect(onSubmitMock).toHaveBeenCalledTimes(1);
    expect(onSubmitMock).toHaveBeenNthCalledWith(1, {
      searchString: '07078518434',
      skalReservere: true,
    });
  });
});
