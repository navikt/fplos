
import React, { Component, Fragment } from 'react';
import { FormattedMessage } from 'react-intl';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';
import Lenke from 'nav-frontend-lenker';
import { Undertittel, Normaltekst } from 'nav-frontend-typografi';

import { getFpsakHref, getFptilbakeHref } from 'app/paths';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { getFpsakUrl, getFptilbakeUrl, hentFpsakInternBehandlingId as hentFpsakInternBehandlingIdActionCreator } from 'app/duck';
import { getBehandledeOppgaver } from 'saksbehandler/saksstotte/duck';
import Oppgave from '../../oppgaveTsType';

const getClickEvent = (openFpsak, oppgave) => () => openFpsak(oppgave);

interface OwnProps {
  fpsakUrl: string;
  fptilbakeUrl: string;
  sistBehandledeSaker: Oppgave[];
}

interface DispatchProps {
  hentFpsakInternBehandlingId: (uuid: string) => Promise<{ payload: number }>;
}

/**
 * SistBehandledeSaker
 *
 * Denne komponenten viser de tre siste fagsakene en nav-ansatt har behandlet.
 */
export class SistBehandledeSaker extends Component<OwnProps & DispatchProps> {
  openFpsak = (oppgave: Oppgave) => {
    const { fpsakUrl, fptilbakeUrl, hentFpsakInternBehandlingId } = this.props;

    if (oppgave.system === 'FPSAK') {
      hentFpsakInternBehandlingId(oppgave.behandlingId)
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
        && <Normaltekst><FormattedMessage id="SistBehandledeSaker.IngenBehandlinger" /></Normaltekst>}
        {sistBehandledeSaker.map((sbs, index) => (
          <Fragment key={sbs.behandlingId}>
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
                )}
            </Normaltekst>
            <VerticalSpacer eightPx />
          </Fragment>
        ))}
      </>
    );
  }
}

const mapStateToProps = (state) => ({
  fpsakUrl: getFpsakUrl(state),
  fptilbakeUrl: getFptilbakeUrl(state),
  sistBehandledeSaker: getBehandledeOppgaver(state) || [],
});

const mapDispatchToProps = (dispatch: Dispatch): DispatchProps => ({
  ...bindActionCreators<DispatchProps, any>({
    hentFpsakInternBehandlingId: hentFpsakInternBehandlingIdActionCreator,
  }, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(SistBehandledeSaker);
