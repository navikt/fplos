import React, { FunctionComponent } from 'react';
import { Field } from 'react-final-form';
import dayjs from 'dayjs';
import { FieldValidator } from 'final-form';

import { ISO_DATE_FORMAT, DDMMYYYY_DATE_FORMAT, ACCEPTED_DATE_INPUT_FORMATS } from 'utils/formats';
import Datepicker from 'sharedComponents/datepicker/Datepicker';
import renderNavField from './renderNavField';
import ReadOnlyField from './ReadOnlyField';
import { LabelType } from './Label';

const composeValidators = (validators: FieldValidator<any>[]): FieldValidator<any> => (
  value: any,
) => validators.reduce((error, validator) => error || validator(value, {}), undefined);

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

export const RenderDatepickerField = renderNavField(Datepicker);

interface OwnProps{
  name: string;
  label?: LabelType;
  readOnly?: boolean;
  format?: (value: string) => string;
  parse?: (value: string) => string;
  isEdited?: boolean;
  validate?: FieldValidator<any>[];
  onBlurValidation?: boolean;
  onBlur?: (value: any) => void;
  alwaysShowCalendar?: boolean;
  disabledDays?: {
    before: Date;
    after: Date;
  };
}

const DatepickerField: FunctionComponent<OwnProps> = ({
  name,
  label = '',
  readOnly = false,
  format = (value) => value,
  parse = (value) => value,
  isEdited = false,
  validate,
  ...otherProps
}) => (
  <Field
    name={name}
    validate={validate ? composeValidators(validate) : undefined}
    component={readOnly ? ReadOnlyField : RenderDatepickerField}
    label={label}
    {...otherProps}
    format={(value) => isoToDdMmYyyy(format(value))}
    parse={(value) => parse(acceptedFormatToIso(value))}
    readOnly={readOnly}
    readOnlyHideEmpty
    isEdited={isEdited}
  />
);

export default DatepickerField;
