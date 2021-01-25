import moment from 'moment/moment';
import {
  DDMMYYYY_DATE_FORMAT, HHMM_TIME_FORMAT,
} from 'utils/formats';

export const TIDENES_ENDE = '9999-12-31';

export const dateFormat = (date) => moment(date).format(DDMMYYYY_DATE_FORMAT);

export const timeFormat = (date) => moment(date).format(HHMM_TIME_FORMAT);

export const getDateAndTime = (tidspunkt) => {
  const dateTime = moment(tidspunkt);
  const date = dateTime.format(DDMMYYYY_DATE_FORMAT);
  const time = dateTime.format(HHMM_TIME_FORMAT);
  return { date, time };
};
