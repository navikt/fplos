import React, { FunctionComponent } from 'react';
import { Field } from 'react-final-form';
import { Checkbox as NavCheckbox } from 'nav-frontend-skjema';
import { Normaltekst } from 'nav-frontend-typografi';

import renderNavField from './renderNavField';
import { LabelType } from './Label';

const composeValidators = (validators) => (value) => validators.reduce((error, validator) => error || validator(value), undefined);

export const RenderCheckboxField = renderNavField(({ onChange, label, ...otherProps }) => (
  <NavCheckbox
    onChange={(e) => onChange(e.target.checked)}
    checked={otherProps.value}
    label={React.cloneElement(label, { typographyElement: Normaltekst })}
    {...otherProps}
  />
));

interface OwnProps {
  name: string;
  label: LabelType;
  validate?: (() => void)[];
  readOnly?: boolean;
  onClick?: () => void;
  onChange?: (any) => void;
}

const CheckboxField: FunctionComponent<OwnProps> = ({
  name, label, validate, readOnly, ...otherProps
}) => (
  <Field
    type="checkbox"
    name={name}
    validate={validate ? composeValidators(validate) : undefined}
    component={RenderCheckboxField}
    label={label}
    disabled={readOnly}
    readOnly={readOnly}
    readOnlyHideEmpty
    {...otherProps}
  />
);

CheckboxField.defaultProps = {
  validate: null,
  readOnly: false,
};

export default CheckboxField;
