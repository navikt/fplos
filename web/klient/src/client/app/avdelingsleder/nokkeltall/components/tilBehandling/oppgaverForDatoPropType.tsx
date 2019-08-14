import PropTypes from 'prop-types';

import kodeverkPropType from 'kodeverk/kodeverkPropType';

const oppgaverForDatoPropType = PropTypes.shape({
  fagsakYtelseType: kodeverkPropType.isRequired,
  behandlingType: kodeverkPropType.isRequired,
  opprettetDato: PropTypes.string.isRequired,
  antall: PropTypes.number.isRequired,
});

export default oppgaverForDatoPropType;
