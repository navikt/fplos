import { combineReducers } from 'redux';

import { formReducer as formContext } from 'form/reduxBinding/formDuck';
import { avdelingslederReducer as avdelingslederContext } from 'avdelingsleder/duck';

export default combineReducers({
  formContext,
  avdelingslederContext,
});
