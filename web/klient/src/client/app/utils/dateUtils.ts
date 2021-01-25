import moment from 'moment/moment';
import {
  DDMMYYYY_DATE_FORMAT, HHMM_TIME_FORMAT,
} from 'utils/formats';

export const TIDENES_ENDE = '9999-12-31';

export const dateFormat = (date: Date | string): string => moment(date).format(DDMMYYYY_DATE_FORMAT);

export const timeFormat = (date: string): string => moment(date).format(HHMM_TIME_FORMAT);

export const getDateAndTime = (tidspunkt: string): { date: string; time: string } => {
  const dateTime = moment(tidspunkt);
  const date = dateTime.format(DDMMYYYY_DATE_FORMAT);
  const time = dateTime.format(HHMM_TIME_FORMAT);
  return { date, time };
};
