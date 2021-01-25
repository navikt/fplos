import React, { FunctionComponent, ReactElement } from 'react';
import classnames from 'classnames/bind';
import { Field, FieldRenderProps } from 'react-final-form';
import { FieldValidator } from 'final-form';

import CustomNavSelect from './CustomNavSelect';
import renderNavField from './renderNavField';
import { LabelType } from './Label';
import ReadOnlyField from './ReadOnlyField';

import styles from './selectField.less';

const classNames = classnames.bind(styles);

interface ReadOnlyProps {
  selectValues: ReactElement[];
}

const renderReadOnly = (): FunctionComponent<FieldRenderProps<any> & ReadOnlyProps> => ({
  input,
  selectValues,
  ...otherProps
}): ReactElement => {
  const option = selectValues.map((sv) => sv.props).find((o) => o.value === input.value);
  const value = option ? option.children : undefined;
  return <ReadOnlyField input={{ value }} {...otherProps} />;
};

const renderNavSelect = renderNavField(CustomNavSelect);

interface OwnProps {
  name: string;
  selectValues: ReactElement[];
  label: LabelType;
  validate?: FieldValidator<any>;
  readOnly?: boolean;
  placeholder?: string;
  hideValueOnDisable?: boolean;
  bredde?: string;
  disabled?: boolean;
}

const SelectField: FunctionComponent<OwnProps> = ({
  name,
  label,
  selectValues,
  validate,
  readOnly,
  ...otherProps
}) => (
  <Field
    name={name}
    validate={validate}
    component={readOnly ? renderReadOnly() : renderNavSelect}
    label={label}
    selectValues={selectValues}
    disabled={!!readOnly}
    {...otherProps}
    readOnly={readOnly}
    readOnlyHideEmpty
    className={classNames('navSelect', { navSelectReadOnly: readOnly })}
  />
);

SelectField.defaultProps = {
  readOnly: false,
  placeholder: ' ',
  hideValueOnDisable: false,
};

export default SelectField;
