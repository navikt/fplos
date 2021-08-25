import { IntlShape } from 'react-intl';

export type FormValidationResult = {
  id?: string,
  length?: number,
  text?: string,
  limit?: string;
  value?: string;
} | string;

export const isRequiredMessage = (): FormValidationResult => ([{ id: 'ValidationMessage.NotEmpty' }]);
export const minLengthMessage = (length: number): FormValidationResult => ([{ id: 'ValidationMessage.MinLength' }, { length }]);
export const maxLengthMessage = (length: number): FormValidationResult => ([{ id: 'ValidationMessage.MaxLength' }, { length }]);
export const minValueMessage = (length: number): FormValidationResult => ([{ id: 'ValidationMessage.MinValue' }, { length }]);
export const maxValueMessage = (length: number): FormValidationResult => ([{ id: 'ValidationMessage.MaxValue' }, { length }]);
export const invalidDateMessage = (intl: IntlShape): string => intl.formatMessage({ id: 'ValidationMessage.InvalidDate' });
export const invalidIntegerMessage = (text: string): FormValidationResult => ([{ id: 'ValidationMessage.InvalidInteger' }, { text }]);
export const invalidDecimalMessage = (text: string): FormValidationResult => ([{ id: 'ValidationMessage.InvalidDecimal' }, { text }]);
export const dateNotBeforeOrEqualMessage = (intl: IntlShape, limit: string): string => intl
  .formatMessage({ id: 'ValidationMessage.DateNotBeforeOrEqual' }, { limit });
export const dateNotAfterOrEqualMessage = (intl: IntlShape, limit: string): string => intl
  .formatMessage({ id: 'ValidationMessage.DateNotAfterOrEqual' }, { limit });
export const dateRangesOverlappingMessage = (): FormValidationResult => ([{ id: 'ValidationMessage.DateRangesOverlapping' }]);
export const datesNotEqual = (value: string): FormValidationResult => ([{ id: 'ValidationMessage.DatesNotEqual' }, { value }]);
export const invalidFodselsnummerFormatMessage = (): FormValidationResult => ([{ id: 'ValidationMessage.InvalidFodselsnummerFormat' }]);
export const invalidFodselsnummerMessage = (): FormValidationResult => ([{ id: 'ValidationMessage.InvalidFodselsnummer' }]);
export const sammeFodselsnummerSomSokerMessage = (): FormValidationResult => ([{ id: 'ValidationMessage.SammeFodselsnummerSomSoker' }]);
export const invalidSaksnummerOrFodselsnummerFormatMessage = (intl: IntlShape) => intl
  .formatMessage({ id: 'ValidationMessage.InvalidSaksnummerOrFodselsnummerFormat' });
export const invalidTextMessage = (text: string): FormValidationResult => ([{ id: 'ValidationMessage.InvalidText' }, { text }]);
export const invalidValueMessage = (value: string): FormValidationResult => ([{ id: 'ValidationMessage.InvalidValue' }, { value }]);
export const arrayMinLengthMessage = (length: number): FormValidationResult => ([{ id: 'ValidationMessage.ArrayMinLength' }, { length }]);
export const invalidDatesInPeriodMessage = (): FormValidationResult => ([{ id: 'ValidationMessage.InvalidDatesInPeriod' }]);
export const invalidPeriodMessage = (): FormValidationResult => ([{ id: 'ValidationMessage.InvalidPeriod' }]);
export const invalidPeriodRangeMessage = (): FormValidationResult => ([{ id: 'ValidationMessage.InvalidPeriodRange' }]);
export const invalidNumberMessage = (text: string): FormValidationResult => ([{ id: 'ValidationMessage.InvalidNumber' }, { text }]);
