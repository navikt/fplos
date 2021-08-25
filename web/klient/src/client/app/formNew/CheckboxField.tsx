import React, { FunctionComponent } from 'react';
import { Checkbox as NavCheckbox } from 'nav-frontend-skjema';
import { useController, useFormContext } from 'react-hook-form';
import { Normaltekst } from 'nav-frontend-typografi';

interface OwnProps {
  name: string;
  label: string;
  onClick?: () => void;
  validate?: ((value: string) => any)[];
  defaultValue?: boolean;
  readOnly?: boolean;
  onChange?: (isChecked: boolean) => void;
}

const CheckboxField: FunctionComponent<OwnProps> = ({
  name,
  label,
  validate = [],
  defaultValue = false,
  readOnly = false,
  onChange,
  ...otherProps
}) => {
  const { formState: { errors } } = useFormContext();
  const validationFunctions = validate.reduce((acc, fn, index) => ({
    ...acc,
    [index]: (value: any) => fn(value) || true,
  }), {});

  const { field } = useController({
    name,
    defaultValue,
    rules: {
      validate: validationFunctions,
    },
  });

  return (
    <NavCheckbox
      label={<Normaltekst>{label}</Normaltekst>}
      feil={errors[name] && errors[name].message}
      disabled={readOnly}
      {...field}
      {...otherProps}
      onChange={(value) => {
        field.onChange((value));
        if (onChange) {
          onChange(value.currentTarget.checked);
        }
      }}
    />
  );
};

export default CheckboxField;
