import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import sinon from 'sinon';

import { AutoLagringVedBlur } from './AutoLagringVedBlur';

describe('<AutoLagringVedBlur>', () => {
  it('skal lagre når en går ut av feltet navn', () => {
    const lagreFn = sinon.spy();

    const wrapper = shallow(<AutoLagringVedBlur
      lagre={lagreFn}
      active="navn"
      invalid={false}
      values={{ navn: 'Nytt listenavn' }}
      fieldNames={['navn']}
    />);

    const changedProps = {
      active: 'annet felt',
    };
    wrapper.setProps(changedProps);

    expect(lagreFn.getCalls()).has.length(1);
    const args1 = lagreFn.getCalls()[0].args;
    expect(args1).has.length(1);
    expect(args1[0]).is.eql({
      navn: 'Nytt listenavn',
    });
  });

  it('skal ikke lagre når en ikke har byttet felt', () => {
    const lagreFn = sinon.spy();

    const wrapper = shallow(<AutoLagringVedBlur
      lagre={lagreFn}
      active="navn"
      invalid={false}
      values={{ navn: 'Nyansatte' }}
      fieldNames={['navn']}
    />);

    const changedProps = {
      active: 'navn',
    };
    wrapper.setProps(changedProps);

    expect(lagreFn.getCalls()).has.length(0);
  });
});
