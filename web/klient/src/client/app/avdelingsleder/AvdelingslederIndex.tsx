import React, {
  FunctionComponent, useMemo, useEffect, useCallback,
} from 'react';
import { FormattedMessage } from 'react-intl';
import { useNavigate } from 'react-router-dom';
import { Location } from 'history';
import { Heading, Panel, Tabs } from '@navikt/ds-react';

import { restApiHooks, RestApiPathsKeys, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { LoadingPanel } from '@navikt/ft-ui-komponenter';
import { parseQueryString } from '@navikt/ft-utils';
import { getAvdelingslederPanelLocationCreator } from 'app/paths';
import Saksbehandler from 'types/avdelingsleder/saksbehandlerAvdelingTsType';
import useTrackRouteParam from 'app/data/useTrackRouteParam';
import AvdelingslederDashboard from './components/AvdelingslederDashboard';
import IkkeTilgangTilAvdelingslederPanel from './components/IkkeTilgangTilAvdelingslederPanel';
import IkkeTilgangTilKode6AvdelingPanel from './components/IkkeTilgangTilKode6AvdelingPanel';
import AvdelingslederPanels from './avdelingslederPanels';
import NokkeltallIndex from './nokkeltall/NokkeltallIndex';
import EndreSaksbehandlereIndex from './saksbehandlere/EndreSaksbehandlereIndex';
import EndreBehandlingskoerIndex from './behandlingskoer/EndreBehandlingskoerIndex';
import ReservasjonerIndex from './reservasjoner/ReservasjonerIndex';

const EMPTY_ARRAY: Saksbehandler[] = [];

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

const getPanelFromUrlOrDefault = (location: Location) => {
  const panelFromUrl = parseQueryString(location.search);
  return panelFromUrl.avdelingsleder ? panelFromUrl.avdelingsleder : AvdelingslederPanels.BEHANDLINGSKOER;
};

/**
 * AvdelingslederIndex
 */
const AvdelingslederIndex: FunctionComponent<OwnProps> = ({
  valgtAvdelingEnhet,
}) => {
  const { selected: activeAvdelingslederPanelTemp, location } = useTrackRouteParam<string>({
    paramName: 'fane',
    isQueryParam: true,
  });

  const { kanOppgavestyre, kanBehandleKode6 } = restApiHooks.useGlobalStateRestApiData(RestApiGlobalStatePathsKeys.NAV_ANSATT);
  const avdelinger = restApiHooks.useGlobalStateRestApiData(RestApiGlobalStatePathsKeys.AVDELINGER);

  const {
    startRequest: hentAvdelingensSb, data: avdelingensSaksbehandlere = EMPTY_ARRAY,
  } = restApiHooks.useRestApiRunner(RestApiPathsKeys.SAKSBEHANDLERE_FOR_AVDELING);
  const hentAvdelingensSaksbehandlere = useCallback((params: { avdelingEnhet: string }) => hentAvdelingensSb(params, true), []);

  useEffect(() => {
    if (valgtAvdelingEnhet) {
      hentAvdelingensSaksbehandlere({ avdelingEnhet: valgtAvdelingEnhet });
    }
  }, [valgtAvdelingEnhet]);

  const getAvdelingslederPanelLocation = getAvdelingslederPanelLocationCreator(location);
  const activeAvdelingslederPanel = activeAvdelingslederPanelTemp || getPanelFromUrlOrDefault(location);

  const navigate = useNavigate();

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
          <Tabs
            size="small"
            value={activeAvdelingslederPanel}
            onChange={(avdelingslederPanel: string) => { navigate(getAvdelingslederPanelLocation(avdelingslederPanel)); }}
          >
            <Tabs.List>
              <Tabs.Tab
                value={AvdelingslederPanels.BEHANDLINGSKOER}
                label={<Heading size="small"><FormattedMessage id={messageId[AvdelingslederPanels.BEHANDLINGSKOER]} /></Heading>}
              />
              <Tabs.Tab
                value={AvdelingslederPanels.NOKKELTALL}
                label={<Heading size="small"><FormattedMessage id={messageId[AvdelingslederPanels.NOKKELTALL]} /></Heading>}
              />
              <Tabs.Tab
                value={AvdelingslederPanels.SAKSBEHANDLERE}
                label={<Heading size="small"><FormattedMessage id={messageId[AvdelingslederPanels.SAKSBEHANDLERE]} /></Heading>}
              />
              <Tabs.Tab
                value={AvdelingslederPanels.RESERVASJONER}
                label={<Heading size="small"><FormattedMessage id={messageId[AvdelingslederPanels.RESERVASJONER]} /></Heading>}
              />
            </Tabs.List>
          </Tabs>
          <Panel>
            {renderAvdelingslederPanel(activeAvdelingslederPanel, valgtAvdelingEnhet, hentAvdelingensSaksbehandlere, avdelingensSaksbehandlere)}
          </Panel>
        </div>
      </AvdelingslederDashboard>
    );
  }
  return <LoadingPanel />;
};

export default AvdelingslederIndex;
