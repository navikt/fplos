import React, { Component } from 'react';
import { Element } from 'nav-frontend-typografi';
import { FormattedMessage } from 'react-intl';
import { Reservasjon } from 'avdelingsleder/reservasjoner/reservasjonTsType';
import PropTypes from 'prop-types';
import reservasjonPropType from 'avdelingsleder/reservasjoner/reservasjonPropType';
import { getValgtAvdelingEnhet } from 'app/duck';
import { connect } from 'react-redux';

interface TsProps {
  reservasjoner: Reservasjon[];
  valgtAvdelingEnhet: string;
}

export class ReservasjonerTabell extends Component<TsProps> {
  static propTypes = {
    reservasjoner: PropTypes.arrayOf(reservasjonPropType).isRequired,
    valgtAvdelingEnhet: PropTypes.string.isRequired,
  }

  render = () => {
    const {
      reservasjoner, valgtAvdelingEnhet,
    } = this.props;
    return (
      <>
        <Element><FormattedMessage id="ReservsajonerTabell.Reservasjoner" /></Element>
      </>
    );
  }
}

const mapStateToProps = state => ({
  valgtAvdelingEnhet: getValgtAvdelingEnhet(state),
});

export default connect(mapStateToProps)(ReservasjonerTabell);
