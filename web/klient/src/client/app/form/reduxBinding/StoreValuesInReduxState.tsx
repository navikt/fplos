import { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import { saveInReduxState } from './formDuck';

interface Form {
  key: string;
  values: any;
}

interface OwnProps {
  saveInReduxState: (form: Form) => void;
  stateKey: string;
  onUmount: boolean;
  values: any;
}

/**
 * StoreValuesInReduxState
 *
 * Lagrer verdier i redux state når komponenten blir kastet. Brukt for å mellomlagre form-state
 * ved navigering fra og til komponenter som har en final-form.
 */
export class StoreValuesInReduxState extends Component<OwnProps> {
  componentWillUnmount = () => {
    const {
      saveInReduxState: save, stateKey, values, onUmount,
    } = this.props;
    if (onUmount) {
      save({
        key: stateKey,
        values,
      });
    }
  }

  render = () => null
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  ...bindActionCreators({
    saveInReduxState,
  }, dispatch),
});


export default connect(mapStateToProps, mapDispatchToProps)(StoreValuesInReduxState);
