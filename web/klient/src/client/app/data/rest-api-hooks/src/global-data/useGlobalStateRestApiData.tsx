import { useContext } from 'react';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';

import { RestApiGlobalDataStateContext } from './RestApiGlobalDataContext';

function useGlobalStateRestApiData<T>(key: RestApiGlobalStatePathsKeys): T {
  const state = useContext(RestApiGlobalDataStateContext);
  return state[key];
}

export default useGlobalStateRestApiData;
