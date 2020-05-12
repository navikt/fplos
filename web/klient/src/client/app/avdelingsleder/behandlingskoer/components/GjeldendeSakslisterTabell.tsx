
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { FormattedMessage } from 'react-intl';
import {
  Normaltekst, Undertekst, Element, Undertittel,
} from 'nav-frontend-typografi';

import { getValgtAvdelingEnhet } from 'app/duck';
import { getKodeverk } from 'kodeverk/duck';
import { Kodeverk } from 'kodeverk/kodeverkTsType';
import kodeverkPropType from 'kodeverk/kodeverkPropType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import Image from 'sharedComponents/Image';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import Table from 'sharedComponents/Table';
import TableRow from 'sharedComponents/TableRow';
import TableColumn from 'sharedComponents/TableColumn';
import DateLabel from 'sharedComponents/DateLabel';
import addCircleIcon from 'images/add-circle.svg';
import removeIcon from 'images/remove.svg';
import { Column, Row } from 'nav-frontend-grid';
import SletteSakslisteModal from './SletteSakslisteModal';
import { Saksliste } from '../sakslisteTsType';
import sakslistePropType from '../sakslistePropType';
import { getAntallOppgaverForAvdelingResultat } from '../duck';

import styles from './gjeldendeSakslisterTabell.less';

const headerTextCodes = [
  'GjeldendeSakslisterTabell.Listenavn',
  'GjeldendeSakslisterTabell.Stonadstype',
  'GjeldendeSakslisterTabell.Behandlingtype',
  'GjeldendeSakslisterTabell.AntallSaksbehandlere',
  'GjeldendeSakslisterTabell.AntallBehandlinger',
  'GjeldendeSakslisterTabell.SistEndret',
  'EMPTY_1',
];

interface TsProps {
  sakslister: Saksliste[];
  setValgtSakslisteId: (sakslisteId: number) => void;
  lagNySaksliste: (avdelingEnhet: string) => void;
  fjernSaksliste: (sakslisteId: number, avdelingEnhet: string) => void;
  valgtSakslisteId?: number;
  behandlingTyper: Kodeverk[];
  fagsakYtelseTyper: Kodeverk[];
  valgtAvdelingEnhet: string;
  hentAvdelingensSakslister: (avdelingEnhet: string) => Saksliste[];
  oppgaverForAvdeling?: number;
  hentAntallOppgaverForAvdeling: (avdelingEnhet: string) => Promise<string>;
}

interface StateTsProps {
  valgtSaksliste?: Saksliste;
}

const wait = ms => new Promise(resolve => setTimeout(resolve, ms));

/**
 * GjeldendeSakslisterTabell
 */
export class GjeldendeSakslisterTabell extends Component<TsProps, StateTsProps> {
  static propTypes = {
    sakslister: PropTypes.arrayOf(sakslistePropType).isRequired,
    setValgtSakslisteId: PropTypes.func.isRequired,
    lagNySaksliste: PropTypes.func.isRequired,
    fjernSaksliste: PropTypes.func.isRequired,
    valgtSakslisteId: PropTypes.number,
    behandlingTyper: PropTypes.arrayOf(kodeverkPropType).isRequired,
    fagsakYtelseTyper: PropTypes.arrayOf(kodeverkPropType).isRequired,
    valgtAvdelingEnhet: PropTypes.string.isRequired,
    hentAvdelingensSakslister: PropTypes.func.isRequired,
    oppgaverForAvdeling: PropTypes.number,
    hentAntallOppgaverForAvdeling: PropTypes.func.isRequired,
  };

  static defaultProps = {
    valgtSakslisteId: undefined,
  }

  constructor(props: TsProps) {
    super(props);

    this.state = {
      valgtSaksliste: undefined,
    };
    this.nodes = [];
  }

  componentDidMount = () => {
    const {
      hentAntallOppgaverForAvdeling, valgtAvdelingEnhet,
    } = this.props;
    hentAntallOppgaverForAvdeling(valgtAvdelingEnhet);
  }

  setValgtSaksliste = async (event: Event, id: number) => {
    const { setValgtSakslisteId, hentAvdelingensSakslister, valgtAvdelingEnhet } = this.props;
    if (this.nodes.some(node => node && node.contains(event.target))) {
      return;
    }

    // Må vente 100 ms før en byttar behandlingskø i tabell. Dette fordi lagring av navn skjer som blur-event. Så i tilfellet
    // der en endrer navn og så trykker direkte på en annen behandlingskø vil ikke lagringen skje før etter at ny kø er valgt.
    await wait(100);

    setValgtSakslisteId(id);
    hentAvdelingensSakslister(valgtAvdelingEnhet);
  }

  lagNySaksliste = (event: KeyboardEvent) => {
    if (event.keyCode === 13) {
      const { lagNySaksliste, valgtAvdelingEnhet } = this.props;
      lagNySaksliste(valgtAvdelingEnhet);
    }
  };

  visFjernSakslisteModal = (valgtSaksliste: Saksliste) => {
    this.setState(prevState => ({ ...prevState, valgtSaksliste }));
  }

  closeSletteModal = () => {
    this.setState(prevState => ({ ...prevState, valgtSaksliste: undefined }));
  }

