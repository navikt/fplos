import React from 'react';
import { Story } from '@storybook/react';
import { action } from '@storybook/addon-actions';

import { RestApiPathsKeys } from 'data/fplosRestApi';
import LeggTilSaksbehandlerForm from 'avdelingsleder/saksbehandlere/components/LeggTilSaksbehandlerForm';
import SaksbehandlerAvdeling from 'types/avdelingsleder/saksbehandlerAvdelingTsType';

import withIntl from '../../../decorators/withIntl';
import withRestApiProvider from '../../../decorators/withRestApi';
import RestApiMock from '../../../utils/RestApiMock';

export default {
  title: 'avdelingsleder/saksbehandlere/LeggTilSaksbehandlerForm',
  component: LeggTilSaksbehandlerForm,
  decorators: [withIntl, withRestApiProvider],
};

const saksbehandler = {
  brukerIdent: 'R232323',
  navn: 'Espen Utvikler',
  avdelingsnavn: ['NAV Viken'],
};

interface Props {
  avdelingensSaksbehandlere?: SaksbehandlerAvdeling[];
}

const Template: Story<Props> = ({
  avdelingensSaksbehandlere,
}) => {
  const data = [
    { key: RestApiPathsKeys.SAKSBEHANDLER_SOK.name, data: saksbehandler },
  ];

  return (
    <RestApiMock data={data}>
      <LeggTilSaksbehandlerForm
        avdelingensSaksbehandlere={avdelingensSaksbehandlere || []}
        hentAvdelingensSaksbehandlere={action('button-click')}
        valgtAvdelingEnhet="NAV Viken"
      />
    </RestApiMock>
  );
};

export const PanelForÅLeggeTilSaksbehandlere = Template.bind({});

export const PanelForNårSaksbehandlerErLagtTilAllerede = Template.bind({});
PanelForNårSaksbehandlerErLagtTilAllerede.args = {
  avdelingensSaksbehandlere: [saksbehandler],
};
