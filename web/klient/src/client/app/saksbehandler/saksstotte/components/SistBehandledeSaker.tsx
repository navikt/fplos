
import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { FormattedMessage } from 'react-intl';
import { Undertittel, Normaltekst } from 'nav-frontend-typografi';
import Lenke from 'nav-frontend-lenker';

import { getFpsakHref, getFptilbakeHref } from 'app/paths';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { getFpsakUrl, getFptilbakeUrl, hentFpsakBehandlingId as hentFpsakBehandlingIdActionCreator } from 'app/duck';
import { getBehandledeOppgaver } from 'saksbehandler/saksstotte/duck';
import { bindActionCreators, Dispatch } from 'redux';
import { connect } from 'react-redux';
import { Oppgave } from '../../oppgaveTsType';
import oppgavePropType from '../../oppgavePropType';

const getClickEvent = (openFpsak, oppgave) => () => openFpsak(oppgave);

type TsProps = Readonly<{
  fpsakUrl: string;
  fptilbakeUrl: string;
  sistBehandledeSaker: Oppgave[];
  hentFpsakBehandlingId: (uuid: string) => Promise<{payload: number }>;
}>

interface StateProps {
  sistBehandledeSaker: Oppgave[];
}
/**
 * SistBehandledeSaker
 *
 * Denne komponenten viser de tre siste fagsakene en nav-ansatt har behandlet.
 */
export class SistBehandledeSaker extends Component<TsProps, StateProps> {
  static propTypes = {
    fpsakUrl: PropTypes.string.isRequired,
    fptilbakeUrl: PropTypes.string.isRequired,
    sistBehandledeSaker: PropTypes.arrayOf(oppgavePropType).isRequired,
    hentFpsakBehandlingId: PropTypes.func.isRequired,
  };

  openFpsak = (oppgave: Oppgave) => {
    const { fpsakUrl, fptilbakeUrl, hentFpsakBehandlingId } = this.props;

    if (oppgave.system === 'FPSAK') {
      hentFpsakBehandlingId(oppgave.eksternId)
        .then((data: { payload: number }) => window.location.assign(getFpsakHref(fpsakUrl, oppgave.saksnummer, data.payload)));
    } else if (oppgave.system === 'FPTILBAKE') {
      window.location.assign(getFptilbakeHref(fptilbakeUrl, oppgave.href));
    } else throw new Error('Fagsystemet for oppgaven er ukjent');
  }

  render = () => {
    const {
      sistBehandledeSaker,
    } = this.props;
    return (
      <>
        <Undertittel><FormattedMessage id="SistBehandledeSaker.SistBehandledeSaker" /></Undertittel>
        <VerticalSpacer eightPx />
        {sistBehandledeSaker.length === 0
        && <Normaltekst><FormattedMessage id="SistBehandledeSaker.IngenBehandlinger" /></Normaltekst>
        }
        {sistBehandledeSaker.map((sbs, index) => (
          <Fragment key={sbs.eksternId}>
            <Normaltekst>
              {sbs.navn
                ? (
                  <Lenke
                    href="#"
                    onClick={getClickEvent(this.openFpsak, sbs)}
                  >
                    {`${sbs.navn} ${sbs.personnummer}`}
                  </Lenke>
)
                : (
                  <Lenke href="#" onClick={getClickEvent(this.openFpsak, sbs)}>
                    <FormattedMessage id="SistBehandledeSaker.Behandling" values={{ index: index + 1 }} />
                  </Lenke>
                )
              }
            </Normaltekst>
            <VerticalSpacer eightPx />
          </Fragment>
        ))}
      </>
    );
  }
}

const mapStateToProps = state => ({
  fpsakUrl: getFpsakUrl(state),
  fptilbakeUrl: getFptilbakeUrl(state),
  sistBehandledeSaker: getBehandledeOppgaver(state) || [],
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  ...bindActionCreators({
    hentFpsakBehandlingId: hentFpsakBehandlingIdActionCreator,
  }, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(SistBehandledeSaker);
