import { shallow } from 'enzyme';
import { expect } from 'chai';
import React from 'react';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import behandlingType from 'kodeverk/behandlingType';
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
          kode: behandlingType.FORSTEGANGSSOKNAD,
          kodeverk: '',
        },
      }];

      requestApi.mock(RestApiPathsKeys.RESERVASJONER_FOR_AVDELING, reservasjoner);
      requestApi.mock(RestApiPathsKeys.AVDELINGSLEDER_OPPHEVER_RESERVASJON, undefined);

      const wrapper = shallow(<ReservasjonerIndex
        valgtAvdelingEnhet="2"
      />);

      await wrapper.find(ReservasjonerTabell).prop('hentAvdelingensReservasjoner')();

      expect(wrapper.find(ReservasjonerTabell).prop('reservasjoner')).is.eql(reservasjoner);
    },
  );
});
