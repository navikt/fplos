import React, { Component } from 'react';
import { Element, Normaltekst } from 'nav-frontend-typografi';
import { FormattedMessage } from 'react-intl';
import { Reservasjon } from 'avdelingsleder/reservasjoner/reservasjonTsType';
import PropTypes from 'prop-types';
import reservasjonPropType from 'avdelingsleder/reservasjoner/reservasjonPropType';
import { getValgtAvdelingEnhet } from 'app/duck';
import { connect } from 'react-redux';
import Table from 'sharedComponents/Table';
import TableRow from 'sharedComponents/TableRow';
import TableColumn from 'sharedComponents/TableColumn';
import Image from 'sharedComponents/Image';
import styles from 'avdelingsleder/saksbehandlere/components/saksbehandlereTabell.less';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import removeIcon from '../../../../images/remove.svg';

const headerTextCodes = [
  'ReservasjonerTabell.Navn',
  'ReservasjonerTabell.OppgaveId',
  'ReservasjonerTabell.Saksnr',
  'ReservasjonerTabell.ReservertTil',
];

interface TsProps {
  reservasjoner: Reservasjon[];
  valgtAvdelingEnhet: string;
}

interface StateTsProps {
  placeholder?: string;
}

export class ReservasjonerTabell extends Component<TsProps, StateTsProps> {
  static propTypes = {
    reservasjoner: PropTypes.arrayOf(reservasjonPropType).isRequired,
    valgtAvdelingEnhet: PropTypes.string.isRequired,
  }

  /* Endre denne */
  constructor(props: TsProps) {
    super(props);

    this.state = {
      placeholder: undefined,
    };
  }

  render = () => {
    const {
      reservasjoner, valgtAvdelingEnhet,
    } = this.props;
    const {
      placeholder,
    } = this.state;
    return (
      <>
        <Element><FormattedMessage id="ReservasjonerTabell.Reservasjoner" /></Element>
        {reservasjoner.length === 0 && (
          <>
            <VerticalSpacer eightPx />
            <Normaltekst><FormattedMessage id="ReservasjonerTabell.IngenReservasjoner" /></Normaltekst>
            <VerticalSpacer eightPx />
          </>
        )
        }
        {reservasjoner.length > 0 && (
          <Table headerTextCodes={headerTextCodes} noHover>
            {reservasjoner.map(reservasjon => (
              <TableRow key={reservasjon.oppgaveId}>
                <TableColumn>{reservasjon.reservertAvNavn}</TableColumn>
                <TableColumn>{reservasjon.oppgaveId}</TableColumn>
                <TableColumn>{reservasjon.oppgaveSaksNr}</TableColumn>
                <TableColumn>{reservasjon.reservertTilTidspunkt}</TableColumn>
                <TableColumn>
                  <Image
                    src={removeIcon}
                    className={styles.removeImage}
                    tabIndex="0"
                  />
                </TableColumn>
              </TableRow>
            ))}
          </Table>
        )}
      </>
    );
  }
}

const mapStateToProps = state => ({
  valgtAvdelingEnhet: getValgtAvdelingEnhet(state),
});

export default connect(mapStateToProps)(ReservasjonerTabell);
