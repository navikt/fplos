import React, { Component } from 'react';
import { FieldRenderProps } from 'react-final-form';
import { injectIntl, WrappedComponentProps } from 'react-intl';

import Label, { LabelType } from './Label';

interface OwnProps {
  label?: LabelType;
  readOnly?: boolean;
  readOnlyHideEmpty?: boolean;
  isEdited?: boolean;
  onBlurValidation?: boolean;
}

const renderNavField = (WrappedNavFieldComponent: any) => {
  class FieldComponent extends Component<OwnProps & FieldRenderProps<any> & WrappedComponentProps> {
    static defaultProps = {
      readOnly: false,
      readOnlyHideEmpty: false,
      isEdited: false,
      onBlurValidation: false,
    };

    constructor(props: OwnProps & FieldRenderProps<any> & WrappedComponentProps) {
      super(props);
      this.formatError = this.formatError.bind(this);
    }

    formatError(error: any, onBlurValidation?: boolean, submitFailed?: boolean): string | undefined {
      const { intl } = this.props;
      if ((onBlurValidation || submitFailed) && error) {
        // @ts-ignore
        return intl.formatMessage(...error);
      }
      return undefined;
    }

    render() {
      const {
        input, meta: { submitFailed, error }, label, readOnly, isEdited, readOnlyHideEmpty, onBlurValidation, ...otherProps
      } = this.props;
      const isEmpty = input.value === null || input.value === undefined || input.value === '';
      if (readOnly && readOnlyHideEmpty && isEmpty) {
        return null;
      }
      const fieldProps = {
        feil: this.formatError(error, onBlurValidation, submitFailed),
        label: <Label input={label} readOnly={readOnly} />,
      };

      if (!readOnly) {
        return <WrappedNavFieldComponent {...fieldProps} {...input} {...otherProps} readOnly={readOnly} />;
      }
      return <WrappedNavFieldComponent {...fieldProps} {...input} isEdited={isEdited} {...otherProps} readOnly={readOnly} />;
    }
  }

  const FieldComponentWithIntl = injectIntl(FieldComponent);

  FieldComponentWithIntl.WrappedComponent = FieldComponent;

  return FieldComponentWithIntl;
};

export default renderNavField;
