import React from 'react';
import { Story } from '@storybook/react';
import { action } from '@storybook/addon-actions';

import Oppgave from 'types/saksbehandler/oppgaveTsType';
import OppgaveErReservertAvAnnenModal from 'saksbehandler/components/OppgaveErReservertAvAnnenModal';
import behandlingStatus from 'kodeverk/behandlingStatus';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import withIntl from 'storybookUtils/decorators/withIntl';

export default {
  title: 'saksbehandler/OppgaveErReservertAvAnnenModal',
  component: OppgaveErReservertAvAnnenModal,
  decorators: [withIntl],
};

const oppgaveForResevertAvAnnenModal = {
  id: 1,
  status: {
    erReservert: false,
    flyttetReservasjon: {
      tidspunkt: '2019-02-02',
      uid: '23423',
      navn: 'Espen Utvikler',
      begrunnelse: 'Flyttet',
    },
  },
  saksnummer: 1234,
  personnummer: '1212',
  navn: 'Espen Utvikler',
  system: 'SAK',
  behandlingstype: behandlingType.FORSTEGANGSSOKNAD,
  behandlingStatus: behandlingStatus.BEHANDLING_UTREDES,
  opprettetTidspunkt: '2019-01-01',
  behandlingsfrist: '2019-01-01',
  fagsakYtelseType: fagsakYtelseType.FORELDREPRENGER,
  erTilSaksbehandling: true,
  behandlingId: '1',
  href: '',
};

const Template: Story<{ lukkErReservertModalOgOpneOppgave: (oppgave: Oppgave) => void; }> = ({
  lukkErReservertModalOgOpneOppgave,
}) => (
  <OppgaveErReservertAvAnnenModal
    lukkErReservertModalOgOpneOppgave={lukkErReservertModalOgOpneOppgave}
    oppgave={oppgaveForResevertAvAnnenModal}
    oppgaveStatus={{
      erReservert: false,
      reservertAvNavn: 'Espen Utvikler',
      reservertAvUid: 'E232323',
      reservertTilTidspunkt: '2020-01-01',
    }}
  />
);

export const Default = Template.bind({});
Default.args = {
  lukkErReservertModalOgOpneOppgave: action('button-click'),
};
