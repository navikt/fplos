import React from 'react';

import FlexColumn from 'sharedComponents/flexGrid/FlexColumn';
import FlexContainer from 'sharedComponents/flexGrid/FlexContainer';
import FlexRow from 'sharedComponents/flexGrid/FlexRow';

export default {
  title: 'sharedComponents/FlexContainer',
  component: FlexContainer,
};

export const Default = () => (
  <FlexContainer>
    <FlexRow>
      <FlexColumn>
        Tekst 1
      </FlexColumn>
      <FlexColumn>
        Tekst 2
      </FlexColumn>
      <FlexColumn>
        Tekst 3
      </FlexColumn>
    </FlexRow>
    <FlexRow>
      <FlexColumn>
        Tekst 4
      </FlexColumn>
      <FlexColumn>
        Tekst 5
      </FlexColumn>
      <FlexColumn>
        Tekst 6
      </FlexColumn>
    </FlexRow>
  </FlexContainer>
);
