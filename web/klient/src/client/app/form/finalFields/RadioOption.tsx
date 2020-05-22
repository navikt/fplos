import React, { FunctionComponent, ReactNode, CSSProperties } from 'react';
import { Radio as NavRadio } from 'nav-frontend-skjema';
import { Normaltekst } from 'nav-frontend-typografi';

import Label, { LabelType } from './Label';

interface OwnProps {
  name?: string;
  label: LabelType;
  value: any;
  actualValue?: any;
  className?: string;
  disabled?: boolean;
  groupDisabled?: boolean;
  onChange?: (any) => void;
  children?: ReactNode | ReactNode[];
  style?: CSSProperties;
  manualHideChildren?: boolean;
}

export const RadioOption: FunctionComponent<OwnProps> = ({
  name,
  className,
  label,
  value,
  actualValue,
  disabled,
  groupDisabled,
  onChange,
  children,
  style,
  manualHideChildren,
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
        onChange={() => onChange(value)}
      />
      {(checked || manualHideChildren) && children}
    </div>
  );
};

RadioOption.defaultProps = {
  name: '',
  className: '',
  disabled: false,
  groupDisabled: false,
  onChange: () => undefined,
  manualHideChildren: false,
};

RadioOption.displayName = 'RadioOption';

export default RadioOption;
