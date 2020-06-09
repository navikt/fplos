import React, {
  createContext, useReducer, FunctionComponent, ReactNode,
} from 'react';

import { createRequestApi } from 'data/rest-api';
import { endpoints, RestApiPathsKeys, RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import RequestApi from 'data/rest-api/src/requestApi/RequestApi';

const requestApi = createRequestApi(endpoints);

const defaultInitialState = {};

type Action = {type: 'success', key: RestApiGlobalStatePathsKeys, data: any } | {type: 'remove', key: RestApiGlobalStatePathsKeys}
type Dispatch = (action: Action) => void
type State = {[key: string]: any[]};

export const RestApiGlobalDataStateContext = createContext<State>(defaultInitialState);
export const RestApiGlobalDataDispatchContext = createContext<Dispatch | undefined>(undefined);
export const RestApiContext = createContext<RequestApi | undefined>(undefined);

interface OwnProps {
  children: ReactNode;
  initialState?: {[key in RestApiPathsKeys]: any};
  customRequestApi?: any;
}

export const RestApiGlobalDataProvider: FunctionComponent<OwnProps> = ({
  children,
  initialState,
  customRequestApi,
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
        <RestApiContext.Provider value={customRequestApi || requestApi}>
          {children}
        </RestApiContext.Provider>
      </RestApiGlobalDataDispatchContext.Provider>
    </RestApiGlobalDataStateContext.Provider>
  );
};
