import PropTypes from 'prop-types';

const reservasjonPropType = PropTypes.shape({
  brukerIdent: PropTypes.string.isRequired,
  navn: PropTypes.string.isRequired,
  avdelingsnavn: PropTypes.arrayOf(PropTypes.string).isRequired,
});

export default reservasjonPropType;
