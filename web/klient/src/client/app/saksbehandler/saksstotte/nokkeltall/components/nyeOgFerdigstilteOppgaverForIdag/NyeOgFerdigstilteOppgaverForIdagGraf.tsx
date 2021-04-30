import React, {
  useState, useMemo, useCallback, FunctionComponent,
} from 'react';
import {
  XYPlot, XAxis, YAxis, VerticalGridLines, HorizontalRectSeries, Hint, DiscreteColorLegend,
} from 'react-vis';
import { FormattedMessage, injectIntl, WrappedComponentProps } from 'react-intl';
import { Normaltekst } from 'nav-frontend-typografi';
import Panel from 'nav-frontend-paneler';

import Kodeverk from 'types/kodeverkTsType';
import BehandlingType from 'kodeverk/behandlingType';
import NyeOgFerdigstilteOppgaver from 'types/saksbehandler/nyeOgFerdigstilteOppgaverTsType';

import 'react-vis/dist/style.css';
import styles from './nyeOgFerdigstilteOppgaverForIdagGraf.less';

const behandlingstypeOrder = [
  BehandlingType.TILBAKEBETALING_REVURDERING,
  BehandlingType.TILBAKEBETALING,
  BehandlingType.DOKUMENTINNSYN,
  BehandlingType.KLAGE,
  BehandlingType.REVURDERING,
  BehandlingType.FORSTEGANGSSOKNAD];

const cssText = {
  fontFamily: 'Source Sans Pro, Arial, sans-serif',
  fontSize: '1rem',
  lineHeight: '1.375rem',
  fontWeight: 400,
};

type Koordinat = {
  x: number;
  y: number;
  y0: number;
  x0: number;
}

const settCustomHoydePaSoylene = (data: { x: number; y: number }[], over: boolean): Koordinat[] => {
  const transformert = data.map((el) => ({
    ...el,
    y0: el.y + (over ? 0.41 : -0.03),
    y: el.y - (over ? -0.03 : -0.35),
    x0: 0,
  }));
  transformert.unshift({
    x: 0, y0: 0, x0: 0, y: 0.5,
  });
  transformert.push({
    x: 0, y0: 0, x0: 0, y: 4.5,
  });
  return transformert;
};

export const lagDatastrukturForFerdigstilte = (
  nyeOgFerdigstilteOppgaver: NyeOgFerdigstilteOppgaver[],
): Koordinat[] => settCustomHoydePaSoylene(
  nyeOgFerdigstilteOppgaver.map((value) => ({
    x: value.antallFerdigstilte,
    y: behandlingstypeOrder.findIndex((bo) => bo === value.behandlingType.kode) + 1,
  })), true,
);

export const lagDatastrukturForNye = (
  nyeOgFerdigstilteOppgaver: NyeOgFerdigstilteOppgaver[],
): Koordinat[] => settCustomHoydePaSoylene(nyeOgFerdigstilteOppgaver
  .map((value) => ({
    x: value.antallNye,
    y: behandlingstypeOrder.findIndex((bo) => bo === value.behandlingType.kode) + 1,
  })), false);

interface OwnProps {
  width: number;
  height: number;
  behandlingTyper: Kodeverk[];
  nyeOgFerdigstilteOppgaver: NyeOgFerdigstilteOppgaver[];
}

/**
 * NyeOgFerdigstilteOppgaverForIdagGraf
 */
