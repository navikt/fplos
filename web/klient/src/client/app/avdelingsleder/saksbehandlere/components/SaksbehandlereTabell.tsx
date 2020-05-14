import React, { Component } from 'react';
import { FormattedMessage } from 'react-intl';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { Normaltekst, Element } from 'nav-frontend-typografi';

import { getValgtAvdelingEnhet } from 'app/duck';
import Image from 'sharedComponents/Image';
import removeIcon from 'images/remove.svg';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import Table from 'sharedComponents/table/Table';
import TableRow from 'sharedComponents/table/TableRow';
import TableColumn from 'sharedComponents/table/TableColumn';
import SletteSaksbehandlerModal from './SletteSaksbehandlerModal';
import Saksbehandler from '../saksbehandlerTsType';

import styles from './saksbehandlereTabell.less';

const headerTextCodes = [
  'SaksbehandlereTabell.Navn',
  'SaksbehandlereTabell.Brukerident',
  'SaksbehandlereTabell.Avdeling',
];

interface OwnProps {
  saksbehandlere: Saksbehandler[];
  fjernSaksbehandler: (brukerIdent: string, avdelingEnhet: string) => (dispatch: Dispatch) => Promise<string>;
  valgtAvdelingEnhet: string;
}

interface StateProps {
  valgtSaksbehandler?: Saksbehandler;
}

/**
 * SaksbehandlereTabell
 */
export class SaksbehandlereTabell extends Component<OwnProps, StateProps> {
  constructor(props: OwnProps) {
    super(props);

    this.state = {
      valgtSaksbehandler: undefined,
    };
  }

  showSletteSaksbehandlerModal = (saksbehandler: Saksbehandler) => {
    this.setState((prevState) => ({ ...prevState, valgtSaksbehandler: saksbehandler }));
  }

  closeSletteModal = () => {
    this.setState((prevState) => ({ ...prevState, valgtSaksbehandler: undefined }));
  }

  fjernSaksbehandler = (valgtSaksbehandler: Saksbehandler) => {
    const {
      fjernSaksbehandler, valgtAvdelingEnhet,
    } = this.props;
    fjernSaksbehandler(valgtSaksbehandler.brukerIdent, valgtAvdelingEnhet);
    this.closeSletteModal();
  }

  render = () => {
    const {
      saksbehandlere,
    } = this.props;
    const {
      valgtSaksbehandler,
    } = this.state;

    const sorterteSaksbehandlere = saksbehandlere.sort((saksbehandler1, saksbehandler2) => saksbehandler1.navn.localeCompare(saksbehandler2.navn));

    return (
      <>
        <Element><FormattedMessage id="SaksbehandlereTabell.Saksbehandlere" /></Element>
        {sorterteSaksbehandlere.length === 0 && (
          <>
            <VerticalSpacer eightPx />
            <Normaltekst><FormattedMessage id="SaksbehandlereTabell.IngenSaksbehandlere" /></Normaltekst>
            <VerticalSpacer eightPx />
          </>
        )}
        {sorterteSaksbehandlere.length > 0 && (
        <Table headerTextCodes={headerTextCodes} noHover>
          {sorterteSaksbehandlere.map((saksbehandler) => (
            <TableRow key={saksbehandler.brukerIdent}>
              <TableColumn>{saksbehandler.navn}</TableColumn>
              <TableColumn>{saksbehandler.brukerIdent}</TableColumn>
              <TableColumn>{saksbehandler.avdelingsnavn.join(', ')}</TableColumn>
              <TableColumn>
                <Image
                  src={removeIcon}
                  className={styles.removeImage}
                  onMouseDown={() => this.showSletteSaksbehandlerModal(saksbehandler)}
                  onKeyDown={() => this.showSletteSaksbehandlerModal(saksbehandler)}
                />
              </TableColumn>
            </TableRow>
          ))}
        </Table>
        )}
        {valgtSaksbehandler && (
        <SletteSaksbehandlerModal
          valgtSaksbehandler={valgtSaksbehandler}
          closeSletteModal={this.closeSletteModal}
          fjernSaksbehandler={this.fjernSaksbehandler}
        />
        )}
      </>
    );
  }
}

const mapStateToProps = (state) => ({
  valgtAvdelingEnhet: getValgtAvdelingEnhet(state),
});

export default connect(mapStateToProps)(SaksbehandlereTabell);
