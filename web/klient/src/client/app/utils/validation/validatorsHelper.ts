import moment from 'moment';

export const isoDateRegex = /(19|20)\d{2}-(0?[1-9]|1[0-2])-(0?[1-9]|1\d|2\d|3[01])$/;
export const numberRegex = /^\d+([,.]\d+)?$/;
export const numberOptionalNegativeRegex = /^-?\d+([,.]\d+)?$/;
export const integerRegex = /^\s*\d+\s*$/;
export const integerOptionalNegativeRegex = /^\s*-?\d+\s*$/;
export const decimalRegex = /^\d+(.\d{1,2})?$/;
export const saksnummerOrFodselsnummerPattern = /^\d{0,18}$/;

export const textRegex = /^[0-9a-zA-ZæøåÆØÅAaÁáBbCcČčDdĐđEeFfGgHhIiJjKkLlMmNnŊŋOoPpRrSsŠšTtŦŧUuVvZzŽžéôèÉöüäÖÜÄ .'\-/%§!?@_()+:;,="&\n]*$/;
export const textGyldigRegex = /[0-9a-zA-ZæøåÆØÅAaÁáBbCcČčDdĐđEeFfGgHhIiJjKkLlMmNnŊŋOoPpRrSsŠšTtŦŧUuVvZzŽžéôèÉöüäÖÜÄ .'\-/%§!?@_()+:;,="&\n]*/g;

export const nameRegex = /^[0-9a-zA-ZæøåÆØÅAaÁáBbCcČčDdĐđEeFfGgHhIiJjKkLlMmNnŊŋOoPpRrSsŠšTtŦŧUuVvZzŽžéôèÉöüäÖÜÄ .'-]*$/;
export const nameGyldigRegex = /[0-9a-zA-ZæøåÆØÅAaÁáBbCcČčDdĐđEeFfGgHhIiJjKkLlMmNnŊŋOoPpRrSsŠšTtŦŧUuVvZzŽžéôèÉöüäÖÜÄ .'-]*/g;

export const isEmpty = (text?: string | number | boolean | moment.Moment | null) => text === null || text === undefined || text.toString().trim().length === 0;
