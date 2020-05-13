import React, { FunctionComponent } from 'react';
import { connect } from 'react-redux';
import { FormattedMessage } from 'react-intl';
import { Element } from 'nav-frontend-typografi';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import OppgaverPerForsteStonadsdagGraf from './OppgaverPerForsteStonadsdagGraf';
import { getOppgaverPerForsteStonadsdag } from '../../duck';
import OppgaverForForsteStonadsdag from './oppgaverForForsteStonadsdagTsType';

interface OwnProps {
  width: number;
  height: number;
  oppgaverPerForsteStonadsdag?: OppgaverForForsteStonadsdag[];
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

OppgaverPerForsteStonadsdagPanel.defaultProps = {
  oppgaverPerForsteStonadsdag: [],
};

const mapStateToProps = (state) => ({
  oppgaverPerForsteStonadsdag: getOppgaverPerForsteStonadsdag(state),
});

export default connect(mapStateToProps)(OppgaverPerForsteStonadsdagPanel);
