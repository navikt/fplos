import { expect } from 'chai';

import AvdelingslederPanels from './avdelingslederPanels';
import {
  avdelingslederReducer, setSelectedAvdelingslederPanel, resetBehandlingSupport, getSelectedAvdelingslederPanel,
} from './duck';

describe('Avdelingsleder-reducer', () => {
  it('skal returnere initial state', () => {
    expect(avdelingslederReducer(undefined, { type: '' })).to.eql({ selectedAvdelingslederPanel: undefined });
  });

  it('skal oppdatere state med valgt panel', () => {
    const setAction = setSelectedAvdelingslederPanel(AvdelingslederPanels.BEHANDLINGSKOER);
    expect(avdelingslederReducer(undefined, setAction)).to.eql({
      selectedAvdelingslederPanel: AvdelingslederPanels.BEHANDLINGSKOER,
    });
  });

  it('skal resette state', () => {
    const state = {
      selectedAvdelingslederPanel: AvdelingslederPanels.BEHANDLINGSKOER,
    };
    const resetAction = resetBehandlingSupport();
    expect(avdelingslederReducer(state, resetAction)).to.eql({
      selectedAvdelingslederPanel: undefined,
    });
  });

  it('skal finne valgt panel', () => {
    const state = {
      default: {
        avdelingslederContext: {
          selectedAvdelingslederPanel: AvdelingslederPanels.BEHANDLINGSKOER,
        },
      },
    };

    expect(getSelectedAvdelingslederPanel(state)).is.eql(AvdelingslederPanels.BEHANDLINGSKOER);
  });
});
