import React from 'react';
import { Story } from '@storybook/react';

import SaksbehandlereTabell from 'avdelingsleder/saksbehandlere/components/SaksbehandlereTabell';
import Saksbehandler from 'types/avdelingsleder/saksbehandlerAvdelingTsType';
import { RestApiPathsKeys } from 'data/fplosRestApi';

import RestApiMock from 'storybookUtils/RestApiMock';
import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';

export default {
  title: 'avdelingsleder/saksbehandlere/SaksbehandlereTabell',
  component: SaksbehandlereTabell,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{
  saksbehandlere?: Saksbehandler[],
  hentAvdelingensSaksbehandlere: (params: {avdelingEnhet: string}) => void
}> = ({
  saksbehandlere,
  hentAvdelingensSaksbehandlere,
}) => {
  const data = [
    { key: RestApiPathsKeys.SLETT_SAKSBEHANDLER.name, data: undefined },
  ];

  return (
    <RestApiMock data={data}>
      <SaksbehandlereTabell
        saksbehandlere={saksbehandlere || []}
        hentAvdelingensSaksbehandlere={hentAvdelingensSaksbehandlere}
        valgtAvdelingEnhet="NAV Viken"
      />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
Default.args = {
  saksbehandlere: [{
    brukerIdent: 'R12122',
    navn: 'Espen Utvikler',
    avdelingsnavn: ['NAV Viken'],
  }, {
    brukerIdent: 'S53343',
    navn: 'Steffen',
    avdelingsnavn: ['NAV Oslo'],
  }],
  hentAvdelingensSaksbehandlere: () => undefined,
};

export const TomTabell = Template.bind({});
Template.args = {
  hentAvdelingensSaksbehandlere: () => undefined,
};
