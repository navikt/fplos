/* Action types */
const actionType = (name) => `saksliste/${name}`;
const SET_VALGT_SAKSLISTE_ID = actionType('SET_VALGT_SAKSLISTE_ID');
const RESET_VALGT_SAKSLISTE_ID = actionType('RESET_VALGT_SAKSLISTE_ID');

/* Action creators */
export const setValgtSakslisteId = (valgtSakslisteId: number) => ({
  type: SET_VALGT_SAKSLISTE_ID,
  payload: valgtSakslisteId,
});


export const resetValgtSakslisteId = () => ({
  type: RESET_VALGT_SAKSLISTE_ID,
});

/* Reducer */
const initialState = {
  valgtSakslisteId: undefined,
};

interface State {
  valgtSakslisteId?: number;
}

interface Action {
  type: string;
  payload?: any;
}

export const organiseringAvSakslisterReducer = (state: State = initialState, action: Action = { type: '' }) => {
  switch (action.type) {
    case SET_VALGT_SAKSLISTE_ID:
      return {
        ...state,
        valgtSakslisteId: action.payload,
      };
    case RESET_VALGT_SAKSLISTE_ID:
      return initialState;
    default:
      return state;
  }
};

const getOrganiseringAvSakslisterContext = (state) => state.default.organiseringAvSakslisterContext;
export const getValgtSakslisteId = (state: any) => getOrganiseringAvSakslisterContext(state).valgtSakslisteId;
