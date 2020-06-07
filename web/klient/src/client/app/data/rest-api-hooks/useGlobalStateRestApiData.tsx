import { useContext } from 'react';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';

import { RestDataContext } from './RestDataContext';

function useGlobalStateRestApiData<T>(key: RestApiGlobalStatePathsKeys): T {
  const { state } = useContext(RestDataContext);
  return state[key];
}

export default useGlobalStateRestApiData;