export const NyeOgFerdigstilteOppgaverForIdagGraf: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  width,
  height,
  nyeOgFerdigstilteOppgaver,
  behandlingTyper,
}) => {
  const [hintVerdi, setHintVerdi] = useState<Koordinat>();

  const leggTilHintVerdi = useCallback((nyHintVerdi: Koordinat) => {
    setHintVerdi(nyHintVerdi);
  }, []);

  const fjernHintVerdi = useCallback(() => {
    setHintVerdi(undefined);
  }, []);

  const ferdigstilteOppgaver = useMemo(() => lagDatastrukturForFerdigstilte(nyeOgFerdigstilteOppgaver), [nyeOgFerdigstilteOppgaver]);
  const nyeOppgaver = useMemo(() => lagDatastrukturForNye(nyeOgFerdigstilteOppgaver), [nyeOgFerdigstilteOppgaver]);

  const isEmpty = nyeOgFerdigstilteOppgaver.length === 0;

  const hintAntall = useMemo(() => {
    if (!hintVerdi) {
      return undefined;
    }

    const isFerdigstiltVerdi = ferdigstilteOppgaver.find((b) => b.y === hintVerdi.y);
    return isFerdigstiltVerdi
      ? intl.formatMessage({ id: 'NyeOgFerdigstilteOppgaverForIdagGraf.FerdigstiltAntall' }, { antall: hintVerdi.x })
      : intl.formatMessage({ id: 'NyeOgFerdigstilteOppgaverForIdagGraf.NyeAntall' }, { antall: hintVerdi.x });
  }, [hintVerdi]);

  const finnBehandlingTypeNavn = useCallback((_v: number, i: number): string => {
    if (behandlingstypeOrder[i] === BehandlingType.FORSTEGANGSSOKNAD) {
      return intl.formatMessage({ id: 'NyeOgFerdigstilteOppgaverForIdagGraf.Førstegangsbehandling' });
    }

    const type = behandlingTyper.find((bt) => bt.kode === behandlingstypeOrder[i]);
    return type ? type.navn : '';
  }, []);

  const maxXValue = useMemo(() => Math.max(...ferdigstilteOppgaver.map((b) => b.x).concat(nyeOppgaver.map((b) => b.x))) + 2,
    [ferdigstilteOppgaver, nyeOppgaver]);

  return (
    <Panel>
      <XYPlot
        dontCheckIfEmpty={isEmpty}
        margin={{
          left: 127, right: 30, top: 0, bottom: 30,
        }}
        width={width}
        height={height}
        yDomain={[0, 7]}
        xDomain={[0, isEmpty ? 10 : maxXValue]}
      >
        <VerticalGridLines />
        <XAxis style={{ text: cssText }} />
        <YAxis
          style={{ text: cssText }}
          // @ts-ignore Feil i @types/react-vis
          tickFormat={finnBehandlingTypeNavn}
          tickValues={[1, 2, 3, 4, 5, 6]}
        />
        <HorizontalRectSeries
          data={ferdigstilteOppgaver}
          // @ts-ignore Usikker på om feil eller ikkje
          onValueMouseOver={leggTilHintVerdi}
          onValueMouseOut={fjernHintVerdi}
          fill="#38a161"
          stroke="#38a161"
          opacity={0.5}
        />
        <HorizontalRectSeries
          data={nyeOppgaver}
          // @ts-ignore Usikker på om feil eller ikkje
          onValueMouseOver={leggTilHintVerdi}
          onValueMouseOut={fjernHintVerdi}
          fill="#337c9b"
          stroke="#337c9b"
          opacity={0.5}
        />
        {hintVerdi && (
          <Hint value={hintVerdi}>
            <div className={styles.hint}>
              {hintAntall}
            </div>
          </Hint>
        )}
      </XYPlot>
      <div className={styles.center}>
        <DiscreteColorLegend
          orientation="horizontal"
          // @ts-ignore Feil i @types/react-vis
          colors={['#38a161', '#337c9b']}
          items={[
            <Normaltekst className={styles.displayInline}>
              <FormattedMessage id="NyeOgFerdigstilteOppgaverForIdagGraf.Ferdigstilte" />
            </Normaltekst>,
            <Normaltekst className={styles.displayInline}>
              <FormattedMessage id="NyeOgFerdigstilteOppgaverForIdagGraf.Nye" />
            </Normaltekst>,
          ]}
        />
      </div>
    </Panel>
  );
};

export default injectIntl(NyeOgFerdigstilteOppgaverForIdagGraf);
