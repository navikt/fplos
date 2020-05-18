import React, { FunctionComponent, ReactNode } from 'react';
import classnames from 'classnames/bind';
import { Field } from 'react-final-form';
import { SkjemaGruppe as NavSkjemaGruppe } from 'nav-frontend-skjema';

import renderNavField from './renderNavField';
import OptionGrid from './OptionGrid';

import styles from './radioGroupField.less';

const classNames = classnames.bind(styles);

const isChecked = (radioOption, actualValueStringified) => radioOption.key === actualValueStringified;

const renderRadioGroupField = renderNavField(({
  label, columns, name, value, onChange, bredde, readOnly, isEdited, feil, children, spaceBetween, rows, direction, DOMName,
}) => {
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

  return (
    <NavSkjemaGruppe feil={readOnly ? undefined : feil} className={classNames(`input--${bredde}`, 'radioGroup', { readOnly })}>
      {label.props.input && <span className={classNames('radioGroupLabel', { readOnly })}>{label}</span>}
      <OptionGrid
        direction={direction}
        isEdited={readOnly && isEdited}
        options={options}
        spaceBetween={spaceBetween}
        columns={showCheckedOnly ? 1 : columns}
        rows={showCheckedOnly ? 1 : rows}
      />
    </NavSkjemaGruppe>
  );
});

interface OwnProps {
  name: string;
  label?: ReactNode;
  /**
   * columns: Antall kolonner som valgene skal fordeles på. Default er samme som antall valg.
   */
  columns?: number;
  bredde?: string;
  children: ReactNode[];
  spaceBetween?: boolean;
  rows?: number;
  direction?: string;
  DOMName?: string;
  onChange?: (any) => void;
}

export const RadioGroupField: FunctionComponent<OwnProps> = (props) => (
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
