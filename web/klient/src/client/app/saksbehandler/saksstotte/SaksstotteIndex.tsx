import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import oppgavePropType from '../oppgavePropType';
import { Oppgave } from '../oppgaveTsType';
import { fetchBehandledeOppgaver, getBehandledeOppgaver } from './duck';
import { getValgtSakslisteId } from '../behandlingskoer/duck';
import SaksstottePaneler from './components/SaksstottePaneler';


interface TsProps {
  fetchBehandledeOppgaver: () => any;
  sistBehandledeSaker: Oppgave[];
  valgtSakslisteId?: number;
}

/**
 * SaksstotteIndex
 */
export class SaksstotteIndex extends Component<TsProps> {
  static propTypes = {
    sistBehandledeSaker: PropTypes.arrayOf(oppgavePropType),
    fetchBehandledeOppgaver: PropTypes.func.isRequired,
    valgtSakslisteId: PropTypes.number,
  };

  static defaultProps = {
    sistBehandledeSaker: [],
    valgtSakslisteId: undefined,
  };

  componentDidMount = () => {
    const { fetchBehandledeOppgaver: fetch } = this.props;
    fetch();
  }

  render = () => {
    const {
      sistBehandledeSaker, valgtSakslisteId,
    } = this.props;
    return (
      <SaksstottePaneler sistBehandledeSaker={sistBehandledeSaker} valgtSakslisteId={valgtSakslisteId} />
    );
  }
}

const mapStateToProps = (state) => ({
  sistBehandledeSaker: getBehandledeOppgaver(state),
  valgtSakslisteId: getValgtSakslisteId(state),
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  ...bindActionCreators({
    fetchBehandledeOppgaver,
  }, dispatch),
});


export default connect(mapStateToProps, mapDispatchToProps)(SaksstotteIndex);
