import React, { Component, ReactNode } from 'react';
import { Element, Normaltekst } from 'nav-frontend-typografi';
import { FormattedMessage } from 'react-intl';

import Reservasjon from 'types/avdelingsleder/reservasjonTsType';
import Table from 'sharedComponents/table/Table';
import TableRow from 'sharedComponents/table/TableRow';
import TableColumn from 'sharedComponents/table/TableColumn';
import Image from 'sharedComponents/Image';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import OppgaveReservasjonEndringDatoModal from 'saksbehandler/behandlingskoer/components/menu/OppgaveReservasjonEndringDatoModal';
import FlyttReservasjonModal from 'saksbehandler/behandlingskoer/components/menu/FlyttReservasjonModal';
import { getDateAndTime } from 'utils/dateUtils';
import AlleKodeverk from 'types/alleKodeverkTsType';
import { getKodeverknavnFraKode } from 'utils/kodeverkUtils';
import KodeverkType from 'kodeverk/kodeverkTyper';

import removeIcon from 'images/remove.svg';
import gruppeHoverUrl from 'images/gruppe_hover.svg';
import gruppeUrl from 'images/gruppe.svg';

import CalendarToggleButton from './CalendarToggleButton';
import styles from './reservasjonerTabell.less';

const headerTextCodes = [
  'ReservasjonerTabell.Navn',
  'ReservasjonerTabell.Saksnr',
  'ReservasjonerTabell.BehandlingType',
  'ReservasjonerTabell.ReservertTil',
  'ReservasjonerTabell.Endre',
  'ReservasjonerTabell.Flytt',
  'ReservasjonerTabell.Slett',
];

interface OwnProps {
  reservasjoner: Reservasjon[];
  opphevReservasjon: (oppgaveId: number) => Promise<string>;
  hentAvdelingensReservasjoner: () => void;
  alleKodeverk: AlleKodeverk;
}

interface StateTsProps {
  showReservasjonEndringDatoModal: boolean;
  showFlyttReservasjonModal: boolean;
  valgtReservasjon?: Reservasjon;
}

// TODO Skriv om til funksjonell komponent

class ReservasjonerTabell extends Component<OwnProps, StateTsProps> {
  constructor(props: OwnProps) {
    super(props);

    this.state = {
      showReservasjonEndringDatoModal: false,
      showFlyttReservasjonModal: false,
      valgtReservasjon: undefined,
    };
  }

  closeReservasjonEndringDatoModal = (): void => {
    this.setState((prevState) => ({ ...prevState, showReservasjonEndringDatoModal: false }));
  };

  showReservasjonEndringDato = (reservasjon: Reservasjon): void => {
    this.setState((prevState) => ({ ...prevState, showReservasjonEndringDatoModal: true, valgtReservasjon: reservasjon }));
  };

  endreReserverasjonState = (): void => {
    this.setState((prevState) => ({ ...prevState, showReservasjonEndringDatoModal: false }));
  };

  showFlytteModal = (reservasjon: Reservasjon): void => {
    this.setState((prevState) => ({ ...prevState, showFlyttReservasjonModal: true, valgtReservasjon: reservasjon }));
  };

  closeFlytteModal = (): void => {
    this.setState((prevState) => ({ ...prevState, showFlyttReservasjonModal: false }));
  };

  toggleMenu = (): void => {
    this.setState((prevState) => ({ ...prevState, showFlyttReservasjonModal: false }));
  };

  // eslint-disable-next-line react/no-arrow-function-lifecycle
  render = (): ReactNode => {
    const {
      reservasjoner, opphevReservasjon, hentAvdelingensReservasjoner, alleKodeverk,
    } = this.props;
    const {
      showReservasjonEndringDatoModal, showFlyttReservasjonModal, valgtReservasjon,
    } = this.state;

    const sorterteReservasjoner = reservasjoner.sort((reservasjon1, reservasjon2) => reservasjon1.reservertAvNavn.localeCompare(reservasjon2.reservertAvNavn));

    return (
      <>
        <Element><FormattedMessage id="ReservasjonerTabell.Reservasjoner" /></Element>
        {sorterteReservasjoner.length === 0 && (
          <>
            <VerticalSpacer eightPx />
            <Normaltekst><FormattedMessage id="ReservasjonerTabell.IngenReservasjoner" /></Normaltekst>
            <VerticalSpacer eightPx />
          </>
        )}
        {sorterteReservasjoner.length > 0 && (
          <Table headerTextCodes={headerTextCodes} noHover>
            {sorterteReservasjoner.map((reservasjon) => (
              <TableRow key={reservasjon.oppgaveId}>
                <TableColumn>{reservasjon.reservertAvNavn}</TableColumn>
                <TableColumn>{reservasjon.oppgaveSaksNr}</TableColumn>
                <TableColumn>{getKodeverknavnFraKode(reservasjon.behandlingType, KodeverkType.BEHANDLING_TYPE, alleKodeverk)}</TableColumn>
                <TableColumn>
                  <FormattedMessage
                    id="ReservasjonerTabell.ReservertTilFormat"
                    values={getDateAndTime(reservasjon.reservertTilTidspunkt)}
                  />
                </TableColumn>
                <TableColumn>
                  <CalendarToggleButton
                    toggleShowCalendar={() => this.showReservasjonEndringDato(reservasjon)}
                    className={styles.calendarToggleButton}
                  />
                </TableColumn>
                <TableColumn>
                  <Image
                    src={gruppeUrl}
                    srcHover={gruppeHoverUrl}
                    onMouseDown={() => this.showFlytteModal(reservasjon)}
                  />
                </TableColumn>
                <TableColumn>
                  <Image
                    src={removeIcon}
                    className={styles.removeImage}
                    onMouseDown={() => opphevReservasjon(reservasjon.oppgaveId)}
                  />
                </TableColumn>
              </TableRow>
            ))}
          </Table>
        )}
        {valgtReservasjon && showReservasjonEndringDatoModal && (
          <OppgaveReservasjonEndringDatoModal
            showModal={showReservasjonEndringDatoModal}
            closeModal={this.closeReservasjonEndringDatoModal}
            reserverTilDefault={valgtReservasjon.reservertTilTidspunkt}
            endreReserverasjonState={this.endreReserverasjonState}
            hentReserverteOppgaver={hentAvdelingensReservasjoner}
            oppgaveId={valgtReservasjon.oppgaveId}
          />
        )}
        {valgtReservasjon && showFlyttReservasjonModal && (
          <FlyttReservasjonModal
            showModal={showFlyttReservasjonModal}
            closeModal={this.closeFlytteModal}
            oppgaveId={valgtReservasjon.oppgaveId}
            toggleMenu={this.toggleMenu}
            hentReserverteOppgaver={hentAvdelingensReservasjoner}
          />
        )}
      </>
    );
  };
}

export default ReservasjonerTabell;
