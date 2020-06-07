
import React, { FunctionComponent } from 'react';
import moment from 'moment';
import { FormattedMessage } from 'react-intl';
import { Element } from 'nav-frontend-typografi';

import { ISO_DATE_FORMAT } from 'utils/formats';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import NyeOgFerdigstilteOppgaverForSisteSyvGraf from './NyeOgFerdigstilteOppgaverForSisteSyvGraf';
import NyeOgFerdigstilteOppgaver from '../../nyeOgFerdigstilteOppgaverTsType';

export const getNyeOgFerdigstilteForSisteSyvDager = (nyeOgFerdigstilte: NyeOgFerdigstilteOppgaver[] = []) => {
  const iDag = moment().startOf('day');
  return nyeOgFerdigstilte.filter((oppgave) => iDag.isAfter(moment(oppgave.dato, ISO_DATE_FORMAT)));
};

interface OwnProps {
  width: number;
  height: number;
  nyeOgFerdigstilteOppgaver: NyeOgFerdigstilteOppgaver[];
}

/**
 * NyeOgFerdigstilteOppgaverForSisteSyvPanel.
 */
export const NyeOgFerdigstilteOppgaverForSisteSyvPanel: FunctionComponent<OwnProps> = ({
  width,
  height,
  nyeOgFerdigstilteOppgaver,
}) => (
  <>
    <VerticalSpacer eightPx />
    <Element>
      <FormattedMessage id="NyeOgFerdigstilteOppgaverForSisteSyvPanel.SisteSyv" />
    </Element>
    <NyeOgFerdigstilteOppgaverForSisteSyvGraf
      width={width}
      height={height}
      nyeOgFerdigstilteOppgaver={nyeOgFerdigstilteOppgaver}
    />
  </>
);

export default NyeOgFerdigstilteOppgaverForSisteSyvPanel;
