import React from 'react';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import FagsakSearchIndex from 'saksbehandler/fagsakSearch/FagsakSearchIndex';

import RestApiMock from 'storybookUtils/RestApiMock';
import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';

export default {
  title: 'saksbehandler/fagsakSearch/FagsakSearchIndex',
  component: FagsakSearchIndex,
  decorators: [
    withIntl,
    withRestApiProvider,
  ],
};

const Template: Story = () => {
  const data = [
    { key: RestApiPathsKeys.RESERVER_OPPGAVE.name, data: undefined },
    { key: RestApiPathsKeys.SEARCH_FAGSAK.name, data: {} },
    { key: RestApiPathsKeys.OPPGAVER_FOR_FAGSAKER.name, data: [] },
    { key: RestApiPathsKeys.HENT_RESERVASJONSSTATUS.name, data: undefined },
    { key: RestApiGlobalStatePathsKeys.NAV_ANSATT.name, data: { kanSaksbehandle: true } },
  ];

  return (
    <RestApiMock data={data}>
      <FagsakSearchIndex
        fpsakUrl="url"
      />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
