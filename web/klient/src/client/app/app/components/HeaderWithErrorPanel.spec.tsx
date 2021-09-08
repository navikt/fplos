import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { composeStories } from '@storybook/testing-react';
import * as stories from './HeaderWithErrorPanel.stories';

const {
  HeaderUtenAvdelingsvelger, HeaderMedAvdelingsvelger, HeaderMedKunEnFeilmelding, HeaderMedMerEnnFemFeilmeldinger, HeaderMedDriftsmeldinger,
} = composeStories(stories);

describe('<HeaderWithErrorPanel>', () => {
  it('skal vise header med tittel og innlogget bruker men uten avdelingsvelger', async () => {
    render(<HeaderUtenAvdelingsvelger />);

    expect(await screen.findByText('Svangerskap, fødsel og adopsjon')).toBeInTheDocument();
    expect(screen.getByText('Espen Utvikler')).toBeInTheDocument();

    expect(await screen.findByText('Rettskildene')).toBeInTheDocument();
    expect(screen.getByText('Systemrutine')).toBeInTheDocument();
  });

  it('skal vise header med tittel, innlogget bruker og avdelingsvelger', async () => {
    render(<HeaderMedAvdelingsvelger />);

    expect(await screen.findByText('Svangerskap, fødsel og adopsjon')).toBeInTheDocument();
    expect(screen.getByText('Espen Utvikler')).toBeInTheDocument();

    expect(await screen.findAllByText('VIK NAV Viken')).toHaveLength(2);
    expect(screen.getByText('OSL NAV Oslo')).toBeInTheDocument();
  });

  it('skal vise en feilmelding og så lukke rød rød ramme ved å trykke på lukk-knappen', async () => {
    render(<HeaderMedKunEnFeilmelding />);

    expect(await screen.findByText('Svangerskap, fødsel og adopsjon')).toBeInTheDocument();
    expect(screen.getByText('Dette er en feilmelding')).toBeInTheDocument();

    userEvent.click(screen.getAllByRole('button')[1]);

    await waitFor(() => expect(screen.queryByText('Dette er en feilmelding')).not.toBeInTheDocument());
  });

  it('skal vise mange feilmelding av forskjellig type', async () => {
    render(<HeaderMedMerEnnFemFeilmeldinger />);

    expect(await screen.findByText('Svangerskap, fødsel og adopsjon')).toBeInTheDocument();
    expect(screen.getByText('Dette er ein feil')).toBeInTheDocument();
    expect(screen.getByText('Rest-kallet feilet')).toBeInTheDocument();
    expect(screen.getByText('Serverkall har gått ut på tid: www.los.no')).toBeInTheDocument();
    expect(screen.getByText('Rest-kallet feilet 2')).toBeInTheDocument();
  });

  it('skal vise to driftsmeldinger', async () => {
    render(<HeaderMedDriftsmeldinger />);

    expect(await screen.findByText('Svangerskap, fødsel og adopsjon')).toBeInTheDocument();
    expect(screen.getByText('Dette er driftsmelding 1')).toBeInTheDocument();
    expect(screen.getByText('Dette er driftsmelding 2')).toBeInTheDocument();
  });
});
