import { RestApiConfigBuilder } from './rest-api';

export enum RestApiPathsKeys {
  KODEVERK = 'KODEVERK',
  NAV_ANSATT = 'NAV_ANSATT',
  FPSAK_URL = 'FPSAK_URL',
  FPTILBAKE_URL = 'FPTILBAKE_URL',
  AVDELINGER = 'AVDELINGER',
}

export const endpoints = new RestApiConfigBuilder()
  .withGet('/api/saksbehandler', RestApiPathsKeys.NAV_ANSATT)
  .withGet('/api/konfig/fpsak-url', RestApiPathsKeys.FPSAK_URL)
  .withGet('/api/konfig/fptilbake-url', RestApiPathsKeys.FPTILBAKE_URL)
  .withGet('/api/kodeverk', RestApiPathsKeys.KODEVERK)
  .withGet('/api/avdelingsleder/avdelinger', RestApiPathsKeys.AVDELINGER)
  .build();
