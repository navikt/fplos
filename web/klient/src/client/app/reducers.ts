import { combineReducers } from 'redux';

import { reduxRestApi } from 'data/fpLosApi';
import errorHandler from 'data/error-api-redux';
import { formReducer as formContext } from 'form/reduxBinding/formDuck';
import { avdelingslederReducer as avdelingslederContext } from 'avdelingsleder/duck';
import { organiseringAvSakslisterReducer as organiseringAvSakslisterContext } from 'avdelingsleder/behandlingskoer/duck';
import { behandlingskoerReducer as behandlingskoerContext } from 'saksbehandler/behandlingskoer/duck';

export default combineReducers({
  formContext,
  avdelingslederContext,
  organiseringAvSakslisterContext,
  behandlingskoerContext,
  [errorHandler.getErrorReducerName()]: errorHandler.getErrorReducer(),
  dataContext: reduxRestApi.getDataReducer(),
});
