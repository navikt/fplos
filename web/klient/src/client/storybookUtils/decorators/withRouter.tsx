import React from 'react';
import { BrowserRouter } from 'react-router-dom';

const withRouterProvider = (Story: any) => (
  <BrowserRouter basename="/">
    <Story />
  </BrowserRouter>
);

export default withRouterProvider;
