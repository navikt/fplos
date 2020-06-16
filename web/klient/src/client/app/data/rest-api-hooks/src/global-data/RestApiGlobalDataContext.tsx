import React, {
  createContext, useReducer, FunctionComponent, ReactNode,
} from 'react';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import RequestApi from 'data/rest-api/src/requestApi/RequestApi';

const defaultInitialState = {};

type Action = {type: 'success', key: RestApiGlobalStatePathsKeys, data: any } | {type: 'remove', key: RestApiGlobalStatePathsKeys}
type Dispatch = (action: Action) => void
type State = {[key: string]: any};

export const RestApiGlobalDataStateContext = createContext<State>(defaultInitialState);
export const RestApiGlobalDataDispatchContext = createContext<Dispatch | undefined>(undefined);
export const RestApiContext = createContext<RequestApi | undefined>(undefined);

interface OwnProps {
  children: ReactNode;
  requestApi: any;
  initialState?: {[key in RestApiGlobalStatePathsKeys]: any};
}

/**
 * State for data som skal hentes fra backend kun en gang og som en trenger aksess til
 * mange steder i applikasjonen.
 *
 * NB! Ikke bruk denne mer enn nødvendig, da det kan føre til veldig mange rerendringer.
 */
export const RestApiGlobalDataProvider: FunctionComponent<OwnProps> = ({
  children,
  initialState,
  requestApi,
}): JSX.Element => {
  const [state, dispatch] = useReducer((oldState, action) => {
    switch (action.type) {
      case 'success':
        return {
          ...oldState,
          [action.key]: action.data,
        };
      case 'remove':
        return Object.keys(oldState).filter((key) => key !== action.key).reduce((acc, key) => ({
          ...acc,
          [key]: oldState[key],
        }), {});
      default:
        throw new Error();
    }
  }, initialState || defaultInitialState);

  return (
    <RestApiGlobalDataStateContext.Provider value={state}>
      <RestApiGlobalDataDispatchContext.Provider value={dispatch}>
        <RestApiContext.Provider value={requestApi}>
          {children}
        </RestApiContext.Provider>
      </RestApiGlobalDataDispatchContext.Provider>
    </RestApiGlobalDataStateContext.Provider>
  );
};
