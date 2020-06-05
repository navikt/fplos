import React from 'react';
import { action } from '@storybook/addon-actions';

import { RestApiPathsKeys } from 'data/restApiPaths';
import SearchForm from 'saksbehandler/fagsakSearch/components/SearchForm';
import { RestDataProvider } from 'data/RestDataContext';

import withIntl from '../../../decorators/withIntl';

const initialState = {
  [RestApiPathsKeys.NAV_ANSATT]: {
    kanSaksbehandle: true,
  },
};

export default {
  title: 'saksbehandler/fagsakSearch/SearchForm',
  component: SearchForm,
  decorators: [
    withIntl,
    (getStory) => <RestDataProvider initialState={initialState as {[key in RestApiPathsKeys]: any}}>{getStory()}</RestDataProvider>,
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
  <RestDataProvider initialState={{
    [RestApiPathsKeys.NAV_ANSATT]: {
      kanSaksbehandle: false,
    },
  } as {[key in RestApiPathsKeys]: any}}
  >
    <SearchForm
      onSubmit={action('button-click')}
      searchStarted={false}
      resetSearch={action('button-click')}
    />
  </RestDataProvider>
);

export const skalViseSøkeskjemaNårSøkPågår = () => (
  <SearchForm
    onSubmit={action('button-click')}
    searchStarted
    resetSearch={action('button-click')}
  />
);
