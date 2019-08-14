import PropTypes from 'prop-types';

import kodeverkPropType from 'kodeverk/kodeverkPropType';

const oppgaverManueltPaVentPropType = PropTypes.shape({
  fagsakYtelseType: kodeverkPropType.isRequired,
  behandlingFrist: PropTypes.string.isRequired,
  antall: PropTypes.number.isRequired,
});

export default oppgaverManueltPaVentPropType;
