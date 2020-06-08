import React, { FunctionComponent } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import Saksbehandler from 'avdelingsleder/saksbehandlere/saksbehandlerTsType';
import {
  setValgtSakslisteId, getValgtSakslisteId,
} from './duck';
import EndreSakslisterPanel from './components/EndreSakslisterPanel';

interface OwnProps {
  valgtSakslisteId?: number;
  valgtAvdelingEnhet: string;
  avdelingensSaksbehandlere: Saksbehandler[];
}

interface DispatchProps {
  setValgtSakslisteId: (sakslisteId: number) => void;
}

/**
 * EndreBehandlingskoerIndex
 */
const EndreBehandlingskoerIndex: FunctionComponent<OwnProps & DispatchProps> = ({
  valgtAvdelingEnhet,
  valgtSakslisteId,
  setValgtSakslisteId: setValgtId,
  avdelingensSaksbehandlere,
}) => (
  <EndreSakslisterPanel
    setValgtSakslisteId={setValgtId}
    valgtSakslisteId={valgtSakslisteId}
    valgtAvdelingEnhet={valgtAvdelingEnhet}
    avdelingensSaksbehandlere={avdelingensSaksbehandlere}
  />
);

const mapStateToProps = (state) => ({
  valgtSakslisteId: getValgtSakslisteId(state),
});

const mapDispatchToProps = (dispatch: Dispatch): DispatchProps => ({
  ...bindActionCreators<DispatchProps, any>({
    setValgtSakslisteId,
  }, dispatch),
});


export default connect(mapStateToProps, mapDispatchToProps)(EndreBehandlingskoerIndex);
