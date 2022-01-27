import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiPathsKeys } from 'data/fplosRestApi';
import OpphevReservasjonModal from 'saksbehandler/behandlingskoer/components/menu/OpphevReservasjonModal';
import behandlingStatus from 'kodeverk/behandlingStatus';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import Oppgave from 'types/saksbehandler/oppgaveTsType';
import RestApiMock from 'storybookUtils//RestApiMock';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';
import withIntl from 'storybookUtils/decorators/withIntl';

export default {
  title: 'saksbehandler/behandlingskoer/OpphevReservasjonModal',
  component: OpphevReservasjonModal,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{
  oppgave: Oppgave,
  opphevData: any,
  hentReserverteOppgaver: (params: any, keepData: boolean) => void,
}> = ({
  oppgave,
  opphevData,
  hentReserverteOppgaver,
}) => {
  const data = [
    { key: RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON.name, data: opphevData },
  ];

  return (
    <RestApiMock data={data}>
      <OpphevReservasjonModal
        showModal
        oppgave={oppgave}
        cancel={action('button-click')}
        hentReserverteOppgaver={hentReserverteOppgaver}
        toggleMenu={action('button-click')}
      />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
Default.args = {
  opphevData: {},
  hentReserverteOppgaver: action('button-click'),
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
  },
};
