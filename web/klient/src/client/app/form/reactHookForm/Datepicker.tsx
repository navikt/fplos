import React, { FunctionComponent, useMemo } from 'react';
import { DatepickerLimitations } from 'nav-datovelger';
import { useFormContext, useController } from 'react-hook-form';
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import { DDMMYYYY_DATE_FORMAT, ISO_DATE_FORMAT } from 'utils/formats';
import PureDatepicker from './pure/PureDatepicker';
import { getError, getValidationRules } from './formUtils';
import Label, { LabelType } from './Label';
import ReadOnlyField from './ReadOnlyField';

dayjs.extend(customParseFormat);

export interface DatepickerProps {
  label: LabelType;
  name: string;
  validate?: ((value: string) => any)[];
  ariaLabel?: string;
  defaultValue?: string;
  limitations?: DatepickerLimitations;
  error?: string;
  disabled?: boolean;
  isReadOnly?: boolean;
  parse?: (value: string) => string;
  onChange?: (value: any) => void;
  disabledDays?: {
    before: Date;
    after?: Date;
  };
  initialMonth?: Date;
  isEdited?: boolean;
}

const Datepicker: FunctionComponent<DatepickerProps> = ({
  name,
  validate,
  limitations,
  label,
  ariaLabel,
  defaultValue,
  error,
  disabled = false,
  isReadOnly = false,
  parse = (value) => value,
  onChange,
  disabledDays,
  initialMonth,
  isEdited,
}): JSX.Element => {
  const { formState: { errors } } = useFormContext();
  const { field } = useController({
    name,
    rules: {
      validate: useMemo(() => getValidationRules(validate), [validate]),
    },
    defaultValue,
  });

  if (isReadOnly) {
    return (
      <ReadOnlyField
        label={<Label input={label} readOnly />}
        value={field.value ? dayjs(field.value, ISO_DATE_FORMAT, true).format(DDMMYYYY_DATE_FORMAT) : undefined}
        isEdited={isEdited}
      />
    );
  }

  return (
    <PureDatepicker
      label={label}
      onChange={(date) => {
        const verdi = parse(date);
        if (onChange) {
          onChange(verdi);
        }
        return field.onChange(verdi);
      }}
      value={field.value || undefined}
      errorMessage={error || getError(errors, name)}
      limitations={limitations}
      ariaLabel={ariaLabel}
      inputId={name}
      disabled={disabled}
      disabledDays={disabledDays}
      initialMonth={initialMonth}
    />
  );
};

export default Datepicker;
