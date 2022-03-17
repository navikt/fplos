import { useCallback } from 'react';
import debounce from 'lodash.debounce';
import { useFormContext, UseFormTrigger } from 'react-hook-form';

const useDebounce = <Value, >(
  feltNavn: string,
  funksjon: (verdier: Value) => void,
  trigger?: UseFormTrigger<any>,
) => {
  const context = useFormContext();
  const validationTrigger = trigger || context.trigger;

  const lagre = useCallback(debounce((verdi: Value) => {
    validationTrigger(feltNavn).then((isValid) => isValid && funksjon(verdi));
  }, 1000), [funksjon]);

  return lagre;
};

export default useDebounce;
