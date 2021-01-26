import React, { FunctionComponent, ReactElement } from 'react';
import classnames from 'classnames/bind';
import { Field } from 'react-final-form';
import { SkjemaGruppe as NavSkjemaGruppe } from 'nav-frontend-skjema';

import { LabelType } from './Label';
import { RadioOptionProps } from './RadioOption';
import renderNavField from './renderNavField';
import OptionGrid, { Direction } from './OptionGrid';

import styles from './radioGroupField.less';

const classNames = classnames.bind(styles);

const isChecked = (radioOption: React.ReactElement<RadioOptionProps>, actualValueStringified: string): boolean => radioOption.key === actualValueStringified;

interface RadioGroupFieldProps {
  name: string;
  label?: LabelType;
  columns?: number;
  bredde?: string;
  children?: React.ReactElement<RadioOptionProps>[];
  spaceBetween?: boolean;
  rows?: number;
  direction?: Direction;
  DOMName?: string;
  onChange?: (any) => void;
  readOnly?: boolean;
  isEdited?: boolean;
}

const renderRadioGroupField = renderNavField(({
  label,
  columns,
  name,
  value,
  onChange,
  bredde,
  readOnly,
  isEdited,
  feil,
  children,
  spaceBetween,
  rows,
  direction,
  DOMName,
}: RadioGroupFieldProps & { feil?: string; value: any }) => {
  const optionProps = {
    onChange,
    name: DOMName || name,
    groupDisabled: readOnly,
    className: classNames('radio'),
    actualValue: value,
  };
  const actualValueStringified = JSON.stringify(value);
  const showCheckedOnly = readOnly && value !== null && value !== undefined && value !== '';
  const options = children
    .filter((radioOption) => !!radioOption)
    .map((radioOption) => React.cloneElement(radioOption, { key: JSON.stringify(radioOption.props.value), ...optionProps }))
    .filter((radioOption) => !showCheckedOnly || isChecked(radioOption, actualValueStringified));

  const labelSomReactNode = label as ReactElement;

  return (
    <NavSkjemaGruppe feil={readOnly ? undefined : feil} className={classNames(`input--${bredde}`, 'radioGroup', { readOnly })}>
      {labelSomReactNode.props.input && <span className={classNames('radioGroupLabel', { readOnly })}>{label}</span>}
      <OptionGrid
        direction={direction}
        isEdited={readOnly && isEdited}
        // @ts-ignore Fiks
        options={options}
        spaceBetween={spaceBetween}
        columns={showCheckedOnly ? 1 : columns}
        rows={showCheckedOnly ? 1 : rows}
      />
    </NavSkjemaGruppe>
  );
});

export const RadioGroupField: FunctionComponent<RadioGroupFieldProps> = (props) => (
  <Field
    component={renderRadioGroupField}
    {...props}
  />
);

RadioGroupField.defaultProps = {
  columns: 0,
  rows: 0,
  bredde: 'fullbredde',
  label: '',
  spaceBetween: false,
  direction: 'horizontal',
};

export default RadioGroupField;
