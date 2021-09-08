import React from 'react';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import AvdelingslederIndex from 'avdelingsleder/AvdelingslederIndex';
import NavAnsatt from 'types/navAnsattTsType';

import RestApiMock from 'storybookUtils/RestApiMock';
import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';
import withRouterProvider from 'storybookUtils/decorators/withRouter';
import alleKodeverk from 'storybookUtils/mocks/alleKodeverk.json';

export default {
  title: 'avdelingsleder/AvdelingslederIndex',
  component: AvdelingslederIndex,
  decorators: [withIntl, withRouterProvider, withRestApiProvider],
};

const navAnsattDefault = {
  kanOppgavestyre: true,
  kanBehandleKode6: true,
} as NavAnsatt;

const Template: Story<{ valgtAvdelingEnhet?: string, navAnsatt: NavAnsatt }> = ({
  valgtAvdelingEnhet,
  navAnsatt,
}) => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
    { key: RestApiGlobalStatePathsKeys.NAV_ANSATT.name, data: navAnsatt },
    { key: RestApiGlobalStatePathsKeys.AVDELINGER.name, data: {} },
    { key: RestApiPathsKeys.SAKSBEHANDLERE_FOR_AVDELING.name, data: [] },
    { key: RestApiPathsKeys.OPPGAVE_ANTALL.name, data: 1 },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_NAVN.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_INTERVALL.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_BEHANDLINGSTYPE.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER.name, data: undefined },
    { key: RestApiPathsKeys.OPPGAVE_AVDELING_ANTALL.name, data: 1 },
    { key: RestApiPathsKeys.SAKSLISTER_FOR_AVDELING.name, data: [] },
    { key: RestApiPathsKeys.HENT_OPPGAVER_FOR_AVDELING.name, data: [] },
    { key: RestApiPathsKeys.HENT_OPPGAVER_PER_DATO.name, data: [] },
    { key: RestApiPathsKeys.HENT_OPPGAVER_APNE_ELLER_PA_VENT.name, data: [] },
    { key: RestApiPathsKeys.HENT_OPPGAVER_MANUELT_PA_VENT.name, data: [] },
    { key: RestApiPathsKeys.HENT_OPPGAVER_PER_FORSTE_STONADSDAG.name, data: [] },
    { key: RestApiPathsKeys.RESERVASJONER_FOR_AVDELING.name, data: [] },
  ];

  return (
    <RestApiMock data={data}>
      <AvdelingslederIndex valgtAvdelingEnhet={valgtAvdelingEnhet} />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
Default.args = {
  valgtAvdelingEnhet: 'NAV Viken',
  navAnsatt: navAnsattDefault,
};

export const LasteIkonFørValgtAvdelingErSatt = Template.bind({});
LasteIkonFørValgtAvdelingErSatt.args = {
  valgtAvdelingEnhet: undefined,
  navAnsatt: navAnsattDefault,
};

export const HarIkkeTilgang = Template.bind({});
HarIkkeTilgang.args = {
  valgtAvdelingEnhet: undefined,
  navAnsatt: {
    kanOppgavestyre: false,
    kanBehandleKode6: false,
  } as NavAnsatt,
};
