import React, { FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';
import { Element } from 'nav-frontend-typografi';

import { VerticalSpacer } from '@navikt/ft-ui-komponenter';
import OppgaverForForsteStonadsdag from 'types/avdelingsleder/oppgaverForForsteStonadsdagTsType';
import OppgaverPerForsteStonadsdagGraf from './OppgaverPerForsteStonadsdagGraf';

interface OwnProps {
  height: number;
  oppgaverPerForsteStonadsdag: OppgaverForForsteStonadsdag[];
}

/**
 * OppgaverPerForsteStonadsdagPanel.
 */
export const OppgaverPerForsteStonadsdagPanel: FunctionComponent<OwnProps> = ({
  height,
  oppgaverPerForsteStonadsdag,
}) => (
  <>
    <Element>
      <FormattedMessage id="OppgaverPerForsteStonadsdagPanel.FordeltPaForsteStonadsdag" />
    </Element>
    <VerticalSpacer sixteenPx />
    <OppgaverPerForsteStonadsdagGraf
      height={height}
      oppgaverPerForsteStonadsdag={oppgaverPerForsteStonadsdag}
    />
  </>
);

export default OppgaverPerForsteStonadsdagPanel;
