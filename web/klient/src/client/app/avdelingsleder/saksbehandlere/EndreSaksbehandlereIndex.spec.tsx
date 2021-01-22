import React from 'react';
import { shallow } from 'enzyme';

import EndreSaksbehandlereIndex from './EndreSaksbehandlereIndex';
import SaksbehandlerePanel from './components/SaksbehandlerePanel';

describe('<EndreSaksbehandlereIndex>', () => {
  it(
    'skal hente saksbehandlere ved lasting av komponent og så vise desse i panel',
    () => {
      const wrapper = shallow(<EndreSaksbehandlereIndex
        valgtAvdelingEnhet="2"
        avdelingensSaksbehandlere={[]}
        hentAvdelingensSaksbehandlere={() => undefined}
      />);

      expect(wrapper.find(SaksbehandlerePanel)).toHaveLength(1);
    },
  );
});
