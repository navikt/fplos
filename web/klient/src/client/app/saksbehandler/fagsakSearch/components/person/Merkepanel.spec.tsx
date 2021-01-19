import React from 'react';
import { expect } from 'chai';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';

import { FormattedMessage, IntlShape } from 'react-intl';
import diskresjonskodeType from 'kodeverk/diskresjonskodeType';
import { MerkePanel } from './Merkepanel';

describe('<MerkePanel>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };
  it('skal sjekke at kun merking om død vises når person er død', () => {
    const wrapper = shallowWithIntl(<MerkePanel
      erDod
      diskresjonskode={diskresjonskodeType.KODE6}
      intl={intl as IntlShape}
    />);
    expect(wrapper.find(FormattedMessage).prop('id')).to.equal('MerkePanel.Dod');
  });
});
