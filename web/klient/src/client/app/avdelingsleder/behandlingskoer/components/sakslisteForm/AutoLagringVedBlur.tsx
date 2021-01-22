import React, { Component } from 'react';

import { FormSpy } from 'react-final-form';

interface OwnProps {
  lagre: (values: any) => void;
  active: string;
  values: any;
  invalid: boolean;
  fieldNames: string[];
}

export class AutoLagringVedBlur extends Component<OwnProps> {
  static defaultProps = {
    active: undefined,
    invalid: false,
  }

  componentDidUpdate(prevProps: OwnProps) {
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
