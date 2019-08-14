/* Action types */
const actionType = name => `avdelingsleder/${name}`;
const SET_SELECTED_AVDELINGSLEDER_PANEL = actionType('SET_SELECTED_AVDELINGSLEDER_PANEL');
const RESET_AVDELINGSLEDER = actionType('RESET_AVDELINGSLEDER');

/* Action creators */
export const setSelectedAvdelingslederPanel = (panelName: string) => ({
  type: SET_SELECTED_AVDELINGSLEDER_PANEL,
  payload: panelName,
});

export const resetBehandlingSupport = () => ({
  type: RESET_AVDELINGSLEDER,
});

/* Reducer */
const initialState = {
  selectedAvdelingslederPanel: undefined,
};

interface StateTsProp {
  selectedAvdelingslederPanel?: string;
}

interface ActionTsProp {
  type: string;
  payload?: any;
}

export const avdelingslederReducer = (state: StateTsProp = initialState, action: ActionTsProp = { type: '' }) => {
  switch (action.type) {
    case SET_SELECTED_AVDELINGSLEDER_PANEL:
      return {
        ...state,
        selectedAvdelingslederPanel: action.payload,
      };
    case RESET_AVDELINGSLEDER:
      return initialState;
    default:
      return state;
  }
};

const getAvdelingslederContext = state => state.default.avdelingslederContext;
export const getSelectedAvdelingslederPanel = (state: any) => getAvdelingslederContext(state).selectedAvdelingslederPanel;
