import React, {
  FunctionComponent, useMemo, useEffect, useCallback, ReactElement,
} from 'react';
import { FormattedMessage } from 'react-intl';
import classnames from 'classnames/bind';
import { NavLink } from 'react-router-dom';
import { Location } from 'history';
import Panel from 'nav-frontend-paneler';
import Tabs from 'nav-frontend-tabs';
import { Undertittel } from 'nav-frontend-typografi';

import { restApiHooks, RestApiPathsKeys, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import LoadingPanel from 'sharedComponents/LoadingPanel';
import { parseQueryString } from 'utils/urlUtils';
import { getAvdelingslederPanelLocationCreator } from 'app/paths';
import NavAnsatt from 'types/navAnsattTsType';
import Avdeling from 'types/avdelingsleder/avdelingTsType';
import Saksbehandler from 'types/avdelingsleder/saksbehandlerTsType';
import useTrackRouteParam from 'app/data/useTrackRouteParam';
import AvdelingslederDashboard from './components/AvdelingslederDashboard';
import IkkeTilgangTilAvdelingslederPanel from './components/IkkeTilgangTilAvdelingslederPanel';
import IkkeTilgangTilKode6AvdelingPanel from './components/IkkeTilgangTilKode6AvdelingPanel';
import AvdelingslederPanels from './avdelingslederPanels';
import NokkeltallIndex from './nokkeltall/NokkeltallIndex';
import EndreSaksbehandlereIndex from './saksbehandlere/EndreSaksbehandlereIndex';
import EndreBehandlingskoerIndex from './behandlingskoer/EndreBehandlingskoerIndex';

import styles from './avdelingslederIndex.less';
import ReservasjonerIndex from './reservasjoner/ReservasjonerIndex';

const EMPTY_ARRAY: Saksbehandler[] = [];

const classNames = classnames.bind(styles);

const renderAvdelingslederPanel = (
  avdelingslederPanel: string,
  valgtAvdelingEnhet: string,
  hentAvdelingensSaksbehandlere: (params: {avdelingEnhet: string}) => void,
  avdelingensSaksbehandlere: Saksbehandler[],
) => {
  switch (avdelingslederPanel) {
    case AvdelingslederPanels.BEHANDLINGSKOER:
      return (
        <EndreBehandlingskoerIndex
          valgtAvdelingEnhet={valgtAvdelingEnhet}
          avdelingensSaksbehandlere={avdelingensSaksbehandlere}
        />
      );
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
}

const getTab = (
  avdelingslederPanel: string,
  activeAvdelingslederPanel: string,
  getAvdelingslederPanelLocation: (avdelingslederPanel: string) => any,
) => ({
  label: (<Undertittel><FormattedMessage id={messageId[avdelingslederPanel]} /></Undertittel>),
  aktiv: avdelingslederPanel === activeAvdelingslederPanel,
  // eslint-disable-next-line react/prop-types
  linkCreator: ({ children, className }: { children: ReactElement, className: string }) => (
    <NavLink
      to={getAvdelingslederPanelLocation(avdelingslederPanel)}
      className={classNames(className, 'link', { isActive: activeAvdelingslederPanel === avdelingslederPanel })}
    >
      {children}
    </NavLink>
  ),
});

const getPanelFromUrlOrDefault = (location: Location) => {
  const panelFromUrl = parseQueryString(location.search);
  return panelFromUrl.avdelingsleder ? panelFromUrl.avdelingsleder : AvdelingslederPanels.BEHANDLINGSKOER;
};

/**
 * AvdelingslederIndex
 */
export const AvdelingslederIndex: FunctionComponent<OwnProps> = ({
  valgtAvdelingEnhet,
}) => {
  const { selected: activeAvdelingslederPanelTemp, location } = useTrackRouteParam<string>({
    paramName: 'fane',
    isQueryParam: true,
  });

  const { kanOppgavestyre, kanBehandleKode6 } = restApiHooks.useGlobalStateRestApiData<NavAnsatt>(RestApiGlobalStatePathsKeys.NAV_ANSATT);
  const avdelinger = restApiHooks.useGlobalStateRestApiData<Avdeling[]>(RestApiGlobalStatePathsKeys.AVDELINGER);

  const {
    startRequest: hentAvdelingensSb, data: avdelingensSaksbehandlere = EMPTY_ARRAY,
  } = restApiHooks.useRestApiRunner<Saksbehandler[]>(RestApiPathsKeys.SAKSBEHANDLERE_FOR_AVDELING);
  const hentAvdelingensSaksbehandlere = useCallback((params) => hentAvdelingensSb(params, true), []);

  useEffect(() => {
    if (valgtAvdelingEnhet) {
      hentAvdelingensSaksbehandlere({ avdelingEnhet: valgtAvdelingEnhet });
    }
  }, [valgtAvdelingEnhet]);

  const getAvdelingslederPanelLocation = getAvdelingslederPanelLocationCreator(location);
  const activeAvdelingslederPanel = activeAvdelingslederPanelTemp || getPanelFromUrlOrDefault(location);

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

export default AvdelingslederIndex;
