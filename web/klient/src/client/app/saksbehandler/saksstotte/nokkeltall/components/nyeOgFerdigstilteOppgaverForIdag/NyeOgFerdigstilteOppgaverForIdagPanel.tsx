import React, { FunctionComponent, useMemo } from 'react';
import dayjs from 'dayjs';
import { FormattedMessage } from 'react-intl';
import { Undertittel, Element } from 'nav-frontend-typografi';

import KodeverkType from 'kodeverk/kodeverkTyper';
import { ISO_DATE_FORMAT } from 'utils/formats';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import useKodeverk from 'data/useKodeverk';
import NyeOgFerdigstilteOppgaver from 'types/saksbehandler/nyeOgFerdigstilteOppgaverTsType';
import NyeOgFerdigstilteOppgaverForIdagGraf from './NyeOgFerdigstilteOppgaverForIdagGraf';

export const getNyeOgFerdigstilteForIDag = (nyeOgFerdigstilte: NyeOgFerdigstilteOppgaver[] = []): NyeOgFerdigstilteOppgaver[] => {
  const iDag = dayjs();
  return nyeOgFerdigstilte.filter((oppgave) => iDag.isSame(dayjs(oppgave.dato, ISO_DATE_FORMAT), 'day'));
};

interface OwnProps {
  height: number;
  nyeOgFerdigstilteOppgaver?: NyeOgFerdigstilteOppgaver[];
}

/**
 * NyeOgFerdigstilteOppgaverForIdagPanel.
 */
const NyeOgFerdigstilteOppgaverForIdagPanel: FunctionComponent<OwnProps> = ({
  height,
  nyeOgFerdigstilteOppgaver,
}) => {
  const behandlingTyper = useKodeverk(KodeverkType.BEHANDLING_TYPE);

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
        height={height}
        nyeOgFerdigstilteOppgaver={filtrerteNyeOgFerdigstilteOppgaver}
        behandlingTyper={behandlingTyper}
      />
    </>
  );
};

export default NyeOgFerdigstilteOppgaverForIdagPanel;
