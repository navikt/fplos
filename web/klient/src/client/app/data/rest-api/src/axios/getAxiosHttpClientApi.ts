import axios, { AxiosResponse } from 'axios';
import HttpClientApi from '../HttpClientApiTsType';

import initRestMethods from './initRestMethods';

const konverterKodeverkTilKode = (data: any) => {
  if (data === undefined || data === null) {
    return;
  }
  Object.keys(data).forEach((key) => {
    if (data[key]?.kode && data[key]?.kodeverk && Object.keys(data[key]).length === 3) {
      // eslint-disable-next-line no-param-reassign
      data[key] = data[key].kode;
    }
    if (typeof data[key] === 'object' && data[key] !== null) {
      konverterKodeverkTilKode(data[key]);
    }
  });
};

/**
 * getAxiosHttpClientApi
 * Oppretter nytt http-klient api basert på Axios.
 */
const getAxiosHttpClientApi = (): HttpClientApi => {
  const axiosInstance = axios.create();

  // TODO (TOR) sentry bør ikkje vera ein avhengighet til pakka "rest-api". Konfigurer dette utanfor
  axiosInstance.interceptors.request.use((c): any => {
    const navCallId = `CallId_${(new Date()).getTime()}_${Math.floor(Math.random() * 1000000000)}`;
    const config = { ...c };
    if (config.headers) {
      config.headers['Nav-Callid'] = navCallId;
    }
    return config;
  });

  // TODO Temp kode til backend returnerer string i staden for Kodeverk
  axiosInstance.interceptors.response.use((response: AxiosResponse): any => {
    if (response.status === 200 && response.config.url && response.config.url.includes('/api/') && !response.config.url.includes('/api/kodeverk')) {
      konverterKodeverkTilKode(response.data);
    }
    return response;
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
