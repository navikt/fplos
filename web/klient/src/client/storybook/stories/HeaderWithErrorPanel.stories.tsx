import React, { useState } from 'react';
import { action } from '@storybook/addon-actions';

import { AVDELINGSLEDER_PATH } from 'app/paths';
import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import EventType from 'data/rest-api/src/requestApi/eventType';
import HeaderWithErrorPanel from 'app/components/HeaderWithErrorPanel';
import { RestApiGlobalDataProvider, RestApiErrorProvider } from 'data/rest-api-hooks';


import withIntl from '../decorators/withIntl';
import RequestMock from '../mocks/RequestMock';

export default {
  title: 'HeaderWithErrorPanel',
  component: HeaderWithErrorPanel,
  decorators: [withIntl],
};

const initialState = {
  [RestApiGlobalStatePathsKeys.NAV_ANSATT]: {
    navn: 'Espen Utvikler',
    kanOppgavestyre: false,
  },
};

export const skalViseHeaderUtenAvdelingsvelger = () => (
  <div style={{ marginLeft: '-40px' }}>
    <RestApiGlobalDataProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}}>
      <HeaderWithErrorPanel
        queryStrings={{}}
        setValgtAvdelingEnhet={action('button-click')}
        setSiteHeight={action('button-click')}
      />
    </RestApiGlobalDataProvider>
  </div>
);

export const skalViseHeaderMedAvdelingsvelger = () => {
  const [valgtAvdelingEnhet, setValgtAvdeling] = useState<string>();
  const newInitialState = {
    [RestApiGlobalStatePathsKeys.NAV_ANSATT]: {
      navn: 'Espen Utvikler',
      kanOppgavestyre: true,
    },
  };
  const avdelinger = [{
    avdelingEnhet: 'VIK',
    navn: 'NAV Viken',
    kreverKode6: false,
  }, {
    avdelingEnhet: 'OSL',
    navn: 'NAV Oslo',
    kreverKode6: false,
  }];
  const requestApi = new RequestMock()
    .withKeyAndResult(RestApiGlobalStatePathsKeys.AVDELINGER, avdelinger)
    .build();

  return (
    <div style={{ marginLeft: '-40px' }}>
      <RestApiGlobalDataProvider initialState={newInitialState as {[key in RestApiGlobalStatePathsKeys]: any}} requestApi={requestApi}>
        <HeaderWithErrorPanel
          queryStrings={{}}
          valgtAvdelingEnhet={valgtAvdelingEnhet}
          setValgtAvdelingEnhet={setValgtAvdeling}
          setSiteHeight={action('button-click')}
          locationPathname={AVDELINGSLEDER_PATH}
        />
      </RestApiGlobalDataProvider>
    </div>
  );
};

export const skalViseHeaderMedKunEnFeilmelding = () => {
  const errorInitialState = {
    errors: [{
      type: EventType.REQUEST_ERROR,
      feilmelding: 'Dette er en feilmelding',
    }],
  };

  return (
    <div style={{ marginLeft: '-40px' }}>
      <RestApiErrorProvider initialState={errorInitialState}>
        <RestApiGlobalDataProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}}>
          <HeaderWithErrorPanel
            queryStrings={{}}
            setValgtAvdelingEnhet={action('button-click')}
            setSiteHeight={action('button-click')}
          />
        </RestApiGlobalDataProvider>
      </RestApiErrorProvider>
    </div>
  );
};

export const skalViseHeaderMedMerEnnFemFeilmeldinger = () => {
  const errorInitialState = {
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
  };
  const queryStrings = {
    errormessage: 'Dette er ein feil',
  };

  return (
    <div style={{ marginLeft: '-40px' }}>
      <RestApiErrorProvider initialState={errorInitialState}>
        <RestApiGlobalDataProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}}>
          <HeaderWithErrorPanel
            queryStrings={queryStrings}
            setValgtAvdelingEnhet={action('button-click')}
            setSiteHeight={action('button-click')}
          />
        </RestApiGlobalDataProvider>
      </RestApiErrorProvider>
    </div>
  );
};
