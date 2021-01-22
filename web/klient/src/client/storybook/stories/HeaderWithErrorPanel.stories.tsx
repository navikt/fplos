import React, { useState } from 'react';
import { action } from '@storybook/addon-actions';

import { AVDELINGSLEDER_PATH } from 'app/paths';
import EventType from 'data/rest-api/src/requestApi/eventType';
import HeaderWithErrorPanel from 'app/components/HeaderWithErrorPanel';
import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { RestApiErrorProvider } from 'data/rest-api-hooks';

import withIntl from '../decorators/withIntl';
import withRestApiProvider from '../decorators/withRestApi';

export default {
  title: 'HeaderWithErrorPanel',
  component: HeaderWithErrorPanel,
  decorators: [withIntl, withRestApiProvider],
};

const navAnsatt = {
  navn: 'Espen Utvikler',
  kanOppgavestyre: false,
};

export const skalViseHeaderUtenAvdelingsvelger = () => {
  requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT, navAnsatt);
  requestApi.mock(RestApiGlobalStatePathsKeys.DRIFTSMELDINGER, []);

  return (
    <div style={{ marginLeft: '-40px' }}>
      <HeaderWithErrorPanel
        queryStrings={{}}
        setValgtAvdelingEnhet={action('button-click')}
        setSiteHeight={action('button-click')}
      />
    </div>
  );
};

export const skalViseHeaderMedAvdelingsvelger = () => {
  const [valgtAvdelingEnhet, setValgtAvdeling] = useState<string>();
  const avdelinger = [{
    avdelingEnhet: 'VIK',
    navn: 'NAV Viken',
    kreverKode6: false,
  }, {
    avdelingEnhet: 'OSL',
    navn: 'NAV Oslo',
    kreverKode6: false,
  }];

  requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT, navAnsatt);
  requestApi.mock(RestApiGlobalStatePathsKeys.DRIFTSMELDINGER, []);
  requestApi.mock(RestApiGlobalStatePathsKeys.AVDELINGER, avdelinger);

  return (
    <div style={{ marginLeft: '-40px' }}>
      <HeaderWithErrorPanel
        queryStrings={{}}
        valgtAvdelingEnhet={valgtAvdelingEnhet}
        setValgtAvdelingEnhet={setValgtAvdeling}
        setSiteHeight={action('button-click')}
        locationPathname={AVDELINGSLEDER_PATH}
      />
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

  requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT, navAnsatt);
  requestApi.mock(RestApiGlobalStatePathsKeys.DRIFTSMELDINGER, []);

  return (
    <div style={{ marginLeft: '-40px' }}>
      <RestApiErrorProvider initialState={errorInitialState}>
        <HeaderWithErrorPanel
          queryStrings={{}}
          setValgtAvdelingEnhet={action('button-click')}
          setSiteHeight={action('button-click')}
        />
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

  requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT, navAnsatt);
  requestApi.mock(RestApiGlobalStatePathsKeys.DRIFTSMELDINGER, []);

  return (
    <div style={{ marginLeft: '-40px' }}>
      <RestApiErrorProvider initialState={errorInitialState}>
        <HeaderWithErrorPanel
          queryStrings={queryStrings}
          setValgtAvdelingEnhet={action('button-click')}
          setSiteHeight={action('button-click')}
        />
      </RestApiErrorProvider>
    </div>
  );
};
