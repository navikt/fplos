import PropTypes from 'prop-types';

const reservasjonPropType = PropTypes.shape({
  reservertAvUid: PropTypes.string.isRequired,
  reservertAvNavn: PropTypes.string.isRequired,
  reservertTilTidspunkt: PropTypes.string.isRequired,
  oppgaveId: PropTypes.number.isRequired,
  oppgaveSaksNr: PropTypes.number.isRequired,
});

export default reservasjonPropType;
