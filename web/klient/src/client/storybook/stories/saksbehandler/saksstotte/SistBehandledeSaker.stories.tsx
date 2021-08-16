import React from 'react';
import { Story } from '@storybook/react';

import { RestApiPathsKeys, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import SistBehandledeSaker from 'saksbehandler/saksstotte/components/SistBehandledeSaker';
import Oppgave from 'types/saksbehandler/oppgaveTsType';

import RestApiMock from '../../../utils/RestApiMock';
import withRestApiProvider from '../../../decorators/withRestApi';
import withIntl from '../../../decorators/withIntl';

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

export const IngenBehandlinger = Template.bind({});

export const SistBehandlendeSaker = Template.bind({});
SistBehandlendeSaker.args = {
  behandledeOppgaver: [{
    id: 1,
    personnummer: '334342323',
    navn: 'Espen Utvikler',
  } as Oppgave],
};
