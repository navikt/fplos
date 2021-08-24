import React, { FunctionComponent } from 'react';
import { useController, useFormContext } from 'react-hook-form';
import { Input as NavInput } from 'nav-frontend-skjema';
import Label from './Label';
import ReadOnlyField from './ReadOnlyField';

interface OwnProps {
  name: string;
  label?: string;
  bredde?: 'fullbredde' | 'XXL' | 'XL' | 'L' | 'M' | 'S' | 'XS' | 'XXS';
  validate?: ((value: string) => any)[];
  defaultValue?: string;
  readOnly?: boolean;
}

const InputField: FunctionComponent<OwnProps> = ({
  name,
  label,
  validate = [],
  defaultValue,
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

  if (readOnly) {
    return <ReadOnlyField label={<Label input={label} readOnly={false} />} input={field.value} />;
  }

  return (
    <NavInput
      label={<Label input={label} readOnly={false} />}
      feil={errors[name] && errors[name].message}
      {...field}
      {...otherProps}
    />
  );
};

export default InputField;
