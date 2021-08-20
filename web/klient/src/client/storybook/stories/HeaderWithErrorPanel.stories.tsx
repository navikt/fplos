import React, { useState } from 'react';
import { Story } from '@storybook/react';
import { action } from '@storybook/addon-actions';

import { AVDELINGSLEDER_PATH } from 'app/paths';
import EventType from 'data/rest-api/src/requestApi/eventType';
import HeaderWithErrorPanel from 'app/components/HeaderWithErrorPanel';
import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { RestApiErrorProvider } from 'data/rest-api-hooks';

import RestApiMock from '../utils/RestApiMock';
import withIntl from '../decorators/withIntl';
import withRestApiProvider from '../decorators/withRestApi';

export default {
  title: 'HeaderWithErrorPanel',
  component: HeaderWithErrorPanel,
  decorators: [withIntl, withRestApiProvider],
};

const getNavAnsatt = (kanOppgavestyre = false) => ({
  navn: 'Espen Utvikler',
  kanOppgavestyre,
});

interface Props {
  queryStrings: {
    errormessage?: string;
    errorcode?: string;
  };
  avdelinger?: any;
  setValgtAvdelingEnhet?: any;
  locationPathname?: string;
  errorInitialState?: any;
  driftsmeldinger?: {
    id: string,
    melding: string,
  }[];
}

const Template: Story<Props> = ({
  queryStrings,
  setValgtAvdelingEnhet,
  avdelinger,
  locationPathname,
  errorInitialState,
  driftsmeldinger = [],
}) => {
  const [valgtAvdelingEnhet, setValgtAvdeling] = useState<string | undefined>(undefined);

  const data = [
    { key: RestApiGlobalStatePathsKeys.NAV_ANSATT.name, data: getNavAnsatt(!!avdelinger) },
    { key: RestApiGlobalStatePathsKeys.DRIFTSMELDINGER.name, data: driftsmeldinger },
  ];
  if (avdelinger) {
    data.push({ key: RestApiGlobalStatePathsKeys.AVDELINGER.name, data: avdelinger });
  }

  return (
    <RestApiMock data={data}>
      <RestApiErrorProvider initialState={errorInitialState}>
        <div style={{ marginLeft: '-40px' }}>
          <HeaderWithErrorPanel
            queryStrings={queryStrings}
            setValgtAvdelingEnhet={setValgtAvdelingEnhet || setValgtAvdeling}
            setSiteHeight={action('button-click')}
            valgtAvdelingEnhet={valgtAvdelingEnhet}
            locationPathname={locationPathname}
          />
        </div>
      </RestApiErrorProvider>
    </RestApiMock>
  );
};

export const HeaderUtenAvdelingsvelger = Template.bind({});
HeaderUtenAvdelingsvelger.args = {
  queryStrings: {},
  setValgtAvdelingEnhet: action('button-click'),
};

export const HeaderMedAvdelingsvelger = Template.bind({});
HeaderMedAvdelingsvelger.args = {
  queryStrings: {},
  avdelinger: [{
    avdelingEnhet: 'VIK',
    navn: 'NAV Viken',
    kreverKode6: false,
  }, {
    avdelingEnhet: 'OSL',
    navn: 'NAV Oslo',
    kreverKode6: false,
  }],
  locationPathname: AVDELINGSLEDER_PATH,
};

export const HeaderMedKunEnFeilmelding = Template.bind({});
HeaderMedKunEnFeilmelding.args = {
  queryStrings: {},
  setValgtAvdelingEnhet: action('button-click'),
  errorInitialState: {
    errors: [{
      type: EventType.REQUEST_ERROR,
      feilmelding: 'Dette er en feilmelding',
    }],
  },
};

export const HeaderMedMerEnnFemFeilmeldinger = Template.bind({});
HeaderMedMerEnnFemFeilmeldinger.args = {
  queryStrings: {
    errormessage: 'Dette er ein feil',
  },
  setValgtAvdelingEnhet: action('button-click'),
  errorInitialState: {
    errors: [{
      type: EventType.REQUEST_ERROR,
      feilmelding: 'Rest-kallet feilet',
    }, {
      type: EventType.POLLING_TIMEOUT,
      message: 'Rest.ErrorMessage.Timeout',
      location: 'www.los.no',
    }, {
      type: EventType.REQUEST_UNAUTHORIZED,
      feilmelding: 'Rest-kallet feilet 2',
    }, {
      type: EventType.REQUEST_ERROR,
      feilmelding: 'Rest-kallet feilet 3',
    }, {
      type: EventType.REQUEST_ERROR,
      feilmelding: 'Rest-kallet feilet 4',
    }, {
      type: EventType.REQUEST_ERROR,
      feilmelding: 'Rest-kallet feilet 5',
    }],
  },
};

export const HeaderMedDriftsmeldinger = Template.bind({});
HeaderMedDriftsmeldinger.args = {
  queryStrings: {},
  setValgtAvdelingEnhet: action('button-click'),
  driftsmeldinger: [{
    id: '1',
    melding: 'Dette er driftsmelding 1',
  }, {
    id: '2',
    melding: 'Dette er driftsmelding 2',
  }],
};
