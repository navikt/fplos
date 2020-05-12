import PropTypes from 'prop-types';

const saksbehandlerPropType = PropTypes.shape({
  brukerIdent: PropTypes.shape({
    brukerIdent: PropTypes.string.isRequired,
    verdi: PropTypes.string.isRequired,
  }).isRequired,
  navn: PropTypes.string.isRequired,
  avdelingsnavn: PropTypes.arrayOf(PropTypes.string),
});

export default saksbehandlerPropType;
