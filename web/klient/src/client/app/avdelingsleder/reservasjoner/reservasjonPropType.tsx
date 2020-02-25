import PropTypes from 'prop-types';

const reservasjonPropType = PropTypes.shape({
  reservertAvUid: PropTypes.string.isRequired,
  reservertAvNavn: PropTypes.string.isRequired,
  avdelingsnavn: PropTypes.arrayOf(PropTypes.string).isRequired,
});

export default reservasjonPropType;
