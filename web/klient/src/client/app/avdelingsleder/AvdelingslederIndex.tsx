import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { FormattedMessage } from 'react-intl';
import { createSelector } from 'reselect';
import classnames from 'classnames/bind';
import { NavLink } from 'react-router-dom';
import { Panel } from 'nav-frontend-paneler';
import Tabs from 'nav-frontend-tabs';
import { Undertittel } from 'nav-frontend-typografi';

import LoadingPanel from 'sharedComponents/LoadingPanel';
import {
  getValgtAvdelingEnhet, getNavAnsattKanOppgavestyre, getNavAnsattKanBehandleKode6, getAvdelingeneTilAvdelingslederResultat,
} from 'app/duck';
import { parseQueryString } from 'utils/urlUtils';
import { getAvdelingslederPanelLocationCreator } from 'app/paths';
import trackRouteParam from 'app/data/trackRouteParam';
import { Location } from 'app/locationTsType';
import { getSelectedAvdelingslederPanel, setSelectedAvdelingslederPanel } from './duck';
import AvdelingslederDashboard from './components/AvdelingslederDashboard';
import IkkeTilgangTilAvdelingslederPanel from './components/IkkeTilgangTilAvdelingslederPanel';
import IkkeTilgangTilKode6AvdelingPanel from './components/IkkeTilgangTilKode6AvdelingPanel';
import AvdelingslederPanels from './avdelingslederPanels';
import NokkeltallIndex from './nokkeltall/NokkeltallIndex';
import EndreSaksbehandlereIndex from './saksbehandlere/EndreSaksbehandlereIndex';
import EndreBehandlingskoerIndex from './behandlingskoer/EndreBehandlingskoerIndex';

import styles from './avdelingslederIndex.less';
import { ReservasjonerIndex } from './reservasjoner/ReservasjonerIndex';

const classNames = classnames.bind(styles);

const renderAvdelingslederPanel = (avdelingslederPanel) => {
  switch (avdelingslederPanel) {
    case AvdelingslederPanels.BEHANDLINGSKOER:
      return <EndreBehandlingskoerIndex />;
    case AvdelingslederPanels.SAKSBEHANDLERE:
      return <EndreSaksbehandlereIndex />;
    case AvdelingslederPanels.NOKKELTALL:
      return <NokkeltallIndex />;
    case AvdelingslederPanels.RESERVASJONER:
      return <ReservasjonerIndex />;
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

interface TsProps {
  valgtAvdelingEnhet?: string;
  activeAvdelingslederPanel: string;
  getAvdelingslederPanelLocation: (panel: string) => Location;
  kanOppgavestyre?: boolean;
  kanBehandleKode6?: boolean;
  erKode6Avdeling?: boolean;
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
export const AvdelingslederIndex = ({
  valgtAvdelingEnhet,
  activeAvdelingslederPanel,
  getAvdelingslederPanelLocation,
  kanOppgavestyre,
  kanBehandleKode6,
  erKode6Avdeling,
}: TsProps) => {
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
            {renderAvdelingslederPanel(activeAvdelingslederPanel)}
          </Panel>
        </div>
      </AvdelingslederDashboard>
    );
  }
  return <LoadingPanel />;
};

AvdelingslederIndex.propTypes = {
  valgtAvdelingEnhet: PropTypes.string,
  activeAvdelingslederPanel: PropTypes.string.isRequired,
  getAvdelingslederPanelLocation: PropTypes.func.isRequired,
  kanOppgavestyre: PropTypes.bool,
  kanBehandleKode6: PropTypes.bool,
  erKode6Avdeling: PropTypes.bool,
};

AvdelingslederIndex.defaultProps = {
  valgtAvdelingEnhet: undefined,
  kanOppgavestyre: false,
  kanBehandleKode6: false,
  erKode6Avdeling: false,
};

const getPanelFromUrlOrDefault = (location) => {
  const panelFromUrl = parseQueryString(location.search);
  return panelFromUrl.avdelingsleder ? panelFromUrl.avdelingsleder : AvdelingslederPanels.BEHANDLINGSKOER;
};

export const erKode6Avdeling = createSelector([getValgtAvdelingEnhet, getAvdelingeneTilAvdelingslederResultat],
  (valgtAvdelingEnhet, avdelinger = []) => {
    const avdeling = avdelinger instanceof Array && avdelinger.find(a => a.avdelingEnhet === valgtAvdelingEnhet);
    return avdeling ? avdeling.kreverKode6 : false;
  });

const mapStateToProps = state => ({
  valgtAvdelingEnhet: getValgtAvdelingEnhet(state),
  activeAvdelingslederPanel: getSelectedAvdelingslederPanel(state),
  kanOppgavestyre: getNavAnsattKanOppgavestyre(state),
  kanBehandleKode6: getNavAnsattKanBehandleKode6(state),
  erKode6Avdeling: erKode6Avdeling(state),
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
  paramPropType: PropTypes.string,
  storeParam: setSelectedAvdelingslederPanel,
  getParamFromStore: getSelectedAvdelingslederPanel,
  isQueryParam: true,
})(connect(mapStateToProps, null, mergeProps)(AvdelingslederIndex));
