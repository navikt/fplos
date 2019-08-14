
import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { FormSpy } from 'react-final-form';

interface TsProps {
  lagre: (values: any) => void;
  active: string;
  values: any;
  invalid: boolean;
  fieldNames: string[];
}

export class AutoLagringVedBlur extends Component<TsProps> {
  static propTypes = {
    lagre: PropTypes.func.isRequired,
    active: PropTypes.string,
    invalid: PropTypes.bool,
    values: PropTypes.shape({}).isRequired,
    fieldNames: PropTypes.arrayOf(PropTypes.string.isRequired).isRequired,
  };

  static defaultProps = {
    active: undefined,
    invalid: false,
  }

  componentDidUpdate(prevProps: TsProps) {
    const {
      active, lagre, values, invalid, fieldNames,
    } = this.props;

    if (!invalid && prevProps.active && prevProps.active !== active && fieldNames.includes(prevProps.active)) {
      // blur occurred
      lagre(values);
    }
  }

  render() {
    return null;
  }
}

export default (props: any) => (
  <FormSpy
    {...props}
    subscription={{ active: true, values: true, invalid: true }}
    component={AutoLagringVedBlur}
  />
);