  fjernSaksliste = (saksliste: Saksliste) => {
    const {
      fjernSaksliste, valgtAvdelingEnhet,
    } = this.props;
    this.closeSletteModal();
    fjernSaksliste(saksliste.sakslisteId, valgtAvdelingEnhet);
  }

  formatStonadstyper = (valgteFagsakYtelseTyper?: Kodeverk[]) => {
    if (!valgteFagsakYtelseTyper || valgteFagsakYtelseTyper.length === 0) {
      return <FormattedMessage id="GjeldendeSakslisterTabell.Alle" />;
    }

    const { fagsakYtelseTyper } = this.props;
    return valgteFagsakYtelseTyper.map((fyt) => {
      const type = fagsakYtelseTyper.find(def => def.kode === fyt.kode);
      return type ? type.navn : '';
    }).join(', ');
  };

  formatBehandlingstyper = (valgteBehandlingTyper?: Kodeverk[]) => {
    const { behandlingTyper } = this.props;

    if (!valgteBehandlingTyper || valgteBehandlingTyper.length === 0
      || valgteBehandlingTyper.length === behandlingTyper.length) {
      return <FormattedMessage id="GjeldendeSakslisterTabell.Alle" />;
    }

    return valgteBehandlingTyper.map((bt) => {
      const type = behandlingTyper.find(def => def.kode === bt.kode);
      return type ? type.navn : '';
    }).join(', ');
  };

  nodes: any[];

  render = () => {
    const {
      sakslister, valgtSakslisteId, lagNySaksliste, valgtAvdelingEnhet, oppgaverForAvdeling,
    } = this.props;
    const {
      valgtSaksliste,
    } = this.state;

    return (
      <>

        <Row>
          <Column xs="9">
            <Element>
              <FormattedMessage id="GjeldendeSakslisterTabell.GjeldendeLister" />
            </Element>
          </Column>
          <Column xs="3">
            <div className={styles.grayBox}>
              <Normaltekst>
                <FormattedMessage id="GjeldendeSakslisterTabell.OppgaverForAvdeling" />
              </Normaltekst>
              <Undertittel>{oppgaverForAvdeling || '0'}</Undertittel>
            </div>
          </Column>
        </Row>
        {sakslister.length === 0 && (
          <>
            <VerticalSpacer eightPx />
            <Normaltekst><FormattedMessage id="GjeldendeSakslisterTabell.IngenLister" /></Normaltekst>
            <VerticalSpacer eightPx />
          </>
        )
        }
        {sakslister.length > 0 && (
        <Table headerTextCodes={headerTextCodes}>
          {sakslister.map(saksliste => (
            <TableRow
              key={saksliste.sakslisteId}
              className={saksliste.sakslisteId === valgtSakslisteId ? styles.isSelected : undefined}
              id={saksliste.sakslisteId}
              onMouseDown={this.setValgtSaksliste}
              onKeyDown={this.setValgtSaksliste}
            >
              <TableColumn>{saksliste.navn}</TableColumn>
              <TableColumn>{this.formatStonadstyper(saksliste.fagsakYtelseTyper)}</TableColumn>
              <TableColumn>{this.formatBehandlingstyper(saksliste.behandlingTyper)}</TableColumn>
              <TableColumn>{saksliste.saksbehandlerIdenter.length > 0 ? saksliste.saksbehandlerIdenter.length : ''}</TableColumn>
              <TableColumn>{saksliste.antallBehandlinger}</TableColumn>
              <TableColumn>
                <DateLabel dateString={saksliste.sistEndret} />
              </TableColumn>
              <TableColumn>
                <div ref={(node) => { this.nodes.push(node); }}>
                  <Image
                    src={removeIcon}
                    className={styles.removeImage}
                    onMouseDown={() => this.visFjernSakslisteModal(saksliste)}
                    onKeyDown={() => this.visFjernSakslisteModal(saksliste)}
                    tabIndex="0"
                  />
                </div>
              </TableColumn>
            </TableRow>
          ))}
        </Table>
        )}
        <div
          id="leggTilListe"
          role="button"
          tabIndex={0}
          className={styles.addPeriode}
          onClick={() => lagNySaksliste(valgtAvdelingEnhet)}
          onKeyDown={this.lagNySaksliste}
        >
          <Image className={styles.addCircleIcon} src={addCircleIcon} />
          <Undertekst className={styles.imageText}>
            <FormattedMessage id="GjeldendeSakslisterTabell.LeggTilListe" />
          </Undertekst>
        </div>
        {valgtSaksliste && (
          <SletteSakslisteModal
            valgtSaksliste={valgtSaksliste}
            cancel={this.closeSletteModal}
            submit={this.fjernSaksliste}
          />
        )}
      </>
    );
  }
}

const mapStateToProps = state => ({
  behandlingTyper: getKodeverk(kodeverkTyper.BEHANDLING_TYPE)(state),
  fagsakYtelseTyper: getKodeverk(kodeverkTyper.FAGSAK_YTELSE_TYPE)(state),
  valgtAvdelingEnhet: getValgtAvdelingEnhet(state),
  oppgaverForAvdeling: getAntallOppgaverForAvdelingResultat(state),
});

export default connect(mapStateToProps)(GjeldendeSakslisterTabell);
