import React, { Component } from 'react';
import { getAvdelingensSaksbehandlere } from 'avdelingsleder/saksbehandlere/duck';
import { getValgtAvdelingEnhet } from 'app/duck';
import ReservasjonerPanel from './components/ReservasjonerPanel';
import {
  fetchAvdelingensReservasjoner, getAvdelingensReservasjoner,
} from './duck';
import PropTypes from "prop-types";

interface TsProps {
  fetchAvdelingensReservasjoner: (avdelingEnhet: string) => void;

}
export class ReservasjonerIndex extends Component<TsProps> {
  static propTypes = {
    fetchAvdelingensReservasjoner: PropTypes.func.isRequired,
  };

  render = () => {
    const {
      avdelingensReservajoner
    }

    return(
      <ReservasjonerPanel reservasjoner={avdelingensReservasjoner}/>
    );
}

const mapStateToProps = state => ({
  avdelingensReservajoner: getAvdelingensReservasjoner(state),
});
