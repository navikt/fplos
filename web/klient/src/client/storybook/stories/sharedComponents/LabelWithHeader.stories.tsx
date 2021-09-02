import React from 'react';

import LabelWithHeader from 'sharedComponents/LabelWithHeader';

export default {
  title: 'sharedComponents/LabelWithHeader',
  component: LabelWithHeader,
};

export const Default = () => (
  <LabelWithHeader header="Dette er en header" texts={['Dette er tekst 1', 'Dette er tekst 2']} />
);
