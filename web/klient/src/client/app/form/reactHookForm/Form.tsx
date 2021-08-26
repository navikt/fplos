import React, { ReactNode } from 'react';
import { FormProvider, SubmitHandler, UseFormReturn } from 'react-hook-form';

interface OwnProps<FormValues> {
  formMethods: UseFormReturn<FormValues>
  onSubmit?: SubmitHandler<FormValues>
  children: ReactNode;
}

const Form = <FormValues, >({
  formMethods,
  onSubmit,
  children,
}: OwnProps<FormValues>) => {
  const { handleSubmit } = formMethods;
  return (
    <FormProvider {...formMethods}>
      <form onSubmit={onSubmit ? handleSubmit((values) => onSubmit(values)) : undefined}>
        {children}
      </form>
    </FormProvider>
  );
};

export default Form;
