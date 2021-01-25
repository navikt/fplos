import React, { FunctionComponent } from 'react';
import { Field, FormRenderProps } from 'react-final-form';
import { Checkbox as NavCheckbox } from 'nav-frontend-skjema';
import { Normaltekst } from 'nav-frontend-typografi';

import { FieldValidator } from 'final-form';
import renderNavField from './renderNavField';
import { LabelType } from './Label';

const composeValidators = (validators: FieldValidator<any>[]): FieldValidator<any> => (
  value: any,
) => validators.reduce((error, validator) => error || validator(value, undefined), undefined);

interface RenderCheckboxFieldProps {
  onChange: (isChecked: boolean) => void;
  label: React.ReactElement;
  value: string | number | string[];
  isEdited: boolean;
}

export const RenderCheckboxField = renderNavField(({
  onChange,
  label,
  ...otherProps
}: FormRenderProps & RenderCheckboxFieldProps) => (
  // @ts-ignore Fiks
  <NavCheckbox
    onChange={(e) => onChange(e.target.checked)}
    checked={!!otherProps.value}
    label={React.cloneElement(label, { typographyElement: Normaltekst })}
    {...otherProps}
  />
));

interface OwnProps {
  name: string;
  label: LabelType;
  validate?: FieldValidator<any>[];
  readOnly?: boolean;
  onClick?: () => void;
  onChange?: (isChecked: boolean) => void;
}

const CheckboxField: FunctionComponent<OwnProps> = ({
  name,
  label,
  validate,
  readOnly,
  ...otherProps
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
  readOnly: false,
};

export default CheckboxField;
