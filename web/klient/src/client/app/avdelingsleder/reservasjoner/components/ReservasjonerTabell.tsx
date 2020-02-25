import React, { Component } from 'react';
import { Element } from 'nav-frontend-typografi';
import { FormattedMessage } from 'react-intl';
import { Reservasjon } from 'avdelingsleder/reservasjoner/reservasjonTsType';
import PropTypes from 'prop-types';
import reservasjonPropType from 'avdelingsleder/reservasjoner/reservasjonPropType';
import { getValgtAvdelingEnhet } from 'app/duck';
import { connect } from 'react-redux';
import Table from 'sharedComponents/Table';
import TableRow from 'sharedComponents/TableRow';
import TableColumn from 'sharedComponents/TableColumn';

const headerTextCodes = [
  'ReservasjonerTabell.Navn',
  'ReservasjonerTabell.Brukerident',
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
        {reservasjoner.length > 0 && (
          <Table headerTextCodes={headerTextCodes} noHover>
            {reservasjoner.map(reservasjon => (
              <TableRow key={reservasjon.reservertAvUid}>
                <TableColumn>{reservasjon.reservertAvNavn}</TableColumn>
                <TableColumn>{reservasjon.reservertAvUid}</TableColumn>
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
