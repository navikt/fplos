import React from 'react';
import { FieldInputProps } from 'react-final-form';
import { shallow } from 'enzyme';
import { Normaltekst } from 'nav-frontend-typografi';

import EditedIcon from 'sharedComponents/EditedIcon';
import Label from './Label';
import ReadOnlyField from './ReadOnlyField';

describe('ReadOnlyField', () => {
  it('skal vise feltverdi', () => {
    const wrapper = shallow(<ReadOnlyField label="Dette er en test" input={{ value: '123' } as FieldInputProps<any>} meta={{}} isEdited={false} />);

    const label = wrapper.find(Label);
    expect(label).toHaveLength(1);
    expect(label.prop('input')).toEqual('Dette er en test');

    const value = wrapper.find(Normaltekst);
    expect(value).toHaveLength(1);
    expect(value.childAt(0).text()).toEqual('123');
  });

  it('skal vise feltverdi som editert', () => {
    const wrapper = shallow(<ReadOnlyField label="Dette er en test" input={{ value: '123' } as FieldInputProps<any>} meta={{}} isEdited />);
    expect(wrapper.find(EditedIcon)).toHaveLength(1);
  });

  it('skal ikke vise label når verdi er tom', () => {
    const wrapper = shallow(<ReadOnlyField label="Dette er en test" input={{ value: '' } as FieldInputProps<any>} meta={{}} isEdited={false} />);
    expect(wrapper.children()).toHaveLength(0);
  });
});
