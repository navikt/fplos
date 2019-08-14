import PropTypes from 'prop-types';

const oppgaverForForsteStonadsdagPropType = PropTypes.shape({
  forsteStonadsdag: PropTypes.string.isRequired,
  antall: PropTypes.number.isRequired,
});

export default oppgaverForForsteStonadsdagPropType;
