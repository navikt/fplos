import React, {
  createContext, useReducer, FunctionComponent, ReactNode,
} from 'react';

const defaultInitialState = {
  errors: [],
};
const RestDataErrorContext = createContext(defaultInitialState);
const { Provider } = RestDataErrorContext;

interface OwnProps {
  children: ReactNode;
}

const RestDataErrorProvider: FunctionComponent<OwnProps> = ({ children }): JSX.Element => {
  const [state, dispatch] = useReducer((oldState, action) => {
    switch (action.type) {
      case 'add':
        return {
          errors: oldState.errors.concat(action.data),
        };
      case 'remove':
        return defaultInitialState;
      default:
        throw new Error();
    }
  }, defaultInitialState);


  return <Provider value={{ state, dispatch }}>{children}</Provider>;
};

export { RestDataErrorContext, RestDataErrorProvider };
