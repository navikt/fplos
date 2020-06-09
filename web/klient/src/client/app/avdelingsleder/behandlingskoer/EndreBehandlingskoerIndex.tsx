import React, { FunctionComponent } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import Saksbehandler from 'avdelingsleder/saksbehandlere/saksbehandlerTsType';
import {
  setValgtSakslisteId, getValgtSakslisteId, resetValgtSakslisteId as reset,
} from './duck';
import EndreSakslisterPanel from './components/EndreSakslisterPanel';

interface OwnProps {
  valgtSakslisteId?: number;
  valgtAvdelingEnhet: string;
  avdelingensSaksbehandlere: Saksbehandler[];
  hentAvdelingensSaksbehandlere: (params: {avdelingEnhet: string}) => void;
}

interface DispatchProps {
  setValgtSakslisteId: (sakslisteId: number) => void;
  resetValgtSakslisteId: () => void;
}

/**
 * EndreBehandlingskoerIndex
 */
const EndreBehandlingskoerIndex: FunctionComponent<OwnProps & DispatchProps> = ({
  valgtAvdelingEnhet,
  valgtSakslisteId,
  setValgtSakslisteId: setValgtId,
  avdelingensSaksbehandlere,
  hentAvdelingensSaksbehandlere,
  resetValgtSakslisteId,
}) => (
  <EndreSakslisterPanel
    setValgtSakslisteId={setValgtId}
    valgtSakslisteId={valgtSakslisteId}
    valgtAvdelingEnhet={valgtAvdelingEnhet}
    avdelingensSaksbehandlere={avdelingensSaksbehandlere}
    hentAvdelingensSaksbehandlere={hentAvdelingensSaksbehandlere}
    resetValgtSakslisteId={resetValgtSakslisteId}
  />
);

const mapStateToProps = (state) => ({
  valgtSakslisteId: getValgtSakslisteId(state),
});

const mapDispatchToProps = (dispatch: Dispatch): DispatchProps => ({
  ...bindActionCreators<DispatchProps, any>({
    setValgtSakslisteId,
    resetValgtSakslisteId: reset,
  }, dispatch),
});


export default connect(mapStateToProps, mapDispatchToProps)(EndreBehandlingskoerIndex);
