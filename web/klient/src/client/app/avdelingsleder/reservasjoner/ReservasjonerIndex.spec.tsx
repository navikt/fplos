import { shallow } from 'enzyme';
import React from 'react';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import BehandlingType from 'kodeverk/behandlingType';
import { ReservasjonerIndex } from './ReservasjonerIndex';
import ReservasjonerTabell from './components/ReservasjonerTabell';

describe('<ReservasjonerIndex>', () => {
  it(
    'skal hente reservasjoner ved lasting av komponent og så vise dem i panel',
    async () => {
      const reservasjoner = [{
        reservertAvUid: '2323',
        reservertAvNavn: 'Espen Utvikler',
        reservertTilTidspunkt: '2019-01-01',
        oppgaveId: 1,
        oppgaveSaksNr: 2,
        behandlingType: {
          kode: BehandlingType.FORSTEGANGSSOKNAD,
          kodeverk: '',
        },
      }];

      requestApi.mock(RestApiPathsKeys.RESERVASJONER_FOR_AVDELING.name, reservasjoner);
      requestApi.mock(RestApiPathsKeys.AVDELINGSLEDER_OPPHEVER_RESERVASJON.name, undefined);

      const wrapper = shallow(<ReservasjonerIndex
        valgtAvdelingEnhet="2"
      />);

      await wrapper.find(ReservasjonerTabell).prop('hentAvdelingensReservasjoner')();

      expect(wrapper.find(ReservasjonerTabell).prop('reservasjoner')).toEqual(reservasjoner);
    },
  );
});
