import React, { Component } from 'react';
import { Element, Normaltekst } from 'nav-frontend-typografi';
import { FormattedMessage } from 'react-intl';

import Reservasjon from 'avdelingsleder/reservasjoner/reservasjonTsType';
import Table from 'sharedComponents/table/Table';
import TableRow from 'sharedComponents/table/TableRow';
import TableColumn from 'sharedComponents/table/TableColumn';
import Image from 'sharedComponents/Image';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import CalendarToggleButton from 'sharedComponents/datepicker/CalendarToggleButton';
import OppgaveReservasjonEndringDatoModal from 'saksbehandler/behandlingskoer/components/menu/OppgaveReservasjonEndringDatoModal';
import FlyttReservasjonModal from 'saksbehandler/behandlingskoer/components/menu/FlyttReservasjonModal';
import { getDateAndTime } from 'utils/dateUtils';

import removeIcon from 'images/remove.svg';
import gruppeHoverUrl from 'images/gruppe_hover.svg';
import gruppeUrl from 'images/gruppe.svg';

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
  endreOppgaveReservasjon: (oppgaveId: number, reserverTil: string) => Promise<string>;
  flyttReservasjon: (oppgaveId: number, brukerident: string, begrunnelse: string) => Promise<string>;
  finnSaksbehandler: (brukerIdent: string) => Promise<string>;
  nullstillSaksbehandler: () => Promise<string>;
}

interface StateTsProps {
  showReservasjonEndringDatoModal: boolean;
  showFlyttReservasjonModal: boolean;
  valgtReservasjon?: Reservasjon;
}

class ReservasjonerTabell extends Component<OwnProps, StateTsProps> {
  constructor(props) {
    super(props);

    this.state = {
      showReservasjonEndringDatoModal: false,
      showFlyttReservasjonModal: false,
      valgtReservasjon: undefined,
    };
  }


  closeReservasjonEndringDatoModal = () => {
    this.setState((prevState) => ({ ...prevState, showReservasjonEndringDatoModal: false }));
  }

  showReservasjonEndringDato = (reservasjon: Reservasjon) => {
    this.setState((prevState) => ({ ...prevState, showReservasjonEndringDatoModal: true, valgtReservasjon: reservasjon }));
  }

  endreReserverasjon = (reserverTil: string) => {
    const { endreOppgaveReservasjon } = this.props;
    const {
      valgtReservasjon,
    } = this.state;
    endreOppgaveReservasjon(valgtReservasjon.oppgaveId, reserverTil).then(() => {
      this.setState((prevState) => ({ ...prevState, showReservasjonEndringDatoModal: false }));
    });
  }

  showFlytteModal = (reservasjon: Reservasjon) => {
    this.setState((prevState) => ({ ...prevState, showFlyttReservasjonModal: true, valgtReservasjon: reservasjon }));
  }

  closeFlytteModal = () => {
    this.setState((prevState) => ({ ...prevState, showFlyttReservasjonModal: false }));
  }

  flyttReservasjon = (oppgaveId: number, brukerident: string, begrunnelse: string) => {
    const { flyttReservasjon } = this.props;
    flyttReservasjon(oppgaveId, brukerident, begrunnelse).then(() => {
      this.setState((prevState) => ({ ...prevState, showFlyttReservasjonModal: false }));
    });
  }

  render = () => {
    const {
      reservasjoner, opphevReservasjon, finnSaksbehandler, nullstillSaksbehandler,
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
                <TableColumn>{reservasjon.behandlingType.navn}</TableColumn>
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
        {showReservasjonEndringDatoModal
          && (
            <OppgaveReservasjonEndringDatoModal
              showModal={showReservasjonEndringDatoModal}
              endreOppgaveReservasjon={this.endreReserverasjon}
              closeModal={this.closeReservasjonEndringDatoModal}
              reserverTilDefault={valgtReservasjon.reservertTilTidspunkt}
            />
          )}
        { showFlyttReservasjonModal && (
          <FlyttReservasjonModal
            oppgaveId={valgtReservasjon.oppgaveId}
            showModal={showFlyttReservasjonModal}
            closeModal={this.closeFlytteModal}
            submit={this.flyttReservasjon}
            finnSaksbehandler={finnSaksbehandler}
            resetSaksbehandler={nullstillSaksbehandler}
          />
        )}
      </>
    );
  }
}

export default ReservasjonerTabell;
