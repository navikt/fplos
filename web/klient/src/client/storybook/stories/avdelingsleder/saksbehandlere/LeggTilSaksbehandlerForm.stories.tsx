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
  funnetSaksbehandler?: SaksbehandlerAvdeling;
  hentAvdelingensSaksbehandlere: (params: {avdelingEnhet: string}) => void;
}

const Template: Story<Props> = ({
  avdelingensSaksbehandlere,
  funnetSaksbehandler,
  hentAvdelingensSaksbehandlere,
}) => {
  const data = [
    { key: RestApiPathsKeys.SAKSBEHANDLER_SOK.name, data: funnetSaksbehandler },
    { key: RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER.name, data: undefined },
  ];

  return (
    <RestApiMock data={data}>
      <LeggTilSaksbehandlerForm
        avdelingensSaksbehandlere={avdelingensSaksbehandlere || []}
        hentAvdelingensSaksbehandlere={hentAvdelingensSaksbehandlere}
        valgtAvdelingEnhet="NAV Viken"
      />
    </RestApiMock>
  );
};

export const Default = Template.bind({});
Default.args = {
  funnetSaksbehandler: saksbehandler,
  hentAvdelingensSaksbehandlere: action('button-click'),
};

export const AlleredeLagtTil = Template.bind({});
AlleredeLagtTil.args = {
  avdelingensSaksbehandlere: [saksbehandler],
  funnetSaksbehandler: saksbehandler,
  hentAvdelingensSaksbehandlere: action('button-click'),
};

export const SaksbehandlerFinnesIkke = Template.bind({});
SaksbehandlerFinnesIkke.args = {
  avdelingensSaksbehandlere: [saksbehandler],
  funnetSaksbehandler: undefined,
  hentAvdelingensSaksbehandlere: action('button-click'),
};
