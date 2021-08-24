import React, { FunctionComponent } from 'react';
import { Checkbox as NavCheckbox } from 'nav-frontend-skjema';
import { useController, useFormContext } from 'react-hook-form';
import Label from './Label';

interface OwnProps {
  name: string;
  label: string;
  onClick?: () => void;
  validate?: ((value: string) => any)[];
  defaultValue?: boolean;
  readOnly?: boolean;
}

const CheckboxField: FunctionComponent<OwnProps> = ({
  name,
  label,
  validate = [],
  defaultValue = false,
  readOnly = false,
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
      label={<Label input={label} readOnly={false} />}
      feil={errors[name] && errors[name].message}
      disabled={readOnly}
      {...field}
      {...otherProps}
    />
  );
};

export default CheckboxField;
