import React from 'react';
import { Story } from '@storybook/react';

import SaksbehandlerIndex from 'saksbehandler/SaksbehandlerIndex';
import { RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';

import RestApiMock from 'storybookUtils/RestApiMock';
import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';

export default {
  title: 'saksbehandler/SaksbehandlerIndex',
  component: SaksbehandlerIndex,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story = () => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.FPSAK_URL.name, data: 'url' },
    { key: RestApiGlobalStatePathsKeys.NAV_ANSATT.name, data: { kanSaksbehandle: true } },
    { key: RestApiPathsKeys.SAKSLISTE.name, data: [] },
    { key: RestApiPathsKeys.RESERVER_OPPGAVE.name, data: undefined },
    { key: RestApiPathsKeys.BEHANDLINGSKO_OPPGAVE_ANTALL.name, data: 1 },
    { key: RestApiPathsKeys.RESERVER_OPPGAVE.name, data: undefined },
    { key: RestApiPathsKeys.SEARCH_FAGSAK.name, data: undefined },
    { key: RestApiPathsKeys.OPPGAVER_FOR_FAGSAKER.name, data: undefined },
    { key: RestApiPathsKeys.HENT_RESERVASJONSSTATUS.name, data: undefined },
    { key: RestApiPathsKeys.BEHANDLEDE_OPPGAVER.name, data: [] },
    { key: RestApiPathsKeys.HENT_NYE_OG_FERDIGSTILTE_OPPGAVER.name, data: undefined },
  ];

  return (
    <RestApiMock data={data}>
      <SaksbehandlerIndex />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
