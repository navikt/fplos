import React, { FunctionComponent, useMemo, useEffect } from 'react';
import { connect } from 'react-redux';
import { FormattedMessage } from 'react-intl';
import classnames from 'classnames/bind';
import { NavLink } from 'react-router-dom';
import Panel from 'nav-frontend-paneler';
import Tabs from 'nav-frontend-tabs';
import { Undertittel } from 'nav-frontend-typografi';

import { RestApiPathsKeys, RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import LoadingPanel from 'sharedComponents/LoadingPanel';
import { parseQueryString } from 'utils/urlUtils';
import { getAvdelingslederPanelLocationCreator } from 'app/paths';
import trackRouteParam from 'app/data/trackRouteParam';
import Location from 'app/locationTsType';
import useRestApiData from 'data/rest-api-hooks/useGlobalStateRestApiData';
import NavAnsatt from 'app/navAnsattTsType';
import Avdeling from 'app/avdelingTsType';
import useRestApiRunner from 'data/rest-api-hooks/useRestApiRunner';
import { getSelectedAvdelingslederPanel, setSelectedAvdelingslederPanel } from './duck';
import AvdelingslederDashboard from './components/AvdelingslederDashboard';
import IkkeTilgangTilAvdelingslederPanel from './components/IkkeTilgangTilAvdelingslederPanel';
import IkkeTilgangTilKode6AvdelingPanel from './components/IkkeTilgangTilKode6AvdelingPanel';
import AvdelingslederPanels from './avdelingslederPanels';
import NokkeltallIndex from './nokkeltall/NokkeltallIndex';
import EndreSaksbehandlereIndex from './saksbehandlere/EndreSaksbehandlereIndex';
import EndreBehandlingskoerIndex from './behandlingskoer/EndreBehandlingskoerIndex';

import styles from './avdelingslederIndex.less';
import ReservasjonerIndex from './reservasjoner/ReservasjonerIndex';
import Saksbehandler from './saksbehandlere/saksbehandlerTsType';

const EMPTY_ARRAY = [];

const classNames = classnames.bind(styles);

const renderAvdelingslederPanel = (avdelingslederPanel, valgtAvdelingEnhet, hentAvdelingensSaksbehandlere, avdelingensSaksbehandlere) => {
  switch (avdelingslederPanel) {
    case AvdelingslederPanels.BEHANDLINGSKOER:
      return <EndreBehandlingskoerIndex valgtAvdelingEnhet={valgtAvdelingEnhet} avdelingensSaksbehandlere={avdelingensSaksbehandlere} />;
    case AvdelingslederPanels.SAKSBEHANDLERE:
      return (
        <EndreSaksbehandlereIndex
          valgtAvdelingEnhet={valgtAvdelingEnhet}
          hentAvdelingensSaksbehandlere={hentAvdelingensSaksbehandlere}
          avdelingensSaksbehandlere={avdelingensSaksbehandlere}
        />
      );
    case AvdelingslederPanels.NOKKELTALL:
      return <NokkeltallIndex valgtAvdelingEnhet={valgtAvdelingEnhet} />;
    case AvdelingslederPanels.RESERVASJONER:
      return <ReservasjonerIndex valgtAvdelingEnhet={valgtAvdelingEnhet} />;
    default:
      return null;
  }
};

const messageId = {
  [AvdelingslederPanels.BEHANDLINGSKOER]: 'AvdelingslederIndex.Behandlingskoer',
  [AvdelingslederPanels.SAKSBEHANDLERE]: 'AvdelingslederIndex.Saksbehandlere',
  [AvdelingslederPanels.NOKKELTALL]: 'AvdelingslederIndex.Nokkeltall',
  [AvdelingslederPanels.RESERVASJONER]: 'AvdelingslederIndex.Reservasjoner',
};

interface OwnProps {
  valgtAvdelingEnhet?: string;
  activeAvdelingslederPanel: string;
  getAvdelingslederPanelLocation: (panel: string) => Location;
}

const getTab = (avdelingslederPanel, activeAvdelingslederPanel, getAvdelingslederPanelLocation) => ({
  label: (<Undertittel><FormattedMessage id={messageId[avdelingslederPanel]} /></Undertittel>),
  aktiv: avdelingslederPanel === activeAvdelingslederPanel,
  // eslint-disable-next-line react/prop-types
  linkCreator: ({ children, className }) => (
    <NavLink
      to={getAvdelingslederPanelLocation(avdelingslederPanel)}
      className={classNames(className, 'link', { isActive: activeAvdelingslederPanel === avdelingslederPanel })}
    >
      {children}
    </NavLink>
  ),
});

/**
 * AvdelingslederIndex
 */
export const AvdelingslederIndex: FunctionComponent<OwnProps> = ({
  valgtAvdelingEnhet,
  activeAvdelingslederPanel,
  getAvdelingslederPanelLocation,
}) => {
  const { kanOppgavestyre, kanBehandleKode6 } = useRestApiData<NavAnsatt>(RestApiGlobalStatePathsKeys.NAV_ANSATT);
  const avdelinger = useRestApiData<Avdeling[]>(RestApiGlobalStatePathsKeys.AVDELINGER);

  const {
    startRequest: hentAvdelingensSaksbehandlere, data: avdelingensSaksbehandlere = EMPTY_ARRAY,
  } = useRestApiRunner<Saksbehandler[]>(RestApiPathsKeys.SAKSBEHANDLERE_FOR_AVDELING);

  useEffect(() => {
    hentAvdelingensSaksbehandlere({ avdelingEnhet: valgtAvdelingEnhet });
  }, []);


  const erKode6Avdeling = useMemo(() => {
    const avdeling = avdelinger instanceof Array && avdelinger.find((a) => a.avdelingEnhet === valgtAvdelingEnhet);
    return avdeling ? avdeling.kreverKode6 : false;
  }, [avdelinger, valgtAvdelingEnhet]);

  if (!kanOppgavestyre) {
    return <IkkeTilgangTilAvdelingslederPanel />;
  } if (erKode6Avdeling && !kanBehandleKode6) {
    return <IkkeTilgangTilKode6AvdelingPanel />;
  } if (valgtAvdelingEnhet) {
    return (
      <AvdelingslederDashboard key={valgtAvdelingEnhet}>
        <div>
          <Tabs tabs={[
            getTab(AvdelingslederPanels.BEHANDLINGSKOER, activeAvdelingslederPanel, getAvdelingslederPanelLocation),
            getTab(AvdelingslederPanels.NOKKELTALL, activeAvdelingslederPanel, getAvdelingslederPanelLocation),
            getTab(AvdelingslederPanels.SAKSBEHANDLERE, activeAvdelingslederPanel, getAvdelingslederPanelLocation),
            getTab(AvdelingslederPanels.RESERVASJONER, activeAvdelingslederPanel, getAvdelingslederPanelLocation),
          ]}
          />
          <Panel className={styles.panelPadding}>
            {renderAvdelingslederPanel(activeAvdelingslederPanel, valgtAvdelingEnhet, hentAvdelingensSaksbehandlere, avdelingensSaksbehandlere)}
          </Panel>
        </div>
      </AvdelingslederDashboard>
    );
  }
  return <LoadingPanel />;
};

const getPanelFromUrlOrDefault = (location) => {
  const panelFromUrl = parseQueryString(location.search);
  return panelFromUrl.avdelingsleder ? panelFromUrl.avdelingsleder : AvdelingslederPanels.BEHANDLINGSKOER;
};

const mapStateToProps = (state) => ({
  activeAvdelingslederPanel: getSelectedAvdelingslederPanel(state),
});

const mergeProps = (stateProps, dispatchProps, ownProps) => ({
  ...ownProps,
  ...dispatchProps,
  ...stateProps,
  getAvdelingslederPanelLocation: getAvdelingslederPanelLocationCreator(ownProps.location), // gets prop 'location' from trackRouteParam
  activeAvdelingslederPanel: stateProps.activeAvdelingslederPanel ? stateProps.activeAvdelingslederPanel : getPanelFromUrlOrDefault(ownProps.location),
});

export default trackRouteParam({
  paramName: 'fane',
  storeParam: setSelectedAvdelingslederPanel,
  getParamFromStore: getSelectedAvdelingslederPanel,
  isQueryParam: true,
})(connect(mapStateToProps, null, mergeProps)(AvdelingslederIndex));
