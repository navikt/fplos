import React, { FunctionComponent, ReactNode, CSSProperties } from 'react';
import { Radio as NavRadio } from 'nav-frontend-skjema';
import { Normaltekst } from 'nav-frontend-typografi';

import Label, { LabelType } from './Label';

export interface RadioOptionProps {
  name?: string;
  label: LabelType;
  value: any;
  actualValue?: any;
  className?: string;
  disabled?: boolean;
  groupDisabled?: boolean;
  onChange?: (value: any) => void;
  children?: ReactNode | ReactNode[];
  style?: CSSProperties;
  manualHideChildren?: boolean;
}

export const RadioOption: FunctionComponent<RadioOptionProps> = ({
  name = '',
  className = '',
  label,
  value,
  actualValue,
  disabled = false,
  groupDisabled = false,
  onChange,
  children,
  style,
  manualHideChildren = false,
}) => {
  const stringifiedValue = JSON.stringify(value);
  const actualStringifiedValue = JSON.stringify(actualValue);
  const checked = stringifiedValue === actualStringifiedValue;
  return (
    <div style={style}>
      <NavRadio
        name={name}
        className={className}
        label={<Label input={label} typographyElement={Normaltekst} />}
        value={value}
        checked={checked}
        disabled={disabled || groupDisabled}
        onChange={onChange ? () => onChange(value) : undefined}
      />
      {(checked || manualHideChildren) && children}
    </div>
  );
};

RadioOption.displayName = 'RadioOption';

export default RadioOption;
