import React from 'react';
import { RestApiProvider } from 'data/rest-api-hooks';

const withRestApiProvider = (story: any) => (
  <RestApiProvider>
    { story() }
  </RestApiProvider>
);

export default withRestApiProvider;
