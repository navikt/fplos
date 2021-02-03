import React, { FunctionComponent } from 'react';
import { Field } from 'react-final-form';
import { Input as NavInput } from 'nav-frontend-skjema';

import { FieldValidator } from 'final-form';
import renderNavField from './renderNavField';
import ReadOnlyField from './ReadOnlyField';
import { LabelType } from './Label';

const renderNavInput = renderNavField(NavInput);

const composeValidators = (validators?: FieldValidator<any>[]): FieldValidator<any> => (
  value: any,
) => (validators ? validators.reduce((error, validator) => error || validator(value, {}), undefined) : []);

interface OwnProps {
  name: string;
  type?: string;
  label?: LabelType;
  validate?: FieldValidator<any>[]
  readOnly?: boolean;
  isEdited?: boolean;
  className?: string;
  placeholder?: string;
  onBlurValidation?: boolean;
  bredde?: string;
  parse?: (value: string) => string;
  autoFocus?: boolean;
}

const InputField: FunctionComponent<OwnProps> = ({
  name,
  type,
  label,
  validate,
  readOnly,
  isEdited,
  ...otherProps
}) => (
  <Field
    name={name}
    validate={composeValidators(validate)}
    component={readOnly ? ReadOnlyField : renderNavInput}
    type={type}
    label={label}
    {...otherProps}
    readOnly={readOnly}
    readOnlyHideEmpty
    isEdited={isEdited}
    autoComplete="off"
  />
);

InputField.defaultProps = {
  type: 'text',
  validate: [],
  readOnly: false,
  label: '',
  isEdited: false,
};

export default InputField;
