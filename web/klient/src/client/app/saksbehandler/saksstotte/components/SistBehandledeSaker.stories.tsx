import React from 'react';
import { Story } from '@storybook/react';

import { RestApiPathsKeys, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import Oppgave from 'types/saksbehandler/oppgaveTsType';
import RestApiMock from 'storybookUtils/RestApiMock';
import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';
import SistBehandledeSaker from './SistBehandledeSaker';

export default {
  title: 'saksbehandler/saksstotte/SistBehandledeSaker',
  component: SistBehandledeSaker,
  decorators: [
    withIntl,
    withRestApiProvider,
  ],
};

const Template: Story<{ behandledeOppgaver?: Oppgave[] }> = ({
  behandledeOppgaver,
}) => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.FPSAK_URL.name, data: { value: 'fpsak-url' } },
    { key: RestApiPathsKeys.BEHANDLEDE_OPPGAVER.name, data: behandledeOppgaver },
  ];

  return (
    <RestApiMock data={data}>
      <SistBehandledeSaker />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
Default.args = {
  behandledeOppgaver: [{
    id: 1,
    personnummer: '334342323',
    navn: 'Espen Utvikler',
  } as Oppgave],
};

export const IngenBehandlinger = Template.bind({});
