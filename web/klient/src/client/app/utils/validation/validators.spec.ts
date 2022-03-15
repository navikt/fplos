import dayjs from 'dayjs';
import intlMock from 'testHelpers/intl-test-helper';
import { DDMMYYYY_DATE_FORMAT, ISO_DATE_FORMAT } from 'utils/formats';
import {
  required, minLength, maxLength, minValue, maxValue,
  hasValidDate, dateBeforeOrEqual, dateAfterOrEqual,
  hasValidText, hasValidName, hasValidSaksnummerOrFodselsnummerFormat,
} from './validators';

const today = dayjs();
const todayAsISO = today.format(ISO_DATE_FORMAT);

describe('Validators', () => {
  describe('required', () => {
    it('skal gi feilmelding når verdi er lik null', () => {
      const result = required(intlMock)(null as unknown as string);
      expect(result).toEqual('Feltet må fylles ut');
    });

    it('skal gi feilmelding når verdi er lik undefined', () => {
      const result = required(intlMock)(undefined as unknown as string);
      expect(result).toEqual('Feltet må fylles ut');
    });

    it('skal ikke gi feilmelding når verdi er ulik null og undefined', () => {
      const result = required(intlMock)('test');
      expect(result).toBeNull();
    });
  });

  describe('minLength', () => {
    it('skal feile når verdi er mindre enn minimum lengde', () => {
      const minLength2 = minLength(2)(intlMock);
      const result = minLength2('e');
      expect(result).toEqual('Du må skrive minst 2 tegn');
    });

    it('skal ikke feile når verdi er større eller lik minimum lengde', () => {
      const minLength2 = minLength(2)(intlMock);
      const result = minLength2('er');
      expect(result).toBeNull();
    });
  });

  describe('maxLength', () => {
    it('skal feile når verdi er større enn maksimum lengde', () => {
      const maxLength2 = maxLength(2)(intlMock);
      const result = maxLength2('ert');
      expect(result).toEqual('Du kan skrive maksimalt 2 tegn');
    });

    it('skal ikke feile når verdi er mindre eller lik minimum lengde', () => {
      const maxLength2 = maxLength(2)(intlMock);
      const result = maxLength2('er');
      expect(result).toBeNull();
    });
  });

  describe('minValue', () => {
    it('skal feile når verdi er mindre enn 2', () => {
      const minValue2 = minValue(2)(intlMock);
      const result = minValue2(1);
      expect(result).toEqual('Feltet må være større eller lik 2');
    });

    it('skal ikke feile når verdi er større eller lik 2', () => {
      const minValue2 = minValue(2)(intlMock);
      const result = minValue2(2);
      expect(result).toBeNull();
    });
  });

  describe('maxValue', () => {
    it('skal feile når verdi er større enn 2', () => {
      const maxValue2 = maxValue(2)(intlMock);
      const result = maxValue2(3);
      expect(result).toEqual('Feltet må være mindre eller lik 2');
    });

    it('skal ikke feile når verdi er mindre eller lik 2', () => {
      const maxValue2 = maxValue(2)(intlMock);
      const result = maxValue2(2);
      expect(result).toBeNull();
    });
  });

  describe('hasValidDate', () => {
    it('skal feile når dag i dato er utenfor lovlig område', () => {
      const result = hasValidDate(intlMock)('2017-10-40');
      expect(result).toEqual('Dato må skrives slik : dd.mm.åååå');
    });

    it('skal feile når måned i dato er utenfor lovlig område', () => {
      const result = hasValidDate(intlMock)('2017-13-20');
      expect(result).toEqual('Dato må skrives slik : dd.mm.åååå');
    });

    it('skal feile når dato er på feil format', () => {
      const result = hasValidDate(intlMock)('10.10.2017');
      expect(result).toEqual('Dato må skrives slik : dd.mm.åååå');
    });

    it('skal ikke feile når dato er korrekt', () => {
      const result = hasValidDate(intlMock)('2017-12-10');
      expect(result).toBeNull();
    });

    it('skal ikke feile når dato er tom', () => {
      // @ts-ignore Fiks
      const result = hasValidDate(intlMock)();
      expect(result).toBeNull();
    });
  });

  describe('dateBeforeOrEqual', () => {
    it('skal ikke feile når dato er før spesifisert dato', () => {
      const result = dateBeforeOrEqual(intlMock, dayjs().toDate())('2000-12-10');
      expect(result).toBeNull();
    });

    it('skal ikke feile når dato er lik spesifisert dato', () => {
      const result = dateBeforeOrEqual(intlMock, today)(todayAsISO);
      expect(result).toBeNull();
    });

    it('skal feile når dato ikke er før eller lik spesifisert dato', () => {
      const result = dateBeforeOrEqual(intlMock, today)('2100-12-10');
      const iDag = dayjs().format(DDMMYYYY_DATE_FORMAT);
      expect(result).toEqual(`Dato må være før eller lik ${iDag}`);
    });

    it('skal ikke feile når dato er tom', () => {
      // @ts-ignore Fiks
      const result = dateBeforeOrEqual(today)();
      expect(result).toBeNull();
    });
  });

  describe('dateAfterOrEqual', () => {
    it('skal ikke feile når dato er etter spesifisert dato', () => {
      const result = dateAfterOrEqual(intlMock, dayjs().toDate())('2100-12-10');
      expect(result).toBeNull();
    });

    it('skal ikke feile når dato er lik spesifisert dato', () => {
      const result = dateAfterOrEqual(intlMock, today)(todayAsISO);
      expect(result).toBeNull();
    });

    it('skal feile når dato er før spesifisert dato', () => {
      const result = dateAfterOrEqual(intlMock, today)('2000-12-10');
      const iDag = dayjs().format(DDMMYYYY_DATE_FORMAT);
      expect(result).toEqual(`Dato må være etter eller lik ${iDag}`);
    });

    it('skal ikke feile når dato er tom', () => {
      // @ts-ignore Fiks
      const result = dateAfterOrEqual(today.add(1, 'days'))();
      expect(result).toBeNull();
    });
  });

  describe('hasValidText', () => {
    it('skal ikke feile når tekst ikke har ugyldig tegn', () => {
      const result = hasValidText(intlMock)('Hei hei\n'
        + 'Áá Čč Đđ Ŋŋ Šš Ŧŧ Žž Ää Ææ Øø Åå\n'
        + 'Lorem + ipsum_dolor, - (sit) amet?! 100%: §2&3="I\'m";');
      expect(result).toBeNull();
    });

    it('skal feile når fødselsnummer har ugyldige tegn', () => {
      const result = hasValidText(intlMock)('Hei {}*');
      expect(result).toEqual('Feltet inneholder ugyldige tegn: {}*');
    });
  });

  describe('hasValidName', () => {
    it('skal ikke feile når navn ikke har ugyldig tegn', () => {
      const result = hasValidName(intlMock)('Navn navn'
        + 'Áá Čč Đđ Ŋŋ Šš Ŧŧ Žž Ää Ææ Øø Åå'
        + ' - . \' ');
      expect(result).toBeNull();
    });

    it('skal feile når navn har ugyldige tegn', () => {
      const result = hasValidName(intlMock)('Navn _*');
      expect(result).toEqual('Feltet inneholder ugyldige tegn: _*');
    });
  });

  describe('hasValidSaksnummerOrFodselsnummerFormat', () => {
    it(
      'skal ikke feile når saksnummer eller fødselsnummer har gyldig pattern',
      () => {
        const result = hasValidSaksnummerOrFodselsnummerFormat(intlMock)('11111111111');
        expect(result).toBeNull();
      },
    );

    it(
      'skal feile når saksnummer eller fødselsnummer har ugyldig pattern',
      () => {
        const result = hasValidSaksnummerOrFodselsnummerFormat(intlMock)('1111111111-d');
        expect(result).toEqual('Ugyldig saksnummer eller fødselsnummer');
      },
    );
  });
});
