import React, { FunctionComponent } from 'react';
import { Field } from 'react-final-form';
import { Input as NavInput } from 'nav-frontend-skjema';

import renderNavField from './renderNavField';
import ReadOnlyField from './ReadOnlyField';
import { LabelType } from './Label';

const renderNavInput = renderNavField(NavInput);

const composeValidators = (validators) => (value) => validators.reduce((error, validator) => error || validator(value), undefined);

interface OwnProps {
  name: string;
  type?: string;
  label?: LabelType;
  validate?: (() => void)[];
  readOnly?: boolean;
  isEdited?: boolean;
}

const InputField: FunctionComponent<OwnProps> = ({
  name, type, label, validate, readOnly, isEdited, ...otherProps
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
  label: { id: '' },
  isEdited: false,
};

export default InputField;
