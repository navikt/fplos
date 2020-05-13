import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';
import { withRouter } from 'react-router-dom';
import moment from 'moment';

import { Avdeling } from 'app/avdelingTsType';
import avdelingPropType from 'app/avdelingPropType';
import { parseQueryString } from 'utils/urlUtils';
import errorHandler from 'data/error-api-redux';
import AppConfigResolver from './AppConfigResolver';
import { AVDELINGSLEDER_PATH } from './paths';
import {
  getFunksjonellTid, getNavAnsattName, setAvdelingEnhet, getValgtAvdelingEnhet,
  fetchAvdelingeneTilAvdelingsleder, getAvdelingeneTilAvdelingslederResultat,
  resetAvdelingeneTilAvdelingslederData, resetAvdelingEnhet, getNavAnsattKanOppgavestyre,
} from './duck';
import { Location } from './locationTsType';
import LanguageProvider from './LanguageProvider';
import HeaderWithErrorPanel from './components/HeaderWithErrorPanel';
import Home from './components/Home';

import '../../nomodulestyles/global.less';

type TsProps = Readonly<{
  errorMessages?: {
    type: EventType;
    code?: string;
    params?: {
      errorDetails?: string;
    };
    text?: string;
  }[];
  errorMessagesLength: number;
  removeErrorMessage: () => void;
  crashMessage: string;
  showCrashMessage: (message: string) => void;
  navAnsattName: string;
  funksjonellTid?: string;
  location: Location;
  fetchAvdelingeneTilAvdelingsleder: () => void;
  resetAvdelingeneTilAvdelingslederData: () => void;
  setAvdelingEnhet: (avdelingEnhet: string) => void;
  resetAvdelingEnhet: () => void;
  avdelinger?: Avdeling[];
  valgtAvdelingEnhet?: string;
  kanOppgavestyre: boolean;
}>

/**
 * AppIndex
 *
 * Container komponent. Dette er toppkomponenten i applikasjonen. Denne vil rendre header
 * og home-komponentene. Home-komponenten vil rendre barn-komponenter via ruter.
 * Komponenten er også ansvarlig for å hente innlogget NAV-ansatt, rettskilde-url, systemrutine-url
 * og kodeverk fra server og lagre desse i klientens state.
 */
export class AppIndex extends Component<TsProps> {
  static defaultProps = {
    crashMessage: '',
    navAnsattName: '',
    funksjonellTid: undefined,
    avdelinger: [],
    valgtAvdelingEnhet: undefined,
    kanOppgavestyre: false,
    errorMessages: [],
  };

  state = {
    headerHeight: 0,
  };

  fetchAvdelinger = () => {
    const {
      location, fetchAvdelingeneTilAvdelingsleder: fetchAvdelinger, resetAvdelingEnhet: resetAvdeling, resetAvdelingeneTilAvdelingslederData: resetAvdelingene,
      kanOppgavestyre, avdelinger,
    } = this.props;

    const harAvdelinger = avdelinger && avdelinger.length > 0;
    if (kanOppgavestyre && !harAvdelinger && location.pathname && location.pathname.includes(AVDELINGSLEDER_PATH)) {
      fetchAvdelinger();
    } else if (harAvdelinger && location.pathname && !location.pathname.includes(AVDELINGSLEDER_PATH)) {
      resetAvdeling();
      resetAvdelingene();
    }
  }

  componentDidMount = () => {
    this.fetchAvdelinger();
  }

  componentDidUpdate = (prevProps: TsProps) => {
    const { funksjonellTid } = this.props;

    if (funksjonellTid && prevProps.funksjonellTid !== funksjonellTid) {
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

  componentDidCatch = (error: Error, info: any) => {
    const { showCrashMessage: showCrashMsg } = this.props;
    showCrashMsg([
      error.toString(),
      info.componentStack
        .split('\n')
        .map((line: string) => line.trim())
        .find((line: string) => !!line),
    ].join(' '));
  }

  setSiteHeight = (headerHeight) => {
    document.documentElement.setAttribute('style', `height: calc(100% - ${headerHeight}px)`);
    this.setState((state) => ({ ...state, headerHeight }));
  }

  render = () => {
    const {
      location, crashMessage, navAnsattName, errorMessages,
      removeErrorMessage: removeErrorMsg, avdelinger, setAvdelingEnhet: setAvdeling, valgtAvdelingEnhet,
    } = this.props;
    const { headerHeight } = this.state;
    const queryStrings = parseQueryString(location.search);

    return (
      <AppConfigResolver>
        <LanguageProvider>
          <HeaderWithErrorPanel
            queryStrings={queryStrings}
            navAnsattName={navAnsattName}
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
  navAnsattName: getNavAnsattName(state),
  funksjonellTid: getFunksjonellTid(state),
  avdelinger: getAvdelingeneTilAvdelingslederResultat(state),
  valgtAvdelingEnhet: getValgtAvdelingEnhet(state),
  kanOppgavestyre: getNavAnsattKanOppgavestyre(state),
});

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({
  showCrashMessage: errorHandler.showCrashMessage,
  removeErrorMessage: errorHandler.removeErrorMessage,
  fetchAvdelingeneTilAvdelingsleder,
  setAvdelingEnhet,
  resetAvdelingEnhet,
  resetAvdelingeneTilAvdelingslederData,
}, dispatch);

// @ts-ignore
export default withRouter(connect(mapStateToProps, mapDispatchToProps)(AppIndex));
