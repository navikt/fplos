import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiPathsKeys } from 'data/fplosRestApi';
import OpphevReservasjonModal from 'saksbehandler/behandlingskoer/components/menu/OpphevReservasjonModal';
import behandlingStatus from 'kodeverk/behandlingStatus';
import behandlingType from 'kodeverk/behandlingType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import Oppgave from 'types/saksbehandler/oppgaveTsType';
import RestApiMock from '../../../utils/RestApiMock';
import withRestApiProvider from '../../../decorators/withRestApi';
import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/behandlingskoer/OpphevReservasjonModal',
  component: OpphevReservasjonModal,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{ oppgave: Oppgave }> = ({
  oppgave,
}) => {
  const data = [
    { key: RestApiPathsKeys.OPPHEV_OPPGAVERESERVASJON.name, data: {} },
  ];

  return (
    <RestApiMock data={data}>
      <OpphevReservasjonModal
        showModal
        oppgave={oppgave}
        cancel={action('button-click')}
        hentReserverteOppgaver={action('button-click')}
        toggleMenu={action('button-click')}
      />
    </RestApiMock>
  );
};

export const ModalForEndringAvReservasjon = Template.bind({});
ModalForEndringAvReservasjon.args = {
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
  },
};
