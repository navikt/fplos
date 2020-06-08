
import { createSelector } from 'reselect';

/* Action types */
const SET_SAKSLISTE_ID = 'SET_SAKSLISTE_ID';

/* Action creators */
export const setValgtSakslisteId = (setSakslisteId: number) => ({
  type: SET_SAKSLISTE_ID,
  data: setSakslisteId,
});

/* Reducers */
const initialState = {
  valgtSakslisteId: undefined,
};

interface Action {
  type: string;
  data?: any;
}
interface State {
  valgtSakslisteId?: number;
}

export const behandlingskoerReducer = (state: State = initialState, action: Action = { type: '' }) => {
  switch (action.type) {
    case SET_SAKSLISTE_ID:
      return {
        ...state,
        valgtSakslisteId: action.data,
      };
    default:
      return state;
  }
};

/* Selectors */
const getBehandlingskoerContext = (state) => state.default.behandlingskoerContext;
export const getValgtSakslisteId = createSelector([getBehandlingskoerContext], (behandlingskoerContext) => behandlingskoerContext.valgtSakslisteId);
