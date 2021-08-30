import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import Saksliste from 'types/avdelingsleder/sakslisteAvdelingTsType';
import SaksbehandlereForSakslisteForm from 'avdelingsleder/behandlingskoer/components/saksbehandlerForm/SaksbehandlereForSakslisteForm';
import SaksbehandlerAvdeling from 'types/avdelingsleder/saksbehandlerAvdelingTsType';
import { RestApiPathsKeys } from 'data/fplosRestApi';

import withIntl from '../../../decorators/withIntl';
import withRestApiProvider from '../../../decorators/withRestApi';
import RestApiMock from '../../../utils/RestApiMock';

export default {
  title: 'avdelingsleder/behandlingskoer/SaksbehandlereForSakslisteForm',
  component: SaksbehandlereForSakslisteForm,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{
  saksliste: Saksliste,
  avdelingensSaksbehandlere: SaksbehandlerAvdeling[],
  hentAvdelingensSakslister: (params: {avdelingEnhet: string}) => void;
}> = ({
  saksliste,
  avdelingensSaksbehandlere,
  hentAvdelingensSakslister,
}) => {
  const data = [
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_SAKSBEHANDLER.name, data: {} },
  ];

  return (
    <RestApiMock data={data}>
      <SaksbehandlereForSakslisteForm
        valgtSaksliste={saksliste}
        avdelingensSaksbehandlere={avdelingensSaksbehandlere}
        hentAvdelingensSakslister={hentAvdelingensSakslister}
        valgtAvdelingEnhet="NAV Viken"
      />
    </RestApiMock>
  );
};

export const IngenSaksbehandlere = Template.bind({});
IngenSaksbehandlere.args = {
  saksliste: {
    sakslisteId: 1,
    navn: 'Saksliste 1',
    sistEndret: '2020-01-01',
    saksbehandlerIdenter: ['S34354'],
    antallBehandlinger: 1,
  },
  avdelingensSaksbehandlere: [],
  hentAvdelingensSakslister: action('button-click'),
};

export const ToSaksbehandlere = Template.bind({});
ToSaksbehandlere.args = {
  saksliste: {
    sakslisteId: 1,
    navn: 'Saksliste 1',
    sistEndret: '2020-01-01',
    saksbehandlerIdenter: ['S34354'],
    antallBehandlinger: 1,
  },
  avdelingensSaksbehandlere: [{
    brukerIdent: 'E23232',
    navn: 'Espen Utvikler',
    avdelingsnavn: ['NAV Viken'],
  }, {
    brukerIdent: 'S34354',
    navn: 'Steffen',
    avdelingsnavn: ['NAV Viken'],
  }],
  hentAvdelingensSakslister: action('button-click'),
};

export const TreSaksbehandlere = Template.bind({});
TreSaksbehandlere.args = {
  saksliste: {
    sakslisteId: 1,
    navn: 'Saksliste 1',
    sistEndret: '2020-01-01',
    saksbehandlerIdenter: ['S34354'],
    antallBehandlinger: 1,
  },
  avdelingensSaksbehandlere: [{
    brukerIdent: 'E23232',
    navn: 'Espen Utvikler',
    avdelingsnavn: ['NAV Viken'],
  }, {
    brukerIdent: 'S34354',
    navn: 'Steffen',
    avdelingsnavn: ['NAV Viken'],
  }, {
    brukerIdent: 'E24353',
    navn: 'Eirik',
    avdelingsnavn: ['NAV Viken'],
  }],
  hentAvdelingensSakslister: action('button-click'),
};
