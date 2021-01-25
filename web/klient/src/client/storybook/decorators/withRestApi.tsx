import React from 'react';
import { RestApiProvider } from 'data/rest-api-hooks';
import { requestApi } from 'data/fplosRestApi';

const withRestApiProvider = (story) => {
  // Denne bør eigentleg ikkje ligga her. Har storybook afterEach?
  requestApi.clearAllMockData();

  return (
    <RestApiProvider>
      { story() }
    </RestApiProvider>
  );
};

export default withRestApiProvider;
