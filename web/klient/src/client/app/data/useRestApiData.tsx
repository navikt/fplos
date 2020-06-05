import { useContext } from 'react';

import { RestApiPathsKeys } from 'data/restApiPaths';

import { RestDataContext } from './RestDataContext';

function useRestApiData<T>(key: RestApiPathsKeys): T {
  const { state } = useContext(RestDataContext);
  return state[key];
}

export default useRestApiData;
