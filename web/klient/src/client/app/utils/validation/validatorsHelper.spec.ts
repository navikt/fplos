import {
  isoDateRegex,
  integerRegex,
  decimalRegex,
  saksnummerOrFodselsnummerPattern,
  textRegex,
  textGyldigRegex,
  nameRegex,
  nameGyldigRegex,
  isEmpty,
} from './validatorsHelper';

describe('validatorsHelper', () => {
  describe('isoDateRegex', () => {
    it('Skal sjekke om dato format er riktig ISO', () => {
      expect(isoDateRegex.test('2018-04-01')).toBe(true);
      expect(isoDateRegex.test('12-04-2018')).toBe(false);
    });
  });

  describe('integerRegex', () => {
    it('Skal sjekke om input er int', () => {
      // @ts-ignore Fiks
      expect(integerRegex.test(34)).toBe(true);
      // @ts-ignore Fiks
      expect(integerRegex.test(34.5)).toBe(false);
      expect(integerRegex.test('XXX')).toBe(false);
    });
  });

  describe('decimalRegex', () => {
    it('Skal sjekke om input er desimal', () => {
      expect(decimalRegex.test('23,34')).toBe(true);
      expect(decimalRegex.test('XXX')).toBe(false);
    });
  });

  describe('saksnummerOrFodselsnummerPattern', () => {
    it('Skal sjekke om saksnummer er i riktig format', () => {
      expect(saksnummerOrFodselsnummerPattern.test('123456789012345678')).toBe(true);
      expect(saksnummerOrFodselsnummerPattern.test('X123456789012345678')).toBe(false);
    });
  });

  describe('textRegex', () => {
    it('Skal sjekke om input er tekst', () => {
      expect(textRegex.test('text')).toBe(true);
      // @ts-ignore Fiks
      expect(textRegex.test(3434)).toBe(true);
    });
  });

  describe('textGyldigRegex', () => {
    it('Skal sjekke om input er i gyldig tekst format', () => {
      expect(textGyldigRegex.test('Text')).toBe(true);
    });
  });

  describe('nameRegex', () => {
    it('Skal sjekke om input er et navn', () => {
      expect(nameRegex.test('Ola Nordmann')).toBe(true);
      expect(nameRegex.test('Ola Nordmann!')).toBe(false);
    });
  });

  describe('nameGyldigRegex', () => {
    it('Skal sjekke om navn er et gyldig navn', () => {
      expect(nameGyldigRegex.test('Ola Nordmann')).toBe(true);
    });
  });

  describe('isEmpty', () => {
    it('Skal sjekke om input er tom', () => {
      const emptyText = null;
      const text = 'Not Empty';
      expect(isEmpty(emptyText)).toBe(true);
      expect(isEmpty(text)).toBe(false);
    });
  });
});
