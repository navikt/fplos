import React, { FunctionComponent, CSSProperties } from 'react';
import { Radio as NavRadio } from 'nav-frontend-skjema';
import { Normaltekst } from 'nav-frontend-typografi';

import Label, { LabelType } from './Label';

export interface RadioOptionProps {
  name?: string;
  label: LabelType;
  value: any;
  style?: CSSProperties;
  onChange?: (value: any) => void;
}

export const RadioOption: FunctionComponent<RadioOptionProps> = ({
  name = '',
  label,
  value,
  style,
  onChange,
}) => (
  <div style={style}>
    <NavRadio
      name={name}
      label={<Label input={label} typographyElement={Normaltekst} />}
      checked={value === true}
      onChange={(newValue) => {
        if (onChange) {
          onChange((newValue));
        }
      }}
    />
  </div>
);

export default RadioOption;
