import React, { FunctionComponent, useMemo } from 'react';
import moment from 'moment';
import { FormattedMessage } from 'react-intl';
import { Element } from 'nav-frontend-typografi';

import NyeOgFerdigstilteOppgaver from 'types/saksbehandler/nyeOgFerdigstilteOppgaverTsType';
import { ISO_DATE_FORMAT } from 'utils/formats';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import NyeOgFerdigstilteOppgaverForSisteSyvGraf from './NyeOgFerdigstilteOppgaverForSisteSyvGraf';

export const getNyeOgFerdigstilteForSisteSyvDager = (nyeOgFerdigstilte: NyeOgFerdigstilteOppgaver[] = []): NyeOgFerdigstilteOppgaver[] => {
  const iDag = moment().startOf('day');
  return nyeOgFerdigstilte.filter((oppgave) => iDag.isAfter(moment(oppgave.dato, ISO_DATE_FORMAT)));
};

interface OwnProps {
  width: number;
  height: number;
  nyeOgFerdigstilteOppgaver?: NyeOgFerdigstilteOppgaver[];
}

/**
 * NyeOgFerdigstilteOppgaverForSisteSyvPanel.
 */
const NyeOgFerdigstilteOppgaverForSisteSyvPanel: FunctionComponent<OwnProps> = ({
  width,
  height,
  nyeOgFerdigstilteOppgaver,
}) => {
  const filtrertenyeOgFerdigstilteOppgaver = useMemo(() => getNyeOgFerdigstilteForSisteSyvDager(nyeOgFerdigstilteOppgaver), [nyeOgFerdigstilteOppgaver]);
  return (
    <>
      <VerticalSpacer eightPx />
      <Element>
        <FormattedMessage id="NyeOgFerdigstilteOppgaverForSisteSyvPanel.SisteSyv" />
      </Element>
      <NyeOgFerdigstilteOppgaverForSisteSyvGraf
        width={width}
        height={height}
        nyeOgFerdigstilteOppgaver={filtrertenyeOgFerdigstilteOppgaver}
      />
    </>
  );
};

export default NyeOgFerdigstilteOppgaverForSisteSyvPanel;
