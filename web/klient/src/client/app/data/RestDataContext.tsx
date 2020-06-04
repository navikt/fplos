import React, { createContext, useReducer } from 'react';
import fpLosApiKeys from 'data/fpLosApi';

const initialState = Object.keys(fpLosApiKeys).reduce((acc, key) => ({
  ...acc,
  [key]: undefined,
}), {});
const RestDataContext = createContext(initialState);
const { Provider } = RestDataContext;

const RestDataProvider = ({ children }) => {
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
  }, initialState);


  return <Provider value={{ state, dispatch }}>{children}</Provider>;
};

export { RestDataContext, RestDataProvider };
