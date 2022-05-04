import React from 'react';
import { Story } from '@storybook/react';
import dayjs from 'dayjs';

import { ISO_DATE_FORMAT } from '@navikt/ft-utils';
import { RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import RestApiMock from 'storybookUtils/RestApiMock';
import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';
import alleKodeverk from 'storybookUtils/mocks/alleKodeverk.json';
import KoSortering from 'kodeverk/KoSortering';
import BehandlingType from 'kodeverk/behandlingType';
import FagsakYtelseType from 'kodeverk/fagsakYtelseType';
import AndreKriterierType from 'kodeverk/andreKriterierType';
import BehandlingStatus from 'kodeverk/behandlingStatus';
import SaksbehandlerIndex from './SaksbehandlerIndex';

export default {
  title: 'saksbehandler/SaksbehandlerIndex',
  component: SaksbehandlerIndex,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story = () => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
    { key: RestApiGlobalStatePathsKeys.FPSAK_URL.name, data: 'url' },
    { key: RestApiGlobalStatePathsKeys.NAV_ANSATT.name, data: { kanSaksbehandle: true } },
    {
      key: RestApiPathsKeys.SAKSLISTE.name,
      data: [{
        sakslisteId: 1,
        navn: 'Saksliste 1',
        sistEndret: '2020-10-10',
        saksbehandlerIdenter: [],
        antallBehandlinger: 1,
        sortering: {
          sorteringType: {
            kode: KoSortering.BEHANDLINGSFRIST,
            navn: 'Behandlingsfrist',
            felttype: '',
            feltkategori: '',
          },
          fra: 1,
          til: 4,
          erDynamiskPeriode: true,
        },
        behandlingTyper: [BehandlingType.FORSTEGANGSSOKNAD],
        fagsakYtelseTyper: [FagsakYtelseType.FORELDREPRENGER],
        andreKriterier: [{
          andreKriterierType: AndreKriterierType.TIL_BESLUTTER,
          inkluder: true,
        }, {
          andreKriterierType: AndreKriterierType.REGISTRER_PAPIRSOKNAD,
          inkluder: false,
        }],
      }],
    },
    { key: RestApiPathsKeys.RESERVER_OPPGAVE.name, data: undefined },
    {
      key: RestApiPathsKeys.RESERVERTE_OPPGAVER.name,
      data: [{
        id: 2,
        status: {
          erReservert: true,
        },
        saksnummer: 1234,
        personnummer: '1212',
        navn: 'Espen Utvikler',
        system: 'SAK',
        behandlingstype: BehandlingType.FORSTEGANGSSOKNAD,
        behandlingStatus: BehandlingStatus.BEHANDLING_UTREDES,
        opprettetTidspunkt: '2019-01-01',
        behandlingsfrist: '2019-01-01',
        fagsakYtelseType: FagsakYtelseType.FORELDREPRENGER,
        erTilSaksbehandling: true,
        behandlingId: '2',
        href: '',
      }],
    },
    {
      key: RestApiPathsKeys.SAKSLISTE_SAKSBEHANDLERE.name,
      data: [{
        brukerIdent: {
          brukerIdent: '32434',
          verdi: '32434',
        },
        navn: 'Espen Utvikler',
        avdelingsnavn: [],
      }, {
        brukerIdent: {
          brukerIdent: '31111',
          verdi: '32111',
        },
        navn: 'Auto Joakim',
        avdelingsnavn: [],
      }],
    },
    { key: RestApiPathsKeys.BEHANDLINGSKO_OPPGAVE_ANTALL.name, data: 1 },
    { key: RestApiPathsKeys.RESERVER_OPPGAVE.name, data: undefined },
    { key: RestApiPathsKeys.SEARCH_FAGSAK.name, data: undefined },
    { key: RestApiPathsKeys.OPPGAVER_FOR_FAGSAKER.name, data: undefined },
    {
      key: RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING.name,
      data: [{
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
        behandlingstype: BehandlingType.FORSTEGANGSSOKNAD,
        behandlingStatus: BehandlingStatus.BEHANDLING_UTREDES,
        opprettetTidspunkt: '2019-01-01',
        behandlingsfrist: '2019-01-01',
        fagsakYtelseType: FagsakYtelseType.FORELDREPRENGER,
        erTilSaksbehandling: true,
        behandlingId: '1',
        href: '',
      }],
    },
    { key: RestApiPathsKeys.HENT_RESERVASJONSSTATUS.name, data: undefined },
    { key: RestApiPathsKeys.BEHANDLEDE_OPPGAVER.name, data: [] },
    {
      key: RestApiPathsKeys.HENT_NYE_OG_FERDIGSTILTE_OPPGAVER.name,
      data: [{
        behandlingType: BehandlingType.FORSTEGANGSSOKNAD,
        antallNye: 10,
        antallFerdigstilte: 20,
        dato: dayjs().format(ISO_DATE_FORMAT),
      }, {
        behandlingType: BehandlingType.KLAGE,
        antallNye: 23,
        antallFerdigstilte: 2,
        dato: dayjs().format(ISO_DATE_FORMAT),
      }, {
        behandlingType: BehandlingType.REVURDERING,
        antallNye: 3,
        antallFerdigstilte: 24,
        dato: dayjs().format(ISO_DATE_FORMAT),
      }, {
        behandlingType: BehandlingType.DOKUMENTINNSYN,
        antallNye: 23,
        antallFerdigstilte: 12,
        dato: dayjs().format(ISO_DATE_FORMAT),
      }, {
        behandlingType: BehandlingType.FORSTEGANGSSOKNAD,
        antallNye: 10,
        antallFerdigstilte: 20,
        dato: dayjs().subtract(1, 'd').format(ISO_DATE_FORMAT),
      }, {
        behandlingType: BehandlingType.FORSTEGANGSSOKNAD,
        antallNye: 30,
        antallFerdigstilte: 15,
        dato: dayjs().subtract(3, 'd').format(ISO_DATE_FORMAT),
      }, {
        behandlingType: BehandlingType.KLAGE,
        antallNye: 23,
        antallFerdigstilte: 2,
        dato: dayjs().subtract(4, 'd').format(ISO_DATE_FORMAT),
      }, {
        behandlingType: BehandlingType.KLAGE,
        antallNye: 23,
        antallFerdigstilte: 2,
        dato: dayjs().subtract(5, 'd').format(ISO_DATE_FORMAT),
      }],
    },
  ];

  return (
    <RestApiMock data={data}>
      <SaksbehandlerIndex />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
