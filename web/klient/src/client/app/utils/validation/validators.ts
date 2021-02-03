import moment from 'moment';
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

type InputValue = string | number | boolean;

export type FormValidationResultOrNull = FormValidationResult | null;

export const required = (value: InputValue): FormValidationResultOrNull => (isEmpty(value) ? isRequiredMessage() : null);

export const minLength = (length: number) => (text: string): FormValidationResultOrNull => (isEmpty(text)
  || text.toString().trim().length >= length ? null : minLengthMessage(length));
export const maxLength = (length: number) => (text: string): FormValidationResultOrNull => (isEmpty(text)
  || text.toString().trim().length <= length ? null : maxLengthMessage(length));

export const minValue = (length: number) => (number: number): FormValidationResultOrNull => (number >= length ? null : minValueMessage(length));
export const maxValue = (length: number) => (number: number): FormValidationResultOrNull => (number <= length ? null : maxValueMessage(length));

const hasValidPosOrNegNumber = (text: string): FormValidationResultOrNull => (isEmpty(text)
  || numberOptionalNegativeRegex.test(text) ? null : invalidNumberMessage(text));
const hasValidPosOrNegInt = (text: string): FormValidationResultOrNull => (isEmpty(text)
  || integerOptionalNegativeRegex.test(text) ? null : invalidIntegerMessage(text));
export const hasValidPosOrNegInteger = (text: string): FormValidationResultOrNull => (hasValidPosOrNegNumber(text) || hasValidPosOrNegInt(text));

export const hasValidSaksnummerOrFodselsnummerFormat = (text: string): FormValidationResultOrNull => (isEmpty(text)
  || saksnummerOrFodselsnummerPattern.test(text)
  ? null : invalidSaksnummerOrFodselsnummerFormatMessage());

export const hasValidDate = (text: string): FormValidationResultOrNull => (isEmpty(text) || isoDateRegex.test(text) ? null : invalidDateMessage());
export const dateBeforeOrEqual = (latest: moment.Moment | Date | string) => (text: moment.Moment | string): FormValidationResultOrNull => (
  (isEmpty(text) || moment(text).isSameOrBefore(moment(latest).startOf('day')))
    ? null
    : dateNotBeforeOrEqualMessage(moment(latest).format(DDMMYYYY_DATE_FORMAT))
);
const getErrorMessage = (
  earliest: moment.Moment | Date | string,
  customErrorMessage?: (date: string) => FormValidationResultOrNull,
): FormValidationResultOrNull => {
  const date = moment(earliest).format(DDMMYYYY_DATE_FORMAT);
  return customErrorMessage ? customErrorMessage(date) : dateNotAfterOrEqualMessage(date);
};
export const dateAfterOrEqual = (earliest: moment.Moment | Date | string, customErrorMessageFunction?: (date: string) => FormValidationResultOrNull) => (
  text: moment.Moment | string,
): FormValidationResultOrNull => (
  (isEmpty(text) || moment(text).isSameOrAfter(moment(earliest).startOf('day')))
    ? null
    : getErrorMessage(earliest, customErrorMessageFunction)
);

export const hasValidText = (text: string): FormValidationResultOrNull => {
  if (!textRegex.test(text)) {
    const illegalChars = text.replace(textGyldigRegex, '');
    return invalidTextMessage(illegalChars.replace(/[\t]/g, 'Tabulatortegn'));
  }
  return null;
};

export const hasValidName = (text: string): FormValidationResultOrNull => {
  if (!nameRegex.test(text)) {
    const illegalChars = text.replace(nameGyldigRegex, '');
    return invalidTextMessage(illegalChars.replace(/[\t]/g, 'Tabulatortegn'));
  }
  return null;
};
