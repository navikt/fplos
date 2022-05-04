import { useCallback, useEffect } from 'react';
import debounce from 'lodash.debounce';
import { UseFormTrigger } from 'react-hook-form';
import { formHooks } from '@navikt/ft-form-hooks';

const useDebounce = <Value, >(
  feltNavn: string,
  funksjon: (verdier: Value) => void,
  trigger?: UseFormTrigger<any>,
) => {
  const context = formHooks.useFormContext();
  const validationTrigger = trigger || context.trigger;

  const lagre = useCallback(debounce((verdi: Value) => {
    validationTrigger(feltNavn).then((isValid) => isValid && funksjon(verdi));
  }, 1000), [funksjon]);

  useEffect(() => () => lagre.cancel(), []);

  return lagre;
};

export default useDebounce;
