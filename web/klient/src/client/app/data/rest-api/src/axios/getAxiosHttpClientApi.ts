import axios from 'axios';

import initRestMethods from './initRestMethods';

/**
 * getAxiosHttpClientApi
 * Oppretter nytt http-klient api basert på Axios.
 */
const getAxiosHttpClientApi = () => {
  const axiosInstance = axios.create();

  // TODO (TOR) sentry bør ikkje vera ein avhengighet til pakka "rest-api". Konfigurer dette utanfor
  axiosInstance.interceptors.request.use((c): any => {
    const navCallId = `CallId_${(new Date()).getTime()}_${Math.floor(Math.random() * 1000000000)}`;
    const config = { ...c };
    config.headers['Nav-Callid'] = navCallId;
    return config;
  });

  const restMethods = initRestMethods(axiosInstance);
  return {
    get: restMethods.get,
    post: restMethods.post,
    put: restMethods.put,
    getBlob: restMethods.getBlob,
    postBlob: restMethods.postBlob,
    getAsync: restMethods.getAsync,
    postAsync: restMethods.postAsync,
    putAsync: restMethods.putAsync,
    axiosInstance,
  };
};

export default getAxiosHttpClientApi;
