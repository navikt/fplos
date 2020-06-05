import React, { Component, ReactNode } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';
import { withRouter } from 'react-router-dom';
import moment from 'moment';

import Avdeling from 'app/avdelingTsType';
import { parseQueryString } from 'utils/urlUtils';
import EventType from 'data/rest-api/src/requestApi/eventType';
import errorHandler from 'data/error-api-redux';
import { RestApiPathsKeys } from 'data/restApiPaths';
import AppConfigResolver from './AppConfigResolver';
import { AVDELINGSLEDER_PATH } from './paths';
import {
  setAvdelingEnhet, getValgtAvdelingEnhet,
  fetchAvdelingeneTilAvdelingsleder, getAvdelingeneTilAvdelingslederResultat,
  resetAvdelingeneTilAvdelingslederData, resetAvdelingEnhet,
} from './duck';
import Location from './locationTsType';
import LanguageProvider from './LanguageProvider';
import HeaderWithErrorPanel from './components/HeaderWithErrorPanel';
import Home from './components/Home';
import { RestDataContext } from '../data/RestDataContext';

import '../../styles/global.less';

interface OwnProps {
  errorMessages?: {
    type: EventType;
    code?: string;
    params?: {
      errorDetails?: string;
    };
    text?: string;
  }[];
  removeErrorMessage: () => void;
  crashMessage: string;
  showCrashMessage: (message: string) => void;
  navAnsattName: string;
  location: Location;
  fetchAvdelingeneTilAvdelingsleder: () => void;
  resetAvdelingeneTilAvdelingslederData: () => void;
  setAvdelingEnhet: (avdelingEnhet: string) => void;
  resetAvdelingEnhet: () => void;
  avdelinger?: Avdeling[];
  valgtAvdelingEnhet?: string;
}

/**
 * AppIndex
 *
 * Container komponent. Dette er toppkomponenten i applikasjonen. Denne vil rendre header
 * og home-komponentene. Home-komponenten vil rendre barn-komponenter via ruter.
 * Komponenten er også ansvarlig for å hente innlogget NAV-ansatt, rettskilde-url, systemrutine-url
 * og kodeverk fra server og lagre desse i klientens state.
 */
export class AppIndex extends Component<OwnProps> {
  static defaultProps = {
    crashMessage: '',
    navAnsattName: '',
    avdelinger: [],
    valgtAvdelingEnhet: undefined,
    kanOppgavestyre: false,
    errorMessages: [],
  };

  static contextType = RestDataContext;

  state = {
    headerHeight: 0,
  };

  fetchAvdelinger = (): void => {
    const {
      location,
      avdelinger,
      fetchAvdelingeneTilAvdelingsleder: fetchAvdelinger,
      resetAvdelingEnhet: resetAvdeling,
      resetAvdelingeneTilAvdelingslederData: resetAvdelingene,
    } = this.props;

    const { state } = this.context;
    const navAnsatt = state[RestApiPathsKeys.NAV_ANSATT];

    const kanOppgavestyre = navAnsatt ? navAnsatt.kanOppgavestyre : undefined;

    const harAvdelinger = avdelinger && avdelinger.length > 0;
    if (kanOppgavestyre && !harAvdelinger && location.pathname && location.pathname.includes(AVDELINGSLEDER_PATH)) {
      fetchAvdelinger();
    } else if (harAvdelinger && location.pathname && !location.pathname.includes(AVDELINGSLEDER_PATH)) {
      resetAvdeling();
      resetAvdelingene();
    }
  }

  componentDidMount = (): void => {
    this.fetchAvdelinger();
  }

  componentDidUpdate = (): void => {
    const { state } = this.context;
    const navAnsatt = state[RestApiPathsKeys.NAV_ANSATT];
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

    this.fetchAvdelinger();
  }

  componentDidCatch = (error: Error, info: { componentStack: string }): void => {
    const { showCrashMessage: showCrashMsg } = this.props;
    showCrashMsg([
      error.toString(),
      info.componentStack
        .split('\n')
        .map((line: string) => line.trim())
        .find((line: string) => !!line),
    ].join(' '));
  }

  setSiteHeight = (headerHeight: number): void => {
    document.documentElement.setAttribute('style', `height: calc(100% - ${headerHeight}px)`);
    this.setState((state) => ({ ...state, headerHeight }));
  }

  render = (): ReactNode => {
    const {
      location, crashMessage, errorMessages,
      removeErrorMessage: removeErrorMsg, avdelinger, setAvdelingEnhet: setAvdeling, valgtAvdelingEnhet,
    } = this.props;
    const { headerHeight } = this.state;
    const queryStrings = parseQueryString(location.search);

    return (
      <AppConfigResolver>
        <LanguageProvider>
          <HeaderWithErrorPanel
            queryStrings={queryStrings}
            removeErrorMessage={removeErrorMsg}
            avdelinger={avdelinger}
            setValgtAvdeling={setAvdeling}
            valgtAvdelingEnhet={valgtAvdelingEnhet}
            errorMessages={errorMessages}
            setSiteHeight={this.setSiteHeight}
          />
          {!crashMessage && (
            <Home headerHeight={headerHeight} />
          )}
        </LanguageProvider>
      </AppConfigResolver>
    );
  }
}

const mapStateToProps = (state: any) => ({
  errorMessages: errorHandler.getAllErrorMessages(state),
  crashMessage: errorHandler.getCrashMessage(state),
  avdelinger: getAvdelingeneTilAvdelingslederResultat(state),
  valgtAvdelingEnhet: getValgtAvdelingEnhet(state),
});

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({
  showCrashMessage: errorHandler.showCrashMessage,
  removeErrorMessage: errorHandler.removeErrorMessage,
  fetchAvdelingeneTilAvdelingsleder,
  setAvdelingEnhet,
  resetAvdelingEnhet,
  resetAvdelingeneTilAvdelingslederData,
}, dispatch);

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(AppIndex));
