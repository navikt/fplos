import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiPathsKeys } from 'data/fplosRestApi';
import OppgaveHandlingerMenu from 'saksbehandler/behandlingskoer/components/menu/OppgaveHandlingerMenu';
import behandlingStatus from 'kodeverk/behandlingStatus';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import Oppgave from 'types/saksbehandler/oppgaveTsType';

import withRestApiProvider from 'storybookUtils/decorators/withRestApi';
import withIntl from 'storybookUtils/decorators/withIntl';
import RestApiMock from 'storybookUtils//RestApiMock';

export default {
  title: 'saksbehandler/behandlingskoer/OppgaveHandlingerMenu',
  component: OppgaveHandlingerMenu,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{ oppgave: Oppgave }> = ({
  oppgave,
}) => {
  const data = [
    { key: RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON.name, data: {} },
    {
      key: RestApiPathsKeys.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK.name,
      data: {
        brukerIdent: 'teasdfa',
        navn: 'Espen Utvikler',
        avdelingsnavn: ['NAV Viken'],
      },
    },
    { key: RestApiPathsKeys.FLYTT_RESERVASJON.name, data: {} },
  ];

  return (
    <RestApiMock data={data}>
      <OppgaveHandlingerMenu
        toggleMenu={action('button-click')}
        offset={{ top: 50, left: 0 }}
        oppgave={oppgave}
        imageNode=""
        forlengOppgaveReservasjon={() => Promise.resolve('')}
        hentReserverteOppgaver={action('button-click')}
      />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
Default.args = {
  oppgave: {
    id: 1,
    status: {
      erReservert: false,
      flyttetReservasjon: {
        tidspunkt: '2019-02-02',
        uid: '23423',
        navn: 'Espen Utvikler',
        begrunnelse: 'Flyttet',
      },
      reservertTilTidspunkt: '2021-08-02T00:54:25.455',
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
  } as Oppgave,
};
