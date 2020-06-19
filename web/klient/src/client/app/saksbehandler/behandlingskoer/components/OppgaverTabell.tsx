import React, {
  useState, useRef, ReactNode, FunctionComponent, useCallback, useMemo, useEffect,
} from 'react';
import { FormattedMessage, injectIntl, WrappedComponentProps } from 'react-intl';
import { Normaltekst, Element } from 'nav-frontend-typografi';
import NavFrontendChevron from 'nav-frontend-chevron';

import TimeoutError from 'data/rest-api/src/requestApi/error/TimeoutError';
import { RestApiPathsKeys } from 'data/restApiPaths';
import { useRestApiRunner } from 'data/rest-api-hooks';
import { getDateAndTime } from 'utils/dateUtils';
import Image from 'sharedComponents/Image';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import Oppgave from 'saksbehandler/oppgaveTsType';
import OppgaveStatus from 'saksbehandler/oppgaveStatusTsType';
import Table from 'sharedComponents/table/Table';
import TableRow from 'sharedComponents/table/TableRow';
import TableColumn from 'sharedComponents/table/TableColumn';
import DateLabel from 'sharedComponents/DateLabel';
import menuIconBlueUrl from 'images/ic-menu-18px_blue.svg';
import menuIconBlackUrl from 'images/ic-menu-18px_black.svg';
import bubbletextUrl from 'images/bubbletext.svg';
import bubbletextFilledUrl from 'images/bubbletext_filled.svg';
import BehandlingPollingTimoutModal from './BehandlingPollingTimoutModal';
import OppgaveHandlingerMenu from './menu/OppgaveHandlingerMenu';

import styles from './oppgaverTabell.less';

const headerTextCodes = [
  'OppgaverTabell.Soker',
  'OppgaverTabell.Behandlingstype',
  'OppgaverTabell.BehandlingOpprettet',
  'OppgaverTabell.FristForBehandling',
  'EMPTY_1',
  'EMPTY_2',
];

const EMPTY_ARRAY = [];

type OppgaveMedReservertIndikator = Oppgave & { underBehandling?: boolean };

const slaSammenOgMarkerReserverte = (reserverteOppgaver, oppgaverTilBehandling): OppgaveMedReservertIndikator[] => {
  const markedAsUnderBehandling = reserverteOppgaver
    .filter((reservertOppgave) => !oppgaverTilBehandling.some((oppgave) => oppgave.id === reservertOppgave.id))
    .map((f) => ({
      ...f,
      underBehandling: true,
    }));

  return markedAsUnderBehandling.concat(oppgaverTilBehandling.slice(0, 3));
};

const getToggleMenuEvent = (oppgave: OppgaveMedReservertIndikator, toggleMenu) => (oppgave.underBehandling ? () => toggleMenu(oppgave) : undefined);

interface OwnProps {
  reserverOppgave: (oppgave: Oppgave) => void;
  antallOppgaver?: number;
  valgtSakslisteId: number;
  doPolling?: boolean;
}

/**
 * OppgaverTabell
 */
