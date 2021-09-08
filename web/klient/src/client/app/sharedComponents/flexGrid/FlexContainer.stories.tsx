import React from 'react';

import FlexColumn from './FlexColumn';
import FlexContainer from './FlexContainer';
import FlexRow from './FlexRow';

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
