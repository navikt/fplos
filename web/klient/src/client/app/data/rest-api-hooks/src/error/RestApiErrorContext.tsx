import React, {
  createContext, useReducer, FunctionComponent, ReactNode,
} from 'react';
import ErrorMessage from 'app/feilhandtering/ErrorMessage';

const defaultInitialState = {
  errors: [],
};

type Action = {type: 'add', data: any } | {type: 'remove'}
type Dispatch = (action: Action) => void
type State = {errors: ErrorMessage[]}

export const RestApiErrorStateContext = createContext<State>(defaultInitialState);
export const RestApiErrorDispatchContext = createContext<Dispatch | undefined>(undefined);

interface OwnProps {
  children: ReactNode;
}

export const RestApiErrorProvider: FunctionComponent<OwnProps> = ({
  children,
}): JSX.Element => {
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

  return (
    <RestApiErrorStateContext.Provider value={state}>
      <RestApiErrorDispatchContext.Provider value={dispatch}>
        {children}
      </RestApiErrorDispatchContext.Provider>
    </RestApiErrorStateContext.Provider>
  );
};
