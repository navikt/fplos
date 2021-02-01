import React, {
  useMemo, useState, FunctionComponent, useCallback,
} from 'react';
import {
  XYPlot, XAxis, YAxis, VerticalGridLines, HorizontalRectSeries, Hint, DiscreteColorLegend,
} from 'react-vis';
import {
  FormattedMessage, injectIntl, IntlShape, WrappedComponentProps,
} from 'react-intl';
import { Normaltekst } from 'nav-frontend-typografi';
import Panel from 'nav-frontend-paneler';

import { FlexContainer, FlexRow, FlexColumn } from 'sharedComponents/flexGrid';
import Kodeverk from 'kodeverk/kodeverkTsType';
import behandlingType from 'kodeverk/behandlingType';
import OppgaverForAvdeling from './oppgaverForAvdelingTsType';

import 'react-vis/dist/style.css';
import styles from './fordelingAvBehandlingstypeGraf.less';

const LEGEND_WIDTH = 210;

const behandlingstypeOrder = [
  behandlingType.TILBAKEBETALING_REVURDERING,
  behandlingType.TILBAKEBETALING,
  behandlingType.DOKUMENTINNSYN,
  behandlingType.KLAGE,
  behandlingType.REVURDERING,
  behandlingType.FORSTEGANGSSOKNAD];

const settCustomHoydePaSoylene = (data: { x: number; y: number }[]): Koordinat[] => {
  const transformert = data.map((el) => ({
    ...el,
    y0: el.y + 0.30,
    y: el.y - 0.30,
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

const formatData = (oppgaverForAvdeling: OppgaverForAvdeling[]): { x: number; y: number }[] => {
  const sammenslatteBehandlingstyper = oppgaverForAvdeling
    .reduce((acc, o) => {
      const index = behandlingstypeOrder.indexOf(o.behandlingType.kode) + 1;
      return {
        ...acc,
        [index]: (acc[index] ? acc[index] + o.antall : o.antall),
      };
    }, {} as Record<number, number>);

  return Object.keys(sammenslatteBehandlingstyper)
    .map((k) => ({ x: sammenslatteBehandlingstyper[k], y: parseInt(k, 10) }));
};

const cssText = {
  fontFamily: 'Source Sans Pro, Arial, sans-serif',
  fontSize: '1rem',
  lineHeight: '1.375rem',
  fontWeight: 400,
};

const getHintAntall = (verdi: Koordinat, intl: IntlShape): string => intl.formatMessage({ id: 'FordelingAvBehandlingstypeGraf.Antall' }, {
  antall: verdi.x0 ? verdi.x - verdi.x0 : verdi.x,
});

const getHintTotalAntall = (
  verdi: Koordinat,
  tilBeslutter: { x: number; y: number }[],
  tilSaksbehandling: { x: number; y: number }[],
  intl: IntlShape,
): string => {
  const y = Math.ceil(verdi.y);
  const beslutterAntall = tilBeslutter.find((b) => b.y === y);
  const sum1 = beslutterAntall ? beslutterAntall.x : 0;
  const saksbehandlingAntall = tilSaksbehandling.find((b) => b.y === y);
  const sum2 = saksbehandlingAntall ? saksbehandlingAntall.x : 0;
  return intl.formatMessage({ id: 'FordelingAvBehandlingstypeGraf.TotaltAntall' }, { antall: sum1 + sum2 });
};

interface OwnProps {
  intl: any;
  width: number;
  height: number;
  behandlingTyper: Kodeverk[];
  oppgaverForAvdeling: OppgaverForAvdeling[];
}

interface Koordinat {
  x: number;
  x0: number;
  y: number;
  y0: number;
}

/**
 * FordelingAvBehandlingstypeGraf.
 */
const FordelingAvBehandlingstypeGraf: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  width,
  height,
  oppgaverForAvdeling,
  behandlingTyper,
}) => {
  const [hintVerdi, setHintVerdi] = useState<Koordinat>();

  const leggTilHintVerdi = useCallback((verdi: Koordinat): void => {
    setHintVerdi(verdi);
  }, []);
  const fjernHintVerdi = useCallback((): void => {
    setHintVerdi(undefined);
  }, []);

  const finnBehandlingTypeNavn = useCallback((_v: number, i: number): string => {
    const type = behandlingTyper.find((bt) => bt.kode === behandlingstypeOrder[i]);
    return type ? type.navn : '';
  }, []);

  const tilSaksbehandling = useMemo(() => formatData(oppgaverForAvdeling.filter((o) => o.tilBehandling)), [oppgaverForAvdeling]);
  const tilBeslutter = useMemo(() => formatData(oppgaverForAvdeling.filter((o) => !o.tilBehandling)), [oppgaverForAvdeling]);
  const isEmpty = tilSaksbehandling.length === 0 && tilBeslutter.length === 0;

  return (
    <Panel className={styles.panel}>
      <FlexContainer>
        <FlexRow>
          <FlexColumn>
            <XYPlot
              dontCheckIfEmpty={isEmpty}
              margin={{
                left: 170, right: 40, top: 40, bottom: 0,
              }}
              width={width - LEGEND_WIDTH > 0 ? width - LEGEND_WIDTH : 100 + LEGEND_WIDTH}
              height={height}
              stackBy="x"
              yDomain={[0, 7]}
              {...(isEmpty ? { xDomain: [0, 100] } : {})}
            >
              <VerticalGridLines />
              <XAxis orientation="top" style={{ text: cssText }} />
              <YAxis
                style={{ text: cssText }}
                // @ts-ignore Feil i @types/react-vis
                tickFormat={finnBehandlingTypeNavn}
                tickValues={[1, 2, 3, 4, 5, 6]}
              />
              <HorizontalRectSeries
                data={settCustomHoydePaSoylene(tilSaksbehandling)}
                onValueMouseOver={leggTilHintVerdi}
                onValueMouseOut={fjernHintVerdi}
                fill="#337c9b"
                stroke="#337c9b"
              />
              <HorizontalRectSeries
                data={settCustomHoydePaSoylene(tilBeslutter)}
                onValueMouseOver={leggTilHintVerdi}
                onValueMouseOut={fjernHintVerdi}
                fill="#38a161"
                stroke="#38a161"
              />
              {hintVerdi && (
                <Hint value={hintVerdi}>
                  <div className={styles.hint}>
                    {getHintAntall(hintVerdi, intl)}
                    <br />
                    {getHintTotalAntall(hintVerdi, tilBeslutter, tilSaksbehandling, intl)}
                  </div>
                </Hint>
              )}
            </XYPlot>
          </FlexColumn>
          <FlexColumn>
            <DiscreteColorLegend
              // @ts-ignore Feil i @types/react-vis
              colors={['#337c9b', '#38a161']}
              items={[
                <Normaltekst className={styles.displayInline}>
                  <FormattedMessage id="FordelingAvBehandlingstypeGraf.TilBehandling" />
                </Normaltekst>,
                <Normaltekst className={styles.displayInline}>
                  <FormattedMessage id="FordelingAvBehandlingstypeGraf.TilBeslutter" />
                </Normaltekst>,
              ]}
            />
          </FlexColumn>
        </FlexRow>
      </FlexContainer>
    </Panel>
  );
};

export default injectIntl(FordelingAvBehandlingstypeGraf);
