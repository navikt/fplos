import React, { FunctionComponent } from 'react';
import { useController, useFormContext } from 'react-hook-form';
import dayjs from 'dayjs';
import { Datepicker } from 'nav-datovelger';

import { ISO_DATE_FORMAT, ACCEPTED_DATE_INPUT_FORMATS } from 'utils/formats';
import { SkjemaelementFeilmelding } from 'nav-frontend-skjema';
import ReadOnlyField from './ReadOnlyField';
import Label, { LabelType } from './Label';

const acceptedFormatToIso = (string: string): string => {
  const validDate = ACCEPTED_DATE_INPUT_FORMATS
    .map((format) => dayjs(string, format, true))
    .find((parsedDate) => parsedDate.isValid());
  if (validDate) {
    return validDate.format(ISO_DATE_FORMAT);
  }
  return string;
};

interface OwnProps{
  name: string;
  label?: LabelType;
  readOnly?: boolean;
  parse?: (value: string) => string;
  validate?: ((value: string) => any)[];
  disabledDays?: {
    before: Date;
    after: Date;
  };
}

const DatepickerField: FunctionComponent<OwnProps> = ({
  name,
  label = '',
  readOnly = false,
  parse = (value) => value,
  validate = [],
  disabledDays,
  ...otherProps
}) => {
  const { formState: { errors } } = useFormContext();
  const validationFunctions = validate.reduce((acc, fn, index) => ({
    ...acc,
    [index]: (value: any) => fn(value) || true,
  }), {});

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const { field: { ref, ...inputProps } } = useController({
    name,
    defaultValue: '',
    rules: {
      validate: validationFunctions,
    },
  });

  if (readOnly) {
    return <ReadOnlyField label={<Label input={label} readOnly />} input={inputProps.value} />;
  }

  return (
    <>
      <Label input={label} readOnly={false} />
      <Datepicker
        dayPickerProps={{
          disabledDays,
        }}
        {...inputProps}
        {...otherProps}
        onChange={(value) => inputProps.onChange(parse(acceptedFormatToIso(value)))}
      />
      {errors[name] && (
        <SkjemaelementFeilmelding id={errors[name].message}>
          {errors[name].message}
        </SkjemaelementFeilmelding>
      )}
    </>
  );
};

export default DatepickerField;
