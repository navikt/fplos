import { useContext } from 'react';

import { RestDataContext } from './RestDataContext';

function useRestApiData<T>(key): T {
  const { state } = useContext(RestDataContext);
  return state[key];
}

export default useRestApiData;
