import { useContext } from 'react';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';

import { RestApiGlobalDataContext } from './RestApiGlobalDataContext';

function useGlobalStateRestApiData<T>(key: RestApiGlobalStatePathsKeys): T {
  const { state } = useContext(RestApiGlobalDataContext);
  return state[key];
}

export default useGlobalStateRestApiData;
