import React, {
  FunctionComponent, useState, useEffect, useCallback,
} from 'react';
import { useLocation } from 'react-router-dom';
import dayjs from 'dayjs';

import { parseQueryString } from '@navikt/ft-utils';
import { restApiHooks, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import AppConfigResolver from './AppConfigResolver';
import LanguageProvider from './LanguageProvider';
import HeaderWithErrorPanel from './components/HeaderWithErrorPanel';
import Home from './components/Home';
import ErrorBoundary from './ErrorBoundary';

import '../../styles/global.less';
import '@navikt/ft-ui-komponenter/dist/style.css';
import '@navikt/ft-form-hooks/dist/style.css';
import '@navikt/ft-plattform-komponenter/dist/style.css';

/**
 * AppIndex
 *
 * Dette er toppkomponenten i applikasjonen. Denne vil rendre header
 * og home-komponentene. Home-komponenten vil rendre barn-komponenter via ruter.
 * Komponenten er også ansvarlig for å hente innlogget NAV-ansatt, rettskilde-url, systemrutine-url
 * og kodeverk fra server og lagre desse i klientens state.
 */
const AppIndex: FunctionComponent = () => {
  const [headerHeight, setHeaderHeight] = useState(0);
  const [crashMessage, setCrashMessage] = useState<string>();
  const [valgtAvdelingEnhet, setValgtAvdelingEnhet] = useState<string>();

  const navAnsatt = restApiHooks.useGlobalStateRestApiData(RestApiGlobalStatePathsKeys.NAV_ANSATT);

  const location = useLocation();

  useEffect(() => {
    if (navAnsatt?.funksjonellTid) {
      // TODO (TOR) Dette endrar jo berre moment. Kva med kode som brukar Date direkte?
      const diffInMinutes = dayjs().diff(navAnsatt.funksjonellTid, 'minutes');
      // Hvis diffInMinutes har avvik på over 5min: override moment.now (ref. http://momentjs.com/docs/#/customization/now/)
      if (diffInMinutes >= 5 || diffInMinutes <= -5) {
        // TODO ta i bruk dayjs
        // const diff = dayjs().diff(funksjonellTid);
        // moment.now = () => Date.now() - diff;
      }
    }
  }, [navAnsatt?.funksjonellTid]);

  const setSiteHeight = useCallback((newHeaderHeight: number): void => {
    document.documentElement.setAttribute('style', `height: calc(100% - ${newHeaderHeight}px)`);
    setHeaderHeight(newHeaderHeight);
  }, []);

  const addErrorMessageAndSetAsCrashed = (error: string) => {
    setCrashMessage(error);
  };

  const queryStrings = parseQueryString(location.search);

  return (
    // @ts-ignore Usikker på kvifor dette feilar
    <ErrorBoundary errorMessageCallback={addErrorMessageAndSetAsCrashed}>
      <AppConfigResolver>
        <LanguageProvider>
          <HeaderWithErrorPanel
            queryStrings={queryStrings}
            setSiteHeight={setSiteHeight}
            locationPathname={location.pathname}
            setValgtAvdelingEnhet={setValgtAvdelingEnhet}
            valgtAvdelingEnhet={valgtAvdelingEnhet}
            crashMessage={crashMessage}
          />
          {crashMessage === undefined && (
            <Home headerHeight={headerHeight} valgtAvdelingEnhet={valgtAvdelingEnhet} />
          )}
        </LanguageProvider>
      </AppConfigResolver>
    </ErrorBoundary>
  );
};

export default AppIndex;
