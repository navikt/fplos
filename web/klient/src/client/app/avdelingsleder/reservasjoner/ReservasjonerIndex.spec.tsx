import { shallow } from 'enzyme';
import { expect } from 'chai';
import React from 'react';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import behandlingType from 'kodeverk/behandlingType';
import { ReservasjonerIndex } from './ReservasjonerIndex';
import ReservasjonerTabell from './components/ReservasjonerTabell';

describe('<ReservasjonerIndex>', () => {
  it('skal hente reservasjoner ved lasting av komponent og så vise dem i panel', () => {
    const reservasjoner = [{
      reservertAvUid: '2323',
      reservertAvNavn: 'Espen Utvikler',
      reservertTilTidspunkt: '2019-01-01',
      oppgaveId: 1,
      oppgaveSaksNr: 2,
      behandlingType: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
        kodeverk: '',
      },
    }];

    requestApi.mock(RestApiPathsKeys.RESERVASJONER_FOR_AVDELING, reservasjoner);
    requestApi.mock(RestApiPathsKeys.AVDELINGSLEDER_OPPHEVER_RESERVASJON, undefined);

    const wrapper = shallow(<ReservasjonerIndex
      valgtAvdelingEnhet="2"
    />);

    const tabell = wrapper.find(ReservasjonerTabell);
    expect(tabell).to.have.length(1);
    expect(tabell.prop('reservasjoner')).is.eql(reservasjoner);
  });
});
