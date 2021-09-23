import React, { FunctionComponent } from 'react';
import { useController, useFormContext } from 'react-hook-form';
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';

import { ACCEPTED_DATE_INPUT_FORMATS, DDMMYYYY_DATE_FORMAT, ISO_DATE_FORMAT } from 'utils/formats';
import Datepicker from 'sharedComponents/datepicker/Datepicker';

import ReadOnlyField from './ReadOnlyField';
import Label, { LabelType } from './Label';

dayjs.extend(customParseFormat);

interface OwnProps {
  name: string;
  label?: LabelType;
  readOnly?: boolean;
  isEdited?: boolean;
  className?: string;
  validate?: ((value: string) => any)[];
  disabledDays?: {
    before: Date;
    after?: Date;
  };
  initialMonth?: Date;
  numberOfMonths?: number;
  onBlur?: (values: any) => void;
  shouldValidateOnBlur?: boolean;
  parse?: (value: string) => string;
  alwaysShowCalendar?: boolean;
}

const isoToDdMmYyyy = (string: string): string => {
  const parsedDate = dayjs(string, ISO_DATE_FORMAT, true);
  if (parsedDate.isValid()) {
    return parsedDate.format(DDMMYYYY_DATE_FORMAT);
  }
  return string;
};

const acceptedFormatToIso = (string: string): string => {
  const validDate = ACCEPTED_DATE_INPUT_FORMATS
    .map((format) => dayjs(string, format, true))
    .find((parsedDate) => parsedDate.isValid());
  if (validDate) {
    return validDate.format(ISO_DATE_FORMAT);
  }
  return string;
};

const DatepickerField: FunctionComponent<OwnProps> = ({
  name,
  label,
  readOnly,
  shouldValidateOnBlur = false,
  onBlur,
  validate = [],
  parse = (value) => value,
  alwaysShowCalendar = false,
  ...otherProps
}) => {
  const { formState: { errors }, trigger } = useFormContext();
  const validationFunctions = validate.reduce((acc, fn, index) => ({
    ...acc,
    [index]: (value: any) => fn(value) || true,
  }), {});

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const { field: { ref, ...inputProps } } = useController({
    name,
    rules: {
      validate: validationFunctions,
    },
  });

  if (readOnly) {
    return <ReadOnlyField label={<Label input={label} readOnly />} input={inputProps.value} />;
  }

  return (
    <Datepicker
      label={<Label input={label} readOnly={false} />}
      feil={errors[name] && errors[name].message}
      {...inputProps}
      {...otherProps}
      value={inputProps.value ? isoToDdMmYyyy(inputProps.value) : ''}
      onBlur={async (values: any) => {
        inputProps.onBlur();
        if (shouldValidateOnBlur) {
          const isValidationOk = await trigger();
          if (onBlur && isValidationOk) {
            onBlur(values);
          }
        } else if (onBlur) {
          onBlur(values);
        }
      }}
      alwaysShowCalendar={alwaysShowCalendar}
      onChange={(event: any) => inputProps.onChange(parse(acceptedFormatToIso(event.currentTarget ? event.currentTarget.value : event)))}
    />
  );
};

export default DatepickerField;