export const OppgaverTabell: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  reserverOppgave,
  antallOppgaver = 0,
  valgtSakslisteId,
  doPolling = true,
}) => {
  const [showMenu, setShowMenu] = useState(false);
  const [valgtOppgaveId, setValgtOppgaveId] = useState<number>();
  const [offset, setOffset] = useState({
    left: 0,
    top: 0,
  });

  const { startRequest: forlengOppgavereservasjon } = useRestApiRunner<Oppgave[]>(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON);

  const { startRequest: hentReserverteOppgaver, data: reserverteOppgaver = EMPTY_ARRAY } = useRestApiRunner<Oppgave[]>(RestApiPathsKeys.RESERVERTE_OPPGAVER);

  const {
    startRequest: hentOppgaverTilBehandling, cancelRequest, data: oppgaverTilBehandling = EMPTY_ARRAY, error: hentOppgaverTilBehandlingError,
  } = useRestApiRunner<Oppgave[] | string>(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING);

  const fetchSakslisteOppgaverPolling = (oppgaveIder?: string) => {
    hentReserverteOppgaver({}, true);
    hentOppgaverTilBehandling(oppgaveIder ? { sakslisteId: valgtSakslisteId, oppgaveIder } : { sakslisteId: valgtSakslisteId }, true)
      .then((response) => (typeof response === 'string' || !doPolling
        ? Promise.resolve()
        : fetchSakslisteOppgaverPolling(response.map((o) => o.id).join(','))))
      .catch(() => undefined);
  };

  useEffect(() => {
    fetchSakslisteOppgaverPolling();

    return () => {
      cancelRequest();
    };
  }, [valgtSakslisteId]);

  const forlengOppgaveReservasjonFn = useCallback((oppgaveId: number): Promise<any> => forlengOppgavereservasjon({ oppgaveId })
    .then(() => hentReserverteOppgaver({}, true)), []);

  const ref = useRef({});

  const goToFagsak = useCallback((event: Event, _id: number, oppgave: Oppgave) => {
    if (ref.current && Object.keys(ref.current).some((key) => ref.current[key] && ref.current[key].contains(event.target))) {
      return;
    }
    reserverOppgave(oppgave);
  }, [ref.current]);

  const toggleMenu = useCallback((valgtOppgave: Oppgave) => {
    const newOffset = ref.current[valgtOppgave.id].getBoundingClientRect();
    setShowMenu(!showMenu);
    setValgtOppgaveId(valgtOppgave.id);
    setOffset({ top: newOffset.top, left: newOffset.left });
  }, [ref.current, showMenu]);

  const createTooltip = useCallback((oppgaveStatus: OppgaveStatus): ReactNode | undefined => {
    const { flyttetReservasjon } = oppgaveStatus;
    if (!flyttetReservasjon) {
      return undefined;
    }
    const datoOgTid = getDateAndTime(flyttetReservasjon.tidspunkt);
    const textValues = {
      dato: datoOgTid.date,
      tid: datoOgTid.time,
      uid: flyttetReservasjon.uid,
      navn: flyttetReservasjon.navn,
      beskrivelse: flyttetReservasjon.begrunnelse,
      br: <br />,
    };
    return (
      <Normaltekst><FormattedMessage id="OppgaverTabell.OverfortReservasjonTooltip" values={textValues} /></Normaltekst>
    );
  }, []);

  const alleOppgaver = useMemo(() => slaSammenOgMarkerReserverte(reserverteOppgaver, oppgaverTilBehandling), [reserverteOppgaver, oppgaverTilBehandling]);
  const valgtOppgave = reserverteOppgaver.find((o) => o.id === valgtOppgaveId);

  return (
    <>
      {hentOppgaverTilBehandlingError instanceof TimeoutError
        && <BehandlingPollingTimoutModal />}
      <Element><FormattedMessage id="OppgaverTabell.DineNesteSaker" values={{ antall: antallOppgaver }} /></Element>
      {alleOppgaver.length === 0 && (
        <>
          <VerticalSpacer eightPx />
          <Normaltekst><FormattedMessage id="OppgaverTabell.IngenOppgaver" /></Normaltekst>
        </>
      )}
      {alleOppgaver.length > 0 && (
        <>
          <Table headerTextCodes={headerTextCodes}>
            {alleOppgaver.map((oppgave) => (
              <TableRow
                key={oppgave.id}
                onMouseDown={goToFagsak}
                onKeyDown={goToFagsak}
                className={oppgave.underBehandling ? styles.isUnderBehandling : undefined}
                model={oppgave}
              >
                <TableColumn>{oppgave.navn ? `${oppgave.navn} ${oppgave.personnummer}` : '<navn>'}</TableColumn>
                <TableColumn>{oppgave.behandlingstype.navn}</TableColumn>
                <TableColumn>{oppgave.opprettetTidspunkt && <DateLabel dateString={oppgave.opprettetTidspunkt} />}</TableColumn>
                <TableColumn>{oppgave.behandlingsfrist && <DateLabel dateString={oppgave.behandlingsfrist} />}</TableColumn>
                <TableColumn>
                  {oppgave.status.flyttetReservasjon && (
                  <Image
                    src={bubbletextUrl}
                    srcHover={bubbletextFilledUrl}
                    alt={intl.formatMessage({ id: 'OppgaverTabell.OverfortReservasjon' })}
                    tooltip={createTooltip(oppgave.status)}
                  />
                  )}
                </TableColumn>
                <TableColumn className={oppgave.underBehandling ? styles.noPadding : undefined}>
                  {!oppgave.underBehandling && <NavFrontendChevron /> }
                  {oppgave.underBehandling && (
                    <div ref={(el) => { ref.current = { ...ref.current, [oppgave.id]: el }; }}>
                      <Image
                        className={styles.image}
                        src={menuIconBlackUrl}
                        srcHover={menuIconBlueUrl}
                        alt={intl.formatMessage({ id: 'OppgaverTabell.OppgaveHandlinger' })}
                        onMouseDown={getToggleMenuEvent(oppgave, toggleMenu)}
                        onKeyDown={getToggleMenuEvent(oppgave, toggleMenu)}
                      />
                    </div>
                  ) }
                </TableColumn>
              </TableRow>
            ))}
          </Table>
          {showMenu && valgtOppgaveId && valgtOppgave && (
            <OppgaveHandlingerMenu
              imageNode={ref.current[valgtOppgaveId]}
              toggleMenu={toggleMenu}
              offset={offset}
              oppgave={valgtOppgave}
              forlengOppgaveReservasjon={forlengOppgaveReservasjonFn}
              hentReserverteOppgaver={hentReserverteOppgaver}
            />
          )}
        </>
      )}
    </>
  );
};

export default injectIntl(OppgaverTabell);
