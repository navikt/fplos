import PropTypes from 'prop-types';

import kodeverkPropType from 'kodeverk/kodeverkPropType';

const oppgaverForAvdelingPropType = PropTypes.shape({
  fagsakYtelseType: kodeverkPropType.isRequired,
  behandlingType: kodeverkPropType.isRequired,
  tilBehandling: PropTypes.bool.isRequired,
  antall: PropTypes.number.isRequired,
});

export default oppgaverForAvdelingPropType;
