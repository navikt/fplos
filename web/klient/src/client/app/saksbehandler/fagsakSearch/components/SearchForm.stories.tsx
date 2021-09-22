import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';

import RestApiMock from 'storybookUtils/RestApiMock';
import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';

import SearchForm from './SearchForm';

export default {
  title: 'saksbehandler/fagsakSearch/SearchForm',
  component: SearchForm,
  decorators: [
    withIntl,
    withRestApiProvider,
  ],
};

const Template: Story<{
  kanSaksbehandle: boolean,
  searchStarted: boolean,
  onSubmit: (values: { searchString: string, skalReservere: boolean }) => void,
}> = ({
  kanSaksbehandle,
  searchStarted,
  onSubmit,
}) => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.NAV_ANSATT.name, data: { kanSaksbehandle } },
  ];

  return (
    <RestApiMock data={data}>
      <SearchForm
        onSubmit={onSubmit}
        searchStarted={searchStarted}
        resetSearch={action('button-click')}
      />
    </RestApiMock>
  );
};

export const Søkeskjema = Template.bind({});
Søkeskjema.args = {
  onSubmit: action('button-click'),
  kanSaksbehandle: true,
  searchStarted: false,
};

export const SøkeskjemaNårEnIkkeKanVelgeÅReservere = Template.bind({});
SøkeskjemaNårEnIkkeKanVelgeÅReservere.args = {
  onSubmit: action('button-click'),
  kanSaksbehandle: false,
  searchStarted: false,
};

export const SøkeskjemaNårSøkPågår = Template.bind({});
SøkeskjemaNårSøkPågår.args = {
  onSubmit: action('button-click'),
  kanSaksbehandle: true,
  searchStarted: true,
};