import dayjs from 'dayjs';
import {
  DDMMYYYY_DATE_FORMAT, HHMM_TIME_FORMAT,
} from 'utils/formats';

export const TIDENES_ENDE = '9999-12-31';

export const dateFormat = (date: Date | string): string => dayjs(date).format(DDMMYYYY_DATE_FORMAT);

export const timeFormat = (date: string): string => dayjs(date).format(HHMM_TIME_FORMAT);

export const getDateAndTime = (tidspunkt?: string): { date: string; time: string } | undefined => {
  if (!tidspunkt) {
    return undefined;
  }
  const dateTime = dayjs(tidspunkt);
  const date = dateTime.format(DDMMYYYY_DATE_FORMAT);
  const time = dateTime.format(HHMM_TIME_FORMAT);
  return { date, time };
};
