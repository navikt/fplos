import React from 'react';
import { action } from '@storybook/addon-actions';

import { requestApi, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import SearchForm from 'saksbehandler/fagsakSearch/components/SearchForm';

import withRestApiProvider from '../../../decorators/withRestApi';
import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/fagsakSearch/SearchForm',
  component: SearchForm,
  decorators: [
    withIntl,
    withRestApiProvider,
  ],
};

export const skalViseSøkeskjema = () => {
  requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT.name, {
    kanSaksbehandle: true,
  });
  return (
    <SearchForm
      onSubmit={action('button-click')}
      searchStarted={false}
      resetSearch={action('button-click')}
    />
  );
};

export const skalViseSøkeskjemaNårEnIkkeKanVelgeÅReservere = () => {
  requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT.name, {
    kanSaksbehandle: false,
  });
  return (
    <SearchForm
      onSubmit={action('button-click')}
      searchStarted={false}
      resetSearch={action('button-click')}
    />
  );
};

export const skalViseSøkeskjemaNårSøkPågår = () => {
  requestApi.mock(RestApiGlobalStatePathsKeys.NAV_ANSATT.name, {
    kanSaksbehandle: true,
  });
  return (
    <SearchForm
      onSubmit={action('button-click')}
      searchStarted
      resetSearch={action('button-click')}
    />
  );
};
