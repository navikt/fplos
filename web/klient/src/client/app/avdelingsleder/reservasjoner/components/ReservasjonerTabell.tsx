import React, { Component } from 'react';
import { Element, Normaltekst } from 'nav-frontend-typografi';
import { FormattedHTMLMessage, FormattedMessage } from 'react-intl';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';

import { Reservasjon } from 'avdelingsleder/reservasjoner/reservasjonTsType';
import reservasjonPropType from 'avdelingsleder/reservasjoner/reservasjonPropType';
import Table from 'sharedComponents/Table';
import TableRow from 'sharedComponents/TableRow';
import TableColumn from 'sharedComponents/TableColumn';
import Image from 'sharedComponents/Image';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import removeIcon from 'images/remove.svg';

import CalendarToggleButton from 'sharedComponents/datepicker/CalendarToggleButton';
import OppgaveReservasjonEndringDatoModal
  from 'saksbehandler/behandlingskoer/components/menu/OppgaveReservasjonEndringDatoModal';
import { getDateAndTime } from 'utils/dateUtils';
import styles from './reservasjonerTabell.less';

const headerTextCodes = [
  'ReservasjonerTabell.Navn',
  'ReservasjonerTabell.Saksnr',
  'ReservasjonerTabell.BehandlingType',
  'ReservasjonerTabell.ReservertTil',
  'ReservasjonerTabell.Endre',
  'ReservasjonerTabell.Slett',
];

interface TsProps {
  reservasjoner: Reservasjon[];
  opphevReservasjon: (oppgaveId: number) => Promise<string>;
  endreOppgaveReservasjon: (oppgaveId: number, reserverTil: string) => Promise<string>;
}

interface StateTsProps {
  showReservasjonEndringDatoModal: boolean;
  valgtReservasjon?: Reservasjon;
}

export class ReservasjonerTabell extends Component<TsProps, StateTsProps> {
  static propTypes = {
    reservasjoner: PropTypes.arrayOf(reservasjonPropType).isRequired,
    opphevReservasjon: PropTypes.func.isRequired,
    endreOppgaveReservasjon: PropTypes.func.isRequired,
  }

  /* Endre denne */
  constructor(props: TsProps) {
    super(props);

    this.state = {
      showReservasjonEndringDatoModal: false,
      valgtReservasjon: undefined,
    };
  }


  closeReservasjonEndringDatoModal = (event: Event) => {
    this.setState(prevState => ({ ...prevState, showReservasjonEndringDatoModal: false }));
  }

  showReservasjonEndringDato = (reservasjon: Reservasjon) => {
    this.setState(prevState => ({ ...prevState, showReservasjonEndringDatoModal: true, valgtReservasjon: reservasjon }));
  }

  endreReserverasjon = (reserverTil: string) => {
    const { endreOppgaveReservasjon } = this.props;
    const {
      valgtReservasjon,
    } = this.state;
    endreOppgaveReservasjon(valgtReservasjon.oppgaveId, reserverTil).then(() => {
      this.setState(prevState => ({ ...prevState, showReservasjonEndringDatoModal: false }));
    });
  }

  render = () => {
    const {
      reservasjoner, opphevReservasjon,
    } = this.props;
    const {
      showReservasjonEndringDatoModal, valgtReservasjon,
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
        )
        }
        {sorterteReservasjoner.length > 0 && (
          <Table headerTextCodes={headerTextCodes} noHover>
            {sorterteReservasjoner.map(reservasjon => (
              <TableRow key={reservasjon.oppgaveId}>
                <TableColumn>{reservasjon.reservertAvNavn}</TableColumn>
                <TableColumn>{reservasjon.oppgaveSaksNr}</TableColumn>
                <TableColumn>{reservasjon.behandlingType}</TableColumn>
                <TableColumn>
                  <FormattedHTMLMessage
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
                    src={removeIcon}
                    className={styles.removeImage}
                    onMouseDown={() => opphevReservasjon(reservasjon.oppgaveId)}
                    tabIndex="0"
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
          )
        }
      </>
    );
  }
}

export default ReservasjonerTabell;
