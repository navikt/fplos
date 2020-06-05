import React, { useState } from 'react';
import { action } from '@storybook/addon-actions';

import { AVDELINGSLEDER_PATH } from 'app/paths';
import { RestApiPathsKeys } from 'data/restApiPaths';
import EventType from 'data/rest-api/src/requestApi/eventType';
import HeaderWithErrorPanel from 'app/components/HeaderWithErrorPanel';
import { RestDataProvider } from 'data/RestDataContext';

import withIntl from '../decorators/withIntl';
import RequestMock from '../mocks/RequestMock';

const initialState = {
  [RestApiPathsKeys.NAV_ANSATT]: {
    navn: 'Espen Utvikler',
    kanOppgavestyre: false,
  },
};

export default {
  title: 'HeaderWithErrorPanel',
  component: HeaderWithErrorPanel,
  decorators: [
    withIntl,
    (getStory) => <RestDataProvider initialState={initialState as {[key in RestApiPathsKeys]: any}}>{getStory()}</RestDataProvider>,
  ],
};

export const skalViseHeaderUtenAvdelingsvelger = () => (
  <div style={{ marginLeft: '-40px' }}>
    <HeaderWithErrorPanel
      removeErrorMessage={action('button-click')}
      queryStrings={{}}
      setValgtAvdelingEnhet={action('button-click')}
      setSiteHeight={action('button-click')}
    />
  </div>
);

export const skalViseHeaderMedAvdelingsvelger = () => {
  const [valgtAvdelingEnhet, setValgtAvdeling] = useState<string>();
  const newInitialState = {
    [RestApiPathsKeys.NAV_ANSATT]: {
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
    .withKeyAndResult(RestApiPathsKeys.AVDELINGER, avdelinger)
    .build();

  return (
    <div style={{ marginLeft: '-40px' }}>
      <RestDataProvider initialState={newInitialState as {[key in RestApiPathsKeys]: any}} customRequestApi={requestApi}>
        <HeaderWithErrorPanel
          removeErrorMessage={action('button-click')}
          queryStrings={{}}
          valgtAvdelingEnhet={valgtAvdelingEnhet}
          setValgtAvdelingEnhet={setValgtAvdeling}
          setSiteHeight={action('button-click')}
          locationPathname={AVDELINGSLEDER_PATH}
        />
      </RestDataProvider>
    </div>
  );
};

export const skalViseHeaderMedKunEnFeilmelding = () => {
  const [errorMessages, setErrorMessages] = useState([{
    type: EventType.REQUEST_ERROR,
    text: 'Rest-kallet feilet',
  }]);

  return (
    <div style={{ marginLeft: '-40px' }}>
      <HeaderWithErrorPanel
        removeErrorMessage={() => setErrorMessages([])}
        queryStrings={{}}
        setValgtAvdelingEnhet={action('button-click')}
        errorMessages={errorMessages}
        setSiteHeight={action('button-click')}
      />
    </div>
  );
};

export const skalViseHeaderMedMerEnnFemFeilmeldinger = () => {
  const [errorMessages, setErrorMessages] = useState([{
    type: EventType.REQUEST_ERROR,
    text: 'Rest-kallet feilet',
  }, {
    type: EventType.POLLING_TIMEOUT,
    code: 'Rest.ErrorMessage.Timeout',
    params: {
      location: 'www.los.no',
    },
  }, {
    type: EventType.REQUEST_UNAUTHORIZED,
    text: 'Rest-kallet feilet 2',
  }, {
    type: EventType.REQUEST_ERROR,
    text: 'Rest-kallet feilet 3',
  }, {
    type: EventType.REQUEST_ERROR,
    text: 'Rest-kallet feilet 4',
  }, {
    type: EventType.REQUEST_ERROR,
    text: 'Rest-kallet feilet 5',
  }]);
  const [queryStrings, setQueryStrings] = useState<{ errormessage?: string}>({
    errormessage: 'Dette er ein feil',
  });

  return (
    <div style={{ marginLeft: '-40px' }}>
      <HeaderWithErrorPanel
        removeErrorMessage={() => { setErrorMessages([]); setQueryStrings({ errormessage: undefined }); }}
        queryStrings={queryStrings}
        setValgtAvdelingEnhet={action('button-click')}
        errorMessages={errorMessages}
        setSiteHeight={action('button-click')}
      />
    </div>
  );
};
