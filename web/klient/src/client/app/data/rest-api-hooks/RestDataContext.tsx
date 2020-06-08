import React, {
  createContext, useReducer, FunctionComponent, ReactNode,
} from 'react';
import { createRequestApi } from 'data/rest-api';
import { endpoints, RestApiPathsKeys } from 'data/restApiPaths';

const contextPath = 'fplos';
const requestApi = createRequestApi(contextPath, endpoints);

const defaultInitialState = Object.keys(RestApiPathsKeys).reduce((acc, key) => ({
  ...acc,
  [key]: undefined,
}), {});
const RestDataContext = createContext(defaultInitialState);
const { Provider } = RestDataContext;

interface OwnProps {
  children: ReactNode;
  initialState?: {[key in RestApiPathsKeys]: any};
  customRequestApi?: any;
}

const RestDataProvider: FunctionComponent<OwnProps> = ({ children, initialState, customRequestApi }): JSX.Element => {
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


  return <Provider value={{ state, dispatch, requestApi: customRequestApi || requestApi }}>{children}</Provider>;
};

export { RestDataContext, RestDataProvider };
