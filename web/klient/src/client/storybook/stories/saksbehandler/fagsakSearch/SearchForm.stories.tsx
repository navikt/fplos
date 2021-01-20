import React from 'react';
import { action } from '@storybook/addon-actions';

import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import SearchForm from 'saksbehandler/fagsakSearch/components/SearchForm';
import { RestApiProvider } from 'data/rest-api-hooks';

import RequestMock from '../../../mocks/RequestMock';
import withIntl from '../../../decorators/withIntl';

const initialState = {
  [RestApiGlobalStatePathsKeys.NAV_ANSATT]: {
    kanSaksbehandle: true,
  },
};

export default {
  title: 'saksbehandler/fagsakSearch/SearchForm',
  component: SearchForm,
  decorators: [
    withIntl,
    (getStory) => (
      <RestApiProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}} requestApi={new RequestMock().build()}>
        {getStory()}
      </RestApiProvider>
    ),
  ],
};

export const skalViseSøkeskjema = () => (
  <SearchForm
    onSubmit={action('button-click')}
    searchStarted={false}
    resetSearch={action('button-click')}
  />
);

export const skalViseSøkeskjemaNårEnIkkeKanVelgeÅReservere = () => (
  <RestApiProvider
    initialState={{
      [RestApiGlobalStatePathsKeys.NAV_ANSATT]: {
        kanSaksbehandle: false,
      },
    } as {[key in RestApiGlobalStatePathsKeys]: any}}
    requestApi={new RequestMock().build()}
  >
    <SearchForm
      onSubmit={action('button-click')}
      searchStarted={false}
      resetSearch={action('button-click')}
    />
  </RestApiProvider>
);

export const skalViseSøkeskjemaNårSøkPågår = () => (
  <SearchForm
    onSubmit={action('button-click')}
    searchStarted
    resetSearch={action('button-click')}
  />
);
