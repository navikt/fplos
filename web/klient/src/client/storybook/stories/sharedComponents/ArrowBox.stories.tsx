import React from 'react';

import ArrowBox from 'sharedComponents/ArrowBox';

export default {
  title: 'sharedComponents/ArrowBox',
  component: ArrowBox,
};

export const MedPilPåToppen = () => (
  <div style={{ width: '200px' }}>
    <ArrowBox>Dette er en tekst</ArrowBox>
  </div>
);
