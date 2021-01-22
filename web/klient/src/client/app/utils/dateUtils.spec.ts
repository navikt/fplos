import {
  dateFormat, timeFormat,
}
  from './dateUtils';

describe('dateutils', () => {
  describe('dateFormat', () => {
    it('Skal formatere en dato til ISO', () => {
      const dateTime = '2017-08-02T01:54:25.455';
      expect(dateFormat(dateTime)).toEqual('02.08.2017');
    });
  });

  describe('timeFormat', () => {
    it('Skal formatere et dato til å vise kun klokkeslett', () => {
      const dateTime = '2017-08-02T01:54:25.455';
      expect(timeFormat(dateTime)).toEqual('01:54');
    });
  });
});
