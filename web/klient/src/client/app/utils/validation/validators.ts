import dayjs from 'dayjs';
import isSameOrAfter from 'dayjs/plugin/isSameOrAfter';
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore';
import { IntlShape } from 'react-intl';
import { DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import {
  isRequiredMessage, minLengthMessage, invalidNumberMessage, maxLengthMessage, minValueMessage, maxValueMessage, invalidDateMessage,
  invalidIntegerMessage, dateNotBeforeOrEqualMessage, dateNotAfterOrEqualMessage, invalidTextMessage, invalidSaksnummerOrFodselsnummerFormatMessage,
  FormValidationResult,
} from './messages';
import {
  isoDateRegex, numberOptionalNegativeRegex, integerOptionalNegativeRegex, textRegex,
  textGyldigRegex, isEmpty, nameRegex, nameGyldigRegex, saksnummerOrFodselsnummerPattern,
} from './validatorsHelper';

dayjs.extend(isSameOrAfter);
dayjs.extend(isSameOrBefore);

type InputValue = string | number | boolean;

export type FormValidationResultOrNull = FormValidationResult | null;

export const required = (intl: IntlShape) => (value: InputValue): FormValidationResultOrNull => (isEmpty(value) ? isRequiredMessage(intl) : null);

export const minLength = (length: number) => (intl: IntlShape) => (text: string): FormValidationResultOrNull => (isEmpty(text)
  || text.toString().trim().length >= length ? null : minLengthMessage(intl, length));
export const maxLength = (length: number) => (intl: IntlShape) => (text: string): FormValidationResultOrNull => (isEmpty(text)
  || text.toString().trim().length <= length ? null : maxLengthMessage(intl, length));

export const minValue = (length: number) => (number: number): FormValidationResultOrNull => (number >= length ? null : minValueMessage(length));
export const maxValue = (length: number) => (number: number): FormValidationResultOrNull => (number <= length ? null : maxValueMessage(length));

const hasValidPosOrNegNumber = (intl: IntlShape) => (text: string): FormValidationResultOrNull => (isEmpty(text)
  || numberOptionalNegativeRegex.test(text) ? null : invalidNumberMessage(intl, text));
const hasValidPosOrNegInt = (intl: IntlShape) => (text: string): FormValidationResultOrNull => (isEmpty(text)
  || integerOptionalNegativeRegex.test(text) ? null : invalidIntegerMessage(intl, text));
export const hasValidPosOrNegInteger = (intl: IntlShape) => (
  text: string,
): FormValidationResultOrNull => (hasValidPosOrNegNumber(intl)(text) || hasValidPosOrNegInt(intl)(text));

export const hasValidSaksnummerOrFodselsnummerFormat = (intl: IntlShape) => (text: string): FormValidationResultOrNull => (isEmpty(text)
  || saksnummerOrFodselsnummerPattern.test(text)
  ? null : invalidSaksnummerOrFodselsnummerFormatMessage(intl));

export const hasValidDate = (intl: IntlShape) => (text: string): FormValidationResultOrNull => (isEmpty(text)
  || isoDateRegex.test(text) ? null : invalidDateMessage(intl));
export const dateBeforeOrEqual = (intl: IntlShape, latest: dayjs.Dayjs | Date | string) => (text: dayjs.Dayjs | string): FormValidationResultOrNull => (
  (isEmpty(text) || dayjs(text).isSameOrBefore(dayjs(latest).startOf('day')))
    ? null
    : dateNotBeforeOrEqualMessage(intl, dayjs(latest).format(DDMMYYYY_DATE_FORMAT))
);
const getErrorMessage = (
  intl: IntlShape,
  earliest: dayjs.Dayjs | Date | string,
  customErrorMessage?: (intl: IntlShape, date: string) => FormValidationResultOrNull,
): FormValidationResultOrNull => {
  const date = dayjs(earliest).format(DDMMYYYY_DATE_FORMAT);
  return customErrorMessage ? customErrorMessage(intl, date) : dateNotAfterOrEqualMessage(intl, date);
};
export const dateAfterOrEqual = (intl: IntlShape, earliest: dayjs.Dayjs | Date | string,
  customErrorMessageFunction?: (intl: IntlShape, date: string) => FormValidationResultOrNull) => (
  text: dayjs.Dayjs | string,
): FormValidationResultOrNull => (
  (isEmpty(text) || dayjs(text).isSameOrAfter(dayjs(earliest).startOf('day')))
    ? null
    : getErrorMessage(intl, earliest, customErrorMessageFunction)
);

export const hasValidText = (intl: IntlShape) => (text: string): FormValidationResultOrNull => {
  if (!textRegex.test(text)) {
    const illegalChars = text.replace(textGyldigRegex, '');
    return invalidTextMessage(intl, illegalChars.replace(/[\t]/g, 'Tabulatortegn'));
  }
  return null;
};

export const hasValidName = (intl: IntlShape) => (text: string): FormValidationResultOrNull => {
  if (!nameRegex.test(text)) {
    const illegalChars = text.replace(nameGyldigRegex, '');
    return invalidTextMessage(intl, illegalChars.replace(/[\t]/g, 'Tabulatortegn'));
  }
  return null;
};
