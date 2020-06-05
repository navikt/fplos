import React, {
  createContext, useReducer, FunctionComponent, ReactNode,
} from 'react';
import { RestApiPathsKeys } from 'data/restApiPaths';

const defaultInitialState = Object.keys(RestApiPathsKeys).reduce((acc, key) => ({
  ...acc,
  [key]: undefined,
}), {});
const RestDataContext = createContext(defaultInitialState);
const { Provider } = RestDataContext;

interface OwnProps {
  initialState?: {[key in RestApiPathsKeys]: any};
  children: ReactNode;
}

const RestDataProvider: FunctionComponent<OwnProps> = ({ initialState, children }): JSX.Element => {
  const [state, dispatch] = useReducer((oldState, action) => {
    switch (action.type) {
      case 'success':
        return {
          ...oldState,
          [action.key]: action.data,
        };
      default:
        throw new Error();
    }
  }, initialState || defaultInitialState);


  return <Provider value={{ state, dispatch }}>{children}</Provider>;
};

export { RestDataContext, RestDataProvider };
