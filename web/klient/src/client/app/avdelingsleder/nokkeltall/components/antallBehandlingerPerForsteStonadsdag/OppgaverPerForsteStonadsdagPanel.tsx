import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { FormattedMessage } from 'react-intl';
import { Element } from 'nav-frontend-typografi';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import OppgaverPerForsteStonadsdagGraf from './OppgaverPerForsteStonadsdagGraf';
import { getOppgaverPerForsteStonadsdag } from '../../duck';
import { OppgaverForForsteStonadsdag } from './oppgaverForForsteStonadsdagTsType';
import oppgaverForForsteStonadsdagPropType from './oppgaverForForsteStonadsdagPropType';

interface TsProps {
  width: number;
  height: number;
  oppgaverPerForsteStonadsdag?: OppgaverForForsteStonadsdag[];
}

/**
 * OppgaverPerForsteStonadsdagPanel.
 */
export const OppgaverPerForsteStonadsdagPanel = ({
  width,
  height,
  oppgaverPerForsteStonadsdag,
}: TsProps) => (
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

OppgaverPerForsteStonadsdagPanel.propTypes = {
  width: PropTypes.number.isRequired,
  height: PropTypes.number.isRequired,
  oppgaverPerForsteStonadsdag: PropTypes.arrayOf(oppgaverForForsteStonadsdagPropType),
};

OppgaverPerForsteStonadsdagPanel.defaultProps = {
  oppgaverPerForsteStonadsdag: [],
};

const mapStateToProps = state => ({
  oppgaverPerForsteStonadsdag: getOppgaverPerForsteStonadsdag(state),
});

export default connect(mapStateToProps)(OppgaverPerForsteStonadsdagPanel);
