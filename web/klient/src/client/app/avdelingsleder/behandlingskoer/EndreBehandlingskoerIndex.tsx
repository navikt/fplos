import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import Kodeverk from 'kodeverk/kodeverkTsType';
import { getValgtAvdelingEnhet } from 'app/duck';
import { fetchAvdelingensSaksbehandlere } from '../saksbehandlere/duck';
import {
  fetchAvdelingensSakslister, getAvdelingensSakslister, setValgtSakslisteId, getValgtSakslisteId, lagNySaksliste, getNySakslisteId,
  fjernSaksliste, lagreSakslisteNavn, lagreSakslisteBehandlingstype, knyttSaksbehandlerTilSaksliste,
  lagreSakslisteFagsakYtelseType, fetchAntallOppgaverForSaksliste, fetchAntallOppgaverForAvdeling, lagreSakslisteAndreKriterier,
} from './duck';
import EndreSakslisterPanel from './components/EndreSakslisterPanel';
import Saksliste from './sakslisteTsType';

interface OwnProps {
  sakslister: Saksliste[];
  valgtSakslisteId?: number;
  valgtAvdelingEnhet: string;
}

interface DispatchProps {
  fetchAvdelingensSakslister: (avdelingEnhet: string) => Saksliste[];
  fetchAntallOppgaverForSaksliste: (sakslisteId: number, avdelingEnhet: string) => Promise<string>;
  fetchAntallOppgaverForAvdeling: (avdelingEnhet: string) => Promise<string>;
  setValgtSakslisteId: (sakslisteId: number) => void;
  lagNySaksliste: (avdelingEnhet: string) => void;
  fjernSaksliste: (sakslisteId: number, avdelingEnhet: string) => void;
  lagreSakslisteNavn: (saksliste: {sakslisteId: number; navn: string}, avdelingEnhet: string) => void;
  lagreSakslisteBehandlingstype: (sakslisteId: number, behandlingType: Kodeverk, isChecked: boolean, avdelingEnhet: string) => void;
  lagreSakslisteFagsakYtelseType: (sakslisteId: number, fagsakYtelseType: string, avdelingEnhet: string) => void;
  knyttSaksbehandlerTilSaksliste: (sakslisteId: number, brukerIdent: string, isChecked: boolean, avdelingEnhet: string) => void;
  lagreSakslisteAndreKriterier: (sakslisteId: number, andreKriterierType: Kodeverk, isChecked: boolean, skalInkludere: boolean, avdelingEnhet: string) => void;
  fetchAvdelingensSaksbehandlere: (avdelingEnhet: string) => void;
}

/**
 * EndreBehandlingskoerIndex
 */
export class EndreBehandlingskoerIndex extends Component<OwnProps & DispatchProps> {
  static defaultProps = {
    sakslister: [],
    valgtSakslisteId: undefined,
  }

  componentDidMount = () => {
    const {
      fetchAvdelingensSakslister: fetchSakslister,
      fetchAvdelingensSaksbehandlere: fetchSaksbehandlere,
      fetchAntallOppgaverForAvdeling: fetchAntallOppgaver,
      valgtAvdelingEnhet,
    } = this.props;
    fetchSakslister(valgtAvdelingEnhet);
    fetchSaksbehandlere(valgtAvdelingEnhet);
    fetchAntallOppgaver(valgtAvdelingEnhet);
  }

  render = () => {
    const {
      sakslister, valgtSakslisteId, setValgtSakslisteId: setValgtId, lagNySaksliste: lagNyListe,
      fjernSaksliste: fjernListe, lagreSakslisteNavn: lagreListeNavn, lagreSakslisteBehandlingstype: lagreListeBehandlingstype,
      knyttSaksbehandlerTilSaksliste: knyttSaksbehandlerTilListe,
      lagreSakslisteFagsakYtelseType: lagreListeFagsakYtelseType,
      fetchAvdelingensSakslister: hentAvdelingensSakslister,
      fetchAntallOppgaverForSaksliste: hentAntallOppgaverForSaksliste,
      fetchAntallOppgaverForAvdeling: hentAntallOppgaverForAvdeling,
      lagreSakslisteAndreKriterier: lagreAndreKriterier,
    } = this.props;
    return (
      <EndreSakslisterPanel
        sakslister={sakslister}
        setValgtSakslisteId={setValgtId}
        valgtSakslisteId={valgtSakslisteId}
        lagNySaksliste={lagNyListe}
        fjernSaksliste={fjernListe}
        lagreSakslisteNavn={lagreListeNavn}
        lagreSakslisteBehandlingstype={lagreListeBehandlingstype}
        lagreSakslisteFagsakYtelseType={lagreListeFagsakYtelseType}
        lagreSakslisteAndreKriterier={lagreAndreKriterier}
        knyttSaksbehandlerTilSaksliste={knyttSaksbehandlerTilListe}
        hentAvdelingensSakslister={hentAvdelingensSakslister}
        hentAntallOppgaverForSaksliste={hentAntallOppgaverForSaksliste}
        hentAntallOppgaverForAvdeling={hentAntallOppgaverForAvdeling}
      />
    );
  }
}

const mapStateToProps = (state) => {
  const id = getValgtSakslisteId(state);
  const nyIdObject = getNySakslisteId(state);
  const nyId = nyIdObject ? parseInt(nyIdObject.sakslisteId, 10) : undefined;
  return {
    sakslister: getAvdelingensSakslister(state),
    valgtSakslisteId: id !== undefined ? id : nyId,
    valgtAvdelingEnhet: getValgtAvdelingEnhet(state),
  };
};

const mapDispatchToProps = (dispatch: Dispatch): DispatchProps => ({
  ...bindActionCreators<DispatchProps, any>({
    fetchAvdelingensSakslister,
    setValgtSakslisteId,
    lagNySaksliste,
    fjernSaksliste,
    lagreSakslisteNavn,
    lagreSakslisteBehandlingstype,
    lagreSakslisteFagsakYtelseType,
    lagreSakslisteAndreKriterier,
    knyttSaksbehandlerTilSaksliste,
    fetchAvdelingensSaksbehandlere,
    fetchAntallOppgaverForSaksliste,
    fetchAntallOppgaverForAvdeling,
  }, dispatch),
});


export default connect(mapStateToProps, mapDispatchToProps)(EndreBehandlingskoerIndex);
