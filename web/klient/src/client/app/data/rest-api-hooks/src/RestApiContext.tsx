import React, {
  createContext, useReducer, FunctionComponent, ReactNode,
} from 'react';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import RequestApi from 'data/rest-api/src/requestApi/RequestApi';

const defaultInitialState = {};

type Action = {type: 'success', key: RestApiGlobalStatePathsKeys, data: any } | {type: 'remove', key: RestApiGlobalStatePathsKeys}
type Dispatch = (action: Action) => void
type State = {[key: string]: any};

export const RestApiStateContext = createContext<State>(defaultInitialState);
export const RestApiDispatchContext = createContext<Dispatch | undefined>(undefined);
export const RestApiRequestContext = createContext<RequestApi | undefined>(undefined);

interface OwnProps {
  children: ReactNode;
  requestApi: any;
  initialState?: {[key in RestApiGlobalStatePathsKeys]: any};
}

/**
 * Håndterer state for data som skal hentes fra backend kun en gang og som en trenger aksess til
 * mange steder i applikasjonen.
 *
 * Tilbyr i tillegg et requestApi for hooks som henter data fra backend
 */
export const RestApiProvider: FunctionComponent<OwnProps> = ({
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
    <RestApiStateContext.Provider value={state}>
      <RestApiDispatchContext.Provider value={dispatch}>
        <RestApiRequestContext.Provider value={requestApi}>
          {children}
        </RestApiRequestContext.Provider>
      </RestApiDispatchContext.Provider>
    </RestApiStateContext.Provider>
  );
};
