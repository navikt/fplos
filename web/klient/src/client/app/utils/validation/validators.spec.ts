import { expect } from 'chai';
import moment from 'moment';

import { DDMMYYYY_DATE_FORMAT, ISO_DATE_FORMAT } from 'utils/formats';
import {
  required, minLength, maxLength, minValue, maxValue,
  hasValidDate, dateBeforeOrEqual, dateAfterOrEqual,
  hasValidText, hasValidName, hasValidSaksnummerOrFodselsnummerFormat,
} from './validators';

const today = moment();
const todayAsISO = today.format(ISO_DATE_FORMAT);
const todayAsDDMMYYYY = today.format(DDMMYYYY_DATE_FORMAT);

describe('Validators', () => {
  describe('required', () => {
    it('skal gi feilmelding når verdi er lik null', () => {
      const result = required(null);
      expect(result).has.length(1);
      expect(result[0]).is.eql({ id: 'ValidationMessage.NotEmpty' });
    });

    it('skal gi feilmelding når verdi er lik undefined', () => {
      const result = required(undefined);
      expect(result).has.length(1);
      expect(result[0]).is.eql({ id: 'ValidationMessage.NotEmpty' });
    });

    it('skal ikke gi feilmelding når verdi er ulik null og undefined', () => {
      const result = required('test');
      expect(result).is.undefined;
    });
  });

  describe('minLength', () => {
    it('skal feile når verdi er mindre enn minimum lengde', () => {
      const minLength2 = minLength(2);
      const result = minLength2('e');
      expect(result).has.length(2);
      expect(result[0]).is.eql({ id: 'ValidationMessage.MinLength' });
      expect(result[1]).is.eql({ length: 2 });
    });

    it('skal ikke feile når verdi er større eller lik minimum lengde', () => {
      const minLength2 = minLength(2);
      const result = minLength2('er');
      expect(result).is.null;
    });
  });

  describe('maxLength', () => {
    it('skal feile når verdi er større enn maksimum lengde', () => {
      const maxLength2 = maxLength(2);
      const result = maxLength2('ert');
      expect(result).has.length(2);
      expect(result[0]).is.eql({ id: 'ValidationMessage.MaxLength' });
      expect(result[1]).is.eql({ length: 2 });
    });

    it('skal ikke feile når verdi er mindre eller lik minimum lengde', () => {
      const maxLength2 = maxLength(2);
      const result = maxLength2('er');
      expect(result).is.null;
    });
  });

  describe('minValue', () => {
    it('skal feile når verdi er mindre enn 2', () => {
      const minValue2 = minValue(2);
      const result = minValue2(1);
      expect(result).has.length(2);
      expect(result[0]).is.eql({ id: 'ValidationMessage.MinValue' });
      expect(result[1]).is.eql({ length: 2 });
    });

    it('skal ikke feile når verdi er større eller lik 2', () => {
      const minValue2 = minValue(2);
      const result = minValue2(2);
      expect(result).is.null;
    });
  });

  describe('maxValue', () => {
    it('skal feile når verdi er større enn 2', () => {
      const maxValue2 = maxValue(2);
      const result = maxValue2(3);
      expect(result).has.length(2);
      expect(result[0]).is.eql({ id: 'ValidationMessage.MaxValue' });
      expect(result[1]).is.eql({ length: 2 });
    });

    it('skal ikke feile når verdi er mindre eller lik 2', () => {
      const maxValue2 = maxValue(2);
      const result = maxValue2(2);
      expect(result).is.null;
    });
  });

  describe('hasValidDate', () => {
    it('skal feile når dag i dato er utenfor lovlig område', () => {
      const result = hasValidDate('2017-10-40');
      expect(result).has.length(1);
      expect(result[0]).is.eql({ id: 'ValidationMessage.InvalidDate' });
    });

    it('skal feile når måned i dato er utenfor lovlig område', () => {
      const result = hasValidDate('2017-13-20');
      expect(result).has.length(1);
      expect(result[0]).is.eql({ id: 'ValidationMessage.InvalidDate' });
    });

    it('skal feile når dato er på feil format', () => {
      const result = hasValidDate('10.10.2017');
      expect(result).has.length(1);
      expect(result[0]).is.eql({ id: 'ValidationMessage.InvalidDate' });
    });

    it('skal ikke feile når dato er korrekt', () => {
      const result = hasValidDate('2017-12-10');
      expect(result).is.null;
    });

    it('skal ikke feile når dato er tom', () => {
      // @ts-ignore Fiks
      const result = hasValidDate();
      expect(result).is.null;
    });
  });

  describe('dateBeforeOrEqual', () => {
    it('skal ikke feile når dato er før spesifisert dato', () => {
      const result = dateBeforeOrEqual(moment().toDate())('2000-12-10');
      expect(result).is.null;
    });

    it('skal ikke feile når dato er lik spesifisert dato', () => {
      const result = dateBeforeOrEqual(today)(todayAsISO);
      expect(result).is.null;
    });

    it('skal feile når dato ikke er før eller lik spesifisert dato', () => {
      const result = dateBeforeOrEqual(today)('2100-12-10');
      expect(result).has.length(2);
      expect(result[0]).is.eql({ id: 'ValidationMessage.DateNotBeforeOrEqual' });
      expect(result[1]).is.eql({ limit: todayAsDDMMYYYY });
    });

    it('skal ikke feile når dato er tom', () => {
      // @ts-ignore Fiks
      const result = dateBeforeOrEqual(today)();
      expect(result).is.null;
    });
  });

  describe('dateAfterOrEqual', () => {
    it('skal ikke feile når dato er etter spesifisert dato', () => {
      const result = dateAfterOrEqual(moment().toDate())('2100-12-10');
      expect(result).is.null;
    });

    it('skal ikke feile når dato er lik spesifisert dato', () => {
      const result = dateAfterOrEqual(today)(todayAsISO);
      expect(result).is.null;
    });

    it('skal feile når dato er før spesifisert dato', () => {
      const result = dateAfterOrEqual(today)('2000-12-10');
      expect(result).has.length(2);
      expect(result[0]).is.eql({ id: 'ValidationMessage.DateNotAfterOrEqual' });
      expect(result[1]).is.eql({ limit: todayAsDDMMYYYY });
    });

    it('skal ikke feile når dato er tom', () => {
      // @ts-ignore Fiks
      const result = dateAfterOrEqual(today.add(1, 'days'))();
      expect(result).is.null;
    });
  });

  describe('hasValidText', () => {
    it('skal ikke feile når tekst ikke har ugyldig tegn', () => {
      const result = hasValidText('Hei hei\n'
        + 'Áá Čč Đđ Ŋŋ Šš Ŧŧ Žž Ää Ææ Øø Åå\n'
        + 'Lorem + ipsum_dolor, - (sit) amet?! 100%: §2&3="I\'m";');
      expect(result).is.null;
    });

    it('skal feile når fødselsnummer har ugyldige tegn', () => {
      const result = hasValidText('Hei {}*');
      expect(result).has.length(2);
      expect(result[0]).is.eql({ id: 'ValidationMessage.InvalidText' });
      expect(result[1]).is.eql({ text: '{}*' });
    });
  });

  describe('hasValidName', () => {
    it('skal ikke feile når navn ikke har ugyldig tegn', () => {
      const result = hasValidName('Navn navn'
        + 'Áá Čč Đđ Ŋŋ Šš Ŧŧ Žž Ää Ææ Øø Åå'
        + ' - . \' ');
      expect(result).is.null;
    });

    it('skal feile når navn har ugyldige tegn', () => {
      const result = hasValidName('Navn _*');
      expect(result).has.length(2);
      expect(result[0]).is.eql({ id: 'ValidationMessage.InvalidText' });
      expect(result[1]).is.eql({ text: '_*' });
    });
  });

  describe('hasValidSaksnummerOrFodselsnummerFormat', () => {
    it('skal ikke feile når saksnummer eller fødselsnummer har gyldig pattern', () => {
      const result = hasValidSaksnummerOrFodselsnummerFormat('22121588017');
      expect(result).is.null;
    });

    it('skal feile når saksnummer eller fødselsnummer har ugyldig pattern', () => {
      const result = hasValidSaksnummerOrFodselsnummerFormat('0501851212-d');
      expect(result).has.length(1);
      expect(result[0]).is.eql({ id: 'ValidationMessage.InvalidSaksnummerOrFodselsnummerFormat' });
    });
  });
});
