import { IntlShape } from 'react-intl';

export const isRequiredMessage = (intl: IntlShape): string => intl.formatMessage({ id: 'ValidationMessage.NotEmpty' });
export const minLengthMessage = (intl: IntlShape, length: number): string => intl.formatMessage({ id: 'ValidationMessage.MinLength' }, { length });
export const maxLengthMessage = (intl: IntlShape, length: number): string => intl.formatMessage({ id: 'ValidationMessage.MaxLength' }, { length });
export const minValueMessage = (intl: IntlShape, length: number): string => intl.formatMessage({ id: 'ValidationMessage.MinValue' }, { length });
export const maxValueMessage = (intl: IntlShape, length: number): string => intl.formatMessage({ id: 'ValidationMessage.MaxValue' }, { length });
export const invalidDateMessage = (intl: IntlShape): string => intl.formatMessage({ id: 'ValidationMessage.InvalidDate' });
export const invalidIntegerMessage = (intl: IntlShape, text: string): string => intl.formatMessage({ id: 'ValidationMessage.InvalidInteger' }, { text });
export const invalidDecimalMessage = (intl: IntlShape, text: string): string => intl.formatMessage({ id: 'ValidationMessage.InvalidDecimal' }, { text });
export const dateNotBeforeOrEqualMessage = (intl: IntlShape, limit: string): string => intl
  .formatMessage({ id: 'ValidationMessage.DateNotBeforeOrEqual' }, { limit });
export const dateNotAfterOrEqualMessage = (intl: IntlShape, limit: string): string => intl
  .formatMessage({ id: 'ValidationMessage.DateNotAfterOrEqual' }, { limit });
export const dateRangesOverlappingMessage = (intl: IntlShape): string => intl.formatMessage({ id: 'ValidationMessage.DateRangesOverlapping' });
export const datesNotEqual = (intl: IntlShape, value: string): string => intl.formatMessage({ id: 'ValidationMessage.DatesNotEqual' }, { value });
export const invalidFodselsnummerFormatMessage = (intl: IntlShape): string => intl.formatMessage({ id: 'ValidationMessage.InvalidFodselsnummerFormat' });
export const invalidFodselsnummerMessage = (intl: IntlShape): string => intl.formatMessage({ id: 'ValidationMessage.InvalidFodselsnummer' });
export const sammeFodselsnummerSomSokerMessage = (intl: IntlShape): string => intl.formatMessage({ id: 'ValidationMessage.SammeFodselsnummerSomSoker' });
export const invalidSaksnummerOrFodselsnummerFormatMessage = (intl: IntlShape) => intl
  .formatMessage({ id: 'ValidationMessage.InvalidSaksnummerOrFodselsnummerFormat' });
export const invalidTextMessage = (intl: IntlShape, text: string): string => intl.formatMessage({ id: 'ValidationMessage.InvalidText' }, { text });
export const invalidValueMessage = (intl: IntlShape, value: string): string => intl.formatMessage({ id: 'ValidationMessage.InvalidValue' }, { value });
export const arrayMinLengthMessage = (intl: IntlShape, length: number): string => intl.formatMessage({ id: 'ValidationMessage.ArrayMinLength' }, { length });
export const invalidDatesInPeriodMessage = (intl: IntlShape): string => intl.formatMessage({ id: 'ValidationMessage.InvalidDatesInPeriod' });
export const invalidPeriodMessage = (intl: IntlShape): string => intl.formatMessage({ id: 'ValidationMessage.InvalidPeriod' });
export const invalidPeriodRangeMessage = (intl: IntlShape): string => intl.formatMessage({ id: 'ValidationMessage.InvalidPeriodRange' });
export const invalidNumberMessage = (intl: IntlShape, text: string): string => intl
  .formatMessage({ id: 'ValidationMessage.InvalidNumber' }, { text });
