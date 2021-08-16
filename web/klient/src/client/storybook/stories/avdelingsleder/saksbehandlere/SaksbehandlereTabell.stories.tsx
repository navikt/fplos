import React from 'react';
import { Story } from '@storybook/react';

import SaksbehandlereTabell from 'avdelingsleder/saksbehandlere/components/SaksbehandlereTabell';
import Saksbehandler from 'types/avdelingsleder/saksbehandlerAvdelingTsType';

import withRestApiProvider from '../../../decorators/withRestApi';
import withIntl from '../../../decorators/withIntl';

export default {
  title: 'avdelingsleder/saksbehandlere/SaksbehandlereTabell',
  component: SaksbehandlereTabell,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{ saksbehandlere?: Saksbehandler[] }> = ({
  saksbehandlere,
}) => (
  <SaksbehandlereTabell
    saksbehandlere={saksbehandlere || []}
    hentAvdelingensSaksbehandlere={() => undefined}
    valgtAvdelingEnhet="NAV Viken"
  />
);

export const TomTabell = Template.bind({});

export const SaksbehandlereITabell = Template.bind({});
SaksbehandlereITabell.args = {
  saksbehandlere: [{
    brukerIdent: 'R12122',
    navn: 'Espen Utvikler',
    avdelingsnavn: ['NAV Viken'],
  }, {
    brukerIdent: 'S53343',
    navn: 'Steffen',
    avdelingsnavn: ['NAV Oslo'],
  }],
};
