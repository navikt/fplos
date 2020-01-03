import PropTypes from 'prop-types';

import kodeverkPropType from 'kodeverk/kodeverkPropType';

const sakslistePropType = PropTypes.shape({
  sakslisteId: PropTypes.number.isRequired,
  navn: PropTypes.string,
  behandlingTyper: PropTypes.arrayOf(kodeverkPropType),
  fagsakYtelseTyper: PropTypes.arrayOf(kodeverkPropType),
  sistEndret: PropTypes.string.isRequired,
  sortering: PropTypes.shape({
    sorteringType: kodeverkPropType.isRequired,
    fra: PropTypes.number,
    til: PropTypes.number,
    fomDato: PropTypes.string,
    tomDato: PropTypes.string,
    erDynamiskPeriode: PropTypes.bool.isRequired,
  }),
  andreKriterier: PropTypes.arrayOf(PropTypes.shape({
    andreKriterierType: kodeverkPropType,
    inkluder: PropTypes.bool.isRequired,
  })),
  saksbehandlerIdenter: PropTypes.arrayOf(PropTypes.string).isRequired,
  antallBehandlinger: PropTypes.number,
});

export default sakslistePropType;
