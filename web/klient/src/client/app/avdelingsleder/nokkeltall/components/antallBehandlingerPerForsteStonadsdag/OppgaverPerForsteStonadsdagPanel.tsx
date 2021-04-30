import React, { FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';
import { Element } from 'nav-frontend-typografi';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import OppgaverForForsteStonadsdag from 'types/avdelingsleder/oppgaverForForsteStonadsdagTsType';
import OppgaverPerForsteStonadsdagGraf from './OppgaverPerForsteStonadsdagGraf';

interface OwnProps {
  width: number;
  height: number;
  oppgaverPerForsteStonadsdag: OppgaverForForsteStonadsdag[];
}

/**
 * OppgaverPerForsteStonadsdagPanel.
 */
export const OppgaverPerForsteStonadsdagPanel: FunctionComponent<OwnProps> = ({
  width,
  height,
  oppgaverPerForsteStonadsdag,
}) => (
  <>
    <Element>
      <FormattedMessage id="OppgaverPerForsteStonadsdagPanel.FordeltPaForsteStonadsdag" />
    </Element>
    <VerticalSpacer sixteenPx />
    <OppgaverPerForsteStonadsdagGraf
      width={width}
      height={height}
      oppgaverPerForsteStonadsdag={oppgaverPerForsteStonadsdag}
    />
  </>
);

export default OppgaverPerForsteStonadsdagPanel;
