import React, { FunctionComponent, useMemo } from 'react';
import moment from 'moment';
import { FormattedMessage } from 'react-intl';
import { Undertittel, Element } from 'nav-frontend-typografi';

import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { ISO_DATE_FORMAT } from 'utils/formats';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import useKodeverk from 'data/useKodeverk';
import NyeOgFerdigstilteOppgaverForIdagGraf from './NyeOgFerdigstilteOppgaverForIdagGraf';
import NyeOgFerdigstilteOppgaver from '../../nyeOgFerdigstilteOppgaverTsType';

export const getNyeOgFerdigstilteForIDag = (nyeOgFerdigstilte: NyeOgFerdigstilteOppgaver[] = []): NyeOgFerdigstilteOppgaver[] => {
  const iDag = moment();
  return nyeOgFerdigstilte.filter((oppgave) => iDag.isSame(moment(oppgave.dato, ISO_DATE_FORMAT), 'day'));
};

interface OwnProps {
  width: number;
  height: number;
  nyeOgFerdigstilteOppgaver?: NyeOgFerdigstilteOppgaver[];
}

/**
 * NyeOgFerdigstilteOppgaverForIdagPanel.
 */
const NyeOgFerdigstilteOppgaverForIdagPanel: FunctionComponent<OwnProps> = ({
  width,
  height,
  nyeOgFerdigstilteOppgaver,
}) => {
  const behandlingTyper = useKodeverk(kodeverkTyper.BEHANDLING_TYPE);

  const filtrerteNyeOgFerdigstilteOppgaver = useMemo(() => getNyeOgFerdigstilteForIDag(nyeOgFerdigstilteOppgaver), [nyeOgFerdigstilteOppgaver]);

  return (
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
        nyeOgFerdigstilteOppgaver={filtrerteNyeOgFerdigstilteOppgaver}
        behandlingTyper={behandlingTyper}
      />
    </>
  );
};

export default NyeOgFerdigstilteOppgaverForIdagPanel;
