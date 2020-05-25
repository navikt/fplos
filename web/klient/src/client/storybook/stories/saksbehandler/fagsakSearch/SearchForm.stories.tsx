import React from 'react';
import { action } from '@storybook/addon-actions';

import { SearchForm } from 'saksbehandler/fagsakSearch/components/SearchForm';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/fagsakSearch/SearchForm',
  component: SearchForm,
  decorators: [withIntl],
};

export const skalViseSøkeskjema = (intl) => (
  <SearchForm
    intl={intl}
    onSubmit={action('button-click')}
    searchStarted={false}
    resetSearch={action('button-click')}
    kanSaksbehandle
  />
);

export const skalViseSøkeskjemaNårEnIkkeKanVelgeÅReservere = (intl) => (
  <SearchForm
    intl={intl}
    onSubmit={action('button-click')}
    searchStarted={false}
    resetSearch={action('button-click')}
    kanSaksbehandle={false}
  />
);

export const skalViseSøkeskjemaNårSøkPågår = (intl) => (
  <SearchForm
    intl={intl}
    onSubmit={action('button-click')}
    searchStarted
    resetSearch={action('button-click')}
    kanSaksbehandle
  />
);
