import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiPathsKeys } from 'data/fplosRestApi';
import OppgaverTabell from 'saksbehandler/behandlingskoer/components/OppgaverTabell';
import behandlingStatus from 'kodeverk/behandlingStatus';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import Oppgave from 'types/saksbehandler/oppgaveTsType';

import withRestApiProvider from '../../../decorators/withRestApi';
import withIntl from '../../../decorators/withIntl';
import RestApiMock from '../../../utils/RestApiMock';

export default {
  title: 'saksbehandler/behandlingskoer/OppgaverTabell',
  component: OppgaverTabell,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{ oppgaverTilBehandling?: Oppgave[], reserverteOppgaver?: Oppgave[] }> = ({
  oppgaverTilBehandling,
  reserverteOppgaver,
}) => {
  const data = [
    { key: RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON.name, data: {} },
    { key: RestApiPathsKeys.RESERVERTE_OPPGAVER.name, data: reserverteOppgaver },
    { key: RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING.name, data: oppgaverTilBehandling },
  ];

  return (
    <RestApiMock data={data}>
      <OppgaverTabell
        reserverOppgave={action('button-click')}
        valgtSakslisteId={1}
        doPolling={false}
      />
    </RestApiMock>
  );
};

export const TomOppgaveTabell = Template.bind({});

export const TabellMedBådeLedigOgReservertOppgave = Template.bind({});
TabellMedBådeLedigOgReservertOppgave.args = {
  oppgaverTilBehandling: [{
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
    behandlingstype: {
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: 'Førstegangssøknad',
    },
    behandlingStatus: {
      kode: behandlingStatus.BEHANDLING_UTREDES,
      navn: 'Behandling utredes',
    },
    opprettetTidspunkt: '2019-01-01',
    behandlingsfrist: '2019-01-01',
    fagsakYtelseType: {
      kode: fagsakYtelseType.FORELDREPRENGER,
      navn: 'Foreldrepenger',
    },
    erTilSaksbehandling: true,
    behandlingId: '1',
    href: '',
  }],
  reserverteOppgaver: [{
    id: 2,
    status: {
      erReservert: true,
    },
    saksnummer: 1234,
    personnummer: '1212',
    navn: 'Espen Utvikler',
    system: 'SAK',
    behandlingstype: {
      kode: behandlingType.FORSTEGANGSSOKNAD,
      navn: 'Førstegangssøknad',
    },
    behandlingStatus: {
      kode: behandlingStatus.BEHANDLING_UTREDES,
      navn: 'Behandling utredes',
    },
    opprettetTidspunkt: '2019-01-01',
    behandlingsfrist: '2019-01-01',
    fagsakYtelseType: {
      kode: fagsakYtelseType.FORELDREPRENGER,
      navn: 'Foreldrepenger',
    },
    erTilSaksbehandling: true,
    behandlingId: '2',
    href: '',
  }],
};
