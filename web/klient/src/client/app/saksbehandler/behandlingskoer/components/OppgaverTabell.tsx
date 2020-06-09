import React, {
  useState, useRef, ReactNode, FunctionComponent, useCallback, useMemo,
} from 'react';
import { FormattedMessage, injectIntl, WrappedComponentProps } from 'react-intl';
import { Normaltekst, Element } from 'nav-frontend-typografi';
import NavFrontendChevron from 'nav-frontend-chevron';

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
  oppgaverTilBehandling: Oppgave[];
  reserverteOppgaver: Oppgave[];
  reserverOppgave: (oppgave: Oppgave) => void;
  opphevOppgaveReservasjon: (oppgaveId: number, begrunnelse: string) => Promise<any>;
  forlengOppgaveReservasjon: (oppgaveId: number) => Promise<any>;
  endreOppgaveReservasjon: (oppgaveId: number, reserverTil: string) => Promise<string>;
  flyttReservasjon: (oppgaveId: number, brukerident: string, begrunnelse: string) => Promise<string>;
  antallOppgaver?: number;
}

/**
 * OppgaverTabell
 */
export const OppgaverTabell: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  reserverOppgave,
  opphevOppgaveReservasjon,
  forlengOppgaveReservasjon,
  endreOppgaveReservasjon,
  flyttReservasjon,
  reserverteOppgaver,
  oppgaverTilBehandling,
  antallOppgaver = 0,
  intl,
}) => {
  const [showMenu, setShowMenu] = useState(false);
  const [valgtOppgaveId, setValgtOppgaveId] = useState<number>();
  const [offset, setOffset] = useState({
    left: 0,
    top: 0,
  });

  const ref = useRef({});

  const goToFagsak = useCallback((event: Event, id: number, oppgave: Oppgave) => {
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
              opphevOppgaveReservasjon={opphevOppgaveReservasjon}
              forlengOppgaveReservasjon={forlengOppgaveReservasjon}
              endreOppgaveReservasjon={endreOppgaveReservasjon}
              flyttReservasjon={flyttReservasjon}
            />
          )}
        </>
      )}
    </>
  );
};

export default injectIntl(OppgaverTabell);
