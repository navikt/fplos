
import React, { FunctionComponent } from 'react';
import moment from 'moment';
import { connect } from 'react-redux';
import { createSelector } from 'reselect';
import { FormattedMessage } from 'react-intl';
import { Undertittel, Element } from 'nav-frontend-typografi';

import Kodeverk from 'kodeverk/kodeverkTsType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { ISO_DATE_FORMAT } from 'utils/formats';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { getAlleKodeverk } from 'kodeverk/duck';
import NyeOgFerdigstilteOppgaverForIdagGraf from './NyeOgFerdigstilteOppgaverForIdagGraf';
import { getNyeOgFerdigstilteOppgaverNokkeltall } from '../../duck';
import NyeOgFerdigstilteOppgaver from '../nyeOgFerdigstilteOppgaverTsType';

interface OwnProps {
  width: number;
  height: number;
  nyeOgFerdigstilteOppgaver: NyeOgFerdigstilteOppgaver[];
  behandlingTyper: Kodeverk[];
}

/**
 * NyeOgFerdigstilteOppgaverForIdagPanel.
 */
export const NyeOgFerdigstilteOppgaverForIdagPanel: FunctionComponent<OwnProps> = ({
  width,
  height,
  nyeOgFerdigstilteOppgaver,
  behandlingTyper,
}) => (
  <>
    <Undertittel>
      <FormattedMessage id="NyeOgFerdigstilteOppgaverForIdagPanel.NyeOgFerdigstilte" />
    </Undertittel>
    <VerticalSpacer eightPx />
    <Element>
      <FormattedMessage id="NyeOgFerdigstilteOppgaverForIdagPanel.IDag" />
    </Element>
    <NyeOgFerdigstilteOppgaverForIdagGraf
      width={width}
      height={height}
      nyeOgFerdigstilteOppgaver={nyeOgFerdigstilteOppgaver}
      behandlingTyper={behandlingTyper}
    />
  </>
);

export const getNyeOgFerdigstilteForIDag = createSelector([getNyeOgFerdigstilteOppgaverNokkeltall], (nyeOgFerdigstilte: { dato: string }[] = []) => {
  const iDag = moment();
  return nyeOgFerdigstilte.filter((oppgave) => iDag.isSame(moment(oppgave.dato, ISO_DATE_FORMAT), 'day'));
});

const mapStateToProps = (state) => ({
  nyeOgFerdigstilteOppgaver: getNyeOgFerdigstilteForIDag(state),
  behandlingTyper: getAlleKodeverk(state)[kodeverkTyper.BEHANDLING_TYPE],
});

export default connect(mapStateToProps)(NyeOgFerdigstilteOppgaverForIdagPanel);
