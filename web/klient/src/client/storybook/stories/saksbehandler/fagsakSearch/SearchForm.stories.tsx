import React from 'react';
import { action } from '@storybook/addon-actions';

import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import SearchForm from 'saksbehandler/fagsakSearch/components/SearchForm';
import { RestApiGlobalDataProvider } from 'data/rest-api-hooks';

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
      <RestApiGlobalDataProvider initialState={initialState as {[key in RestApiGlobalStatePathsKeys]: any}}>
        {getStory()}
      </RestApiGlobalDataProvider>
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
  <RestApiGlobalDataProvider initialState={{
    [RestApiGlobalStatePathsKeys.NAV_ANSATT]: {
      kanSaksbehandle: false,
    },
  } as {[key in RestApiGlobalStatePathsKeys]: any}}
  >
    <SearchForm
      onSubmit={action('button-click')}
      searchStarted={false}
      resetSearch={action('button-click')}
    />
  </RestApiGlobalDataProvider>
);

export const skalViseSøkeskjemaNårSøkPågår = () => (
  <SearchForm
    onSubmit={action('button-click')}
    searchStarted
    resetSearch={action('button-click')}
  />
);
