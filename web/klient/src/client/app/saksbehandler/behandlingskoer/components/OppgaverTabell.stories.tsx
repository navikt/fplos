import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import OppgaverTabell from 'saksbehandler/behandlingskoer/components/OppgaverTabell';
import behandlingStatus from 'kodeverk/behandlingStatus';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import Oppgave from 'types/saksbehandler/oppgaveTsType';

import alleKodeverk from 'storybookUtils/mocks/alleKodeverk.json';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';
import withIntl from 'storybookUtils/decorators/withIntl';
import RestApiMock from 'storybookUtils//RestApiMock';

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
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
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

export const Default = Template.bind({});
Default.args = {
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
    behandlingstype: behandlingType.FORSTEGANGSSOKNAD,
    behandlingStatus: behandlingStatus.BEHANDLING_UTREDES,
    opprettetTidspunkt: '2019-01-01',
    behandlingsfrist: '2019-01-01',
    fagsakYtelseType: fagsakYtelseType.FORELDREPRENGER,
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
    personnummer: '233',
    navn: 'Helge Utvikler',
    system: 'SAK',
    behandlingstype: behandlingType.KLAGE,
    behandlingStatus: behandlingStatus.BEHANDLING_UTREDES,
    opprettetTidspunkt: '2019-01-01',
    behandlingsfrist: '2019-01-01',
    fagsakYtelseType: fagsakYtelseType.FORELDREPRENGER,
    erTilSaksbehandling: true,
    behandlingId: '2',
    href: '',
  }],
};

export const TomOppgaveTabell = Template.bind({});
