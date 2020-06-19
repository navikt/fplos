import React, { Component, ReactNode } from 'react';
import { withRouter, match } from 'react-router-dom';
import moment from 'moment';
import { Location, History } from 'history';

import { parseQueryString } from 'utils/urlUtils';
import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import AppConfigResolver from './AppConfigResolver';
import LanguageProvider from './LanguageProvider';
import HeaderWithErrorPanel from './components/HeaderWithErrorPanel';
import Home from './components/Home';
import { RestApiStateContext } from '../data/rest-api-hooks';

import '../../styles/global.less';

interface RouterProps {
  location: Location;
  history: History;
  match: match;
}

interface StateProps {
  headerHeight: number;
  valgtAvdelingEnhet?: string;
  crashMessage?: string;
}

/**
 * AppIndex
 *
 * Container komponent. Dette er toppkomponenten i applikasjonen. Denne vil rendre header
 * og home-komponentene. Home-komponenten vil rendre barn-komponenter via ruter.
 * Komponenten er også ansvarlig for å hente innlogget NAV-ansatt, rettskilde-url, systemrutine-url
 * og kodeverk fra server og lagre desse i klientens state.
 */
export class AppIndex extends Component<RouterProps, StateProps> {
  static contextType = RestApiStateContext;

  state = {
    headerHeight: 0,
    valgtAvdelingEnhet: undefined,
    crashMessage: undefined,
  };

  componentDidUpdate = (): void => {
    const state = this.context;
    const navAnsatt = state[RestApiGlobalStatePathsKeys.NAV_ANSATT];
    const funksjonellTid = navAnsatt ? navAnsatt.funksjonellTid : undefined;

    if (funksjonellTid) {
      // TODO (TOR) Dette endrar jo berre moment. Kva med kode som brukar Date direkte?
      const diffInMinutes = moment().diff(funksjonellTid, 'minutes');
      // Hvis diffInMinutes har avvik på over 5min: override moment.now (ref. http://momentjs.com/docs/#/customization/now/)
      if (diffInMinutes >= 5 || diffInMinutes <= -5) {
        const diff = moment().diff(funksjonellTid);
        moment.now = () => Date.now() - diff;
      }
    }
  }

  componentDidCatch = (error: Error, info: { componentStack: string }): void => {
    const crashMessage = [
      error.toString(),
      info.componentStack
        .split('\n')
        .map((line: string) => line.trim())
        .find((line: string) => !!line),
    ].join(' ');

    this.setState((state) => ({ ...state, crashMessage }));
  }

  setValgtAvdelingEnhet = (valgtAvdelingEnhet: string) => {
    this.setState((state) => ({ ...state, valgtAvdelingEnhet }));
  }

  setSiteHeight = (headerHeight: number): void => {
    document.documentElement.setAttribute('style', `height: calc(100% - ${headerHeight}px)`);
    this.setState((state) => ({ ...state, headerHeight }));
  }

  render = (): ReactNode => {
    const {
      location,
    } = this.props;
    const {
      crashMessage,
    } = this.state;
    const { headerHeight, valgtAvdelingEnhet } = this.state;
    const queryStrings = parseQueryString(location.search);

    return (
      <AppConfigResolver>
        <LanguageProvider>
          <HeaderWithErrorPanel
            queryStrings={queryStrings}
            setSiteHeight={this.setSiteHeight}
            locationPathname={location.pathname}
            setValgtAvdelingEnhet={this.setValgtAvdelingEnhet}
            valgtAvdelingEnhet={valgtAvdelingEnhet}
            crashMessage={crashMessage}
          />
          {!crashMessage && (
            <Home headerHeight={headerHeight} valgtAvdelingEnhet={valgtAvdelingEnhet} />
          )}
        </LanguageProvider>
      </AppConfigResolver>
    );
  }
}

export default withRouter(AppIndex);
