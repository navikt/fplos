import { parseQueryString } from './urlUtils';

describe('Url-utils', () => {
  it('skal parse url parameter', () => {
    const queryString = '?errormessage=Det+finnes+ingen+sak+med+denne+referansen%3A+266';
    expect(parseQueryString(queryString)).toEqual({ errormessage: 'Det finnes ingen sak med denne referansen: 266' });
  });

  it('skal parse to url parametere', () => {
    const queryString = '?errormessage=Det+finnes+ingen+sak+med+denne+referansen%3A+266&message=Dette+er+en+test';
    expect(parseQueryString(queryString)).toEqual(
      { errormessage: 'Det finnes ingen sak med denne referansen: 266', message: 'Dette er en test' },
    );
  });
});
