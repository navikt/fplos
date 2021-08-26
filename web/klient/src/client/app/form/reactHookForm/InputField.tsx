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
  readOnly?: boolean;
  className?: string;
  placeholder?: string;
  onBlur?: (values: any) => void;
  shouldValidateOnBlur?: boolean;
}

const InputField: FunctionComponent<OwnProps> = ({
  name,
  label,
  validate = [],
  readOnly = false,
  bredde,
  shouldValidateOnBlur = false,
  onBlur,
  className,
  placeholder,
}) => {
  const { formState: { errors }, trigger } = useFormContext();
  const validationFunctions = validate.reduce((acc, fn, index) => ({
    ...acc,
    [index]: (value: any) => fn(value) || true,
  }), {});

  const { field } = useController({
    name,
    rules: {
      validate: validationFunctions,
    },
  });

  if (readOnly) {
    return <ReadOnlyField label={<Label input={label} readOnly />} input={field.value} />;
  }

  return (
    <NavInput
      className={className}
      placeholder={placeholder}
      label={<Label input={label} readOnly={false} />}
      feil={errors[name] && errors[name].message}
      bredde={bredde}
      {...field}
      onBlur={async (values) => {
        field.onBlur();
        if (shouldValidateOnBlur) {
          const isValidationOk = await trigger();
          if (onBlur && isValidationOk) {
            onBlur(values);
          }
        } else if (onBlur) {
          onBlur(values);
        }
      }}
    />
  );
};

export default InputField;
