import { expect } from 'chai';

import {
  organiseringAvSakslisterReducer, setValgtSakslisteId, resetValgtSakslisteId, getValgtSakslisteId,
} from './duck';

describe('Saksliste-reducer', () => {
  it('skal returnere initial state', () => {
    expect(organiseringAvSakslisterReducer(undefined, {})).to.eql({ valgtSakslisteId: undefined });
  });

  it('skal oppdatere state med valgt saksliste id', () => {
    const setAction = setValgtSakslisteId(1);
    expect(organiseringAvSakslisterReducer(undefined, setAction)).to.eql({
      valgtSakslisteId: 1,
    });
  });

  it('skal resette state', () => {
    const state = {
      valgtSakslisteId: 1,
    };
    const resetAction = resetValgtSakslisteId();
    expect(organiseringAvSakslisterReducer(state, resetAction)).to.eql({
      valgtSakslisteId: undefined,
    });
  });

  it('skal finne valgt saksliste id', () => {
    const state = {
      default: {
        organiseringAvSakslisterContext: {
          valgtSakslisteId: 1,
        },
      },
    };

    expect(getValgtSakslisteId(state)).is.eql(1);
  });
});
