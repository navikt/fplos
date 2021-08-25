import React, {
  useMemo, useState, FunctionComponent, useCallback,
} from 'react';
import dayjs from 'dayjs';
import {
  XYPlot, XAxis, YAxis, HorizontalGridLines, AreaSeries, DiscreteColorLegend, Crosshair, MarkSeries,
} from 'react-vis';
import Panel from 'nav-frontend-paneler';
import { Normaltekst, Undertekst } from 'nav-frontend-typografi';

import { FlexContainer, FlexRow, FlexColumn } from 'sharedComponents/flexGrid';
import { DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import BehandlingType from 'kodeverk/behandlingType';
import Kodeverk from 'types/kodeverkTsType';

import 'react-vis/dist/style.css';

import styles from './tilBehandlingGraf.less';

const LEGEND_WIDTH = 260;

const behandlingstypeOrder = [
  BehandlingType.TILBAKEBETALING_REVURDERING,
  BehandlingType.TILBAKEBETALING,
  BehandlingType.DOKUMENTINNSYN,
  BehandlingType.KLAGE,
  BehandlingType.REVURDERING,
  BehandlingType.FORSTEGANGSSOKNAD];

const behandlingstypeFarger = {
  [BehandlingType.TILBAKEBETALING_REVURDERING]: '#ef5d28',
  [BehandlingType.TILBAKEBETALING]: '#ff842f',
  [BehandlingType.DOKUMENTINNSYN]: '#ffd23b',
  [BehandlingType.KLAGE]: '#826ba1',
  [BehandlingType.REVURDERING]: '#3385d1',
  [BehandlingType.FORSTEGANGSSOKNAD]: '#85d5f0',
};

const cssText = {
  fontFamily: 'Source Sans Pro, Arial, sans-serif',
  fontSize: '1rem',
  lineHeight: '1.375rem',
  fontWeight: 400,
};

type Koordinat = {
  x: Date,
  y: number,
}

const sorterBehandlingtyper = (b1: string, b2: string): number => {
  const index1 = behandlingstypeOrder.findIndex((bo) => bo === b1);
  const index2 = behandlingstypeOrder.findIndex((bo) => bo === b2);
  if (index1 === index2) {
    return 0;
  }
  return index1 > index2 ? -1 : 1;
};

const konverterTilKoordinaterGruppertPaBehandlingstype = (oppgaverForAvdeling: OppgaveForDatoGraf[]): Record<string, Koordinat[]> => oppgaverForAvdeling
  .reduce((acc, o) => {
    const nyKoordinat = {
      x: dayjs(o.opprettetDato).startOf('day').toDate(),
      y: o.antall,
    };

    const eksisterendeKoordinater = acc[o.behandlingType.kode];
    return {
      ...acc,
      [o.behandlingType.kode]: (eksisterendeKoordinater ? eksisterendeKoordinater.concat(nyKoordinat) : [nyKoordinat]),
    };
  }, {} as Record<string, Koordinat[]>);

const fyllInnManglendeDatoerOgSorterEtterDato = (
  data: Record<string, Koordinat[]>,
  periodeStart: dayjs.Dayjs,
  periodeSlutt: dayjs.Dayjs,
): Record<string, Koordinat[]> => Object.keys(data).reduce((acc, behandlingstype) => {
  const behandlingstypeData = data[behandlingstype];
  const koordinater = [];

  for (let dato = dayjs(periodeStart); dato.isSameOrBefore(periodeSlutt); dato = dato.add(1, 'days')) {
    const funnetDato = behandlingstypeData.find((d) => dayjs(d.x).startOf('day').isSame(dato.startOf('day')));
    koordinater.push(funnetDato || {
      x: dato.toDate(),
      y: 0,
    });
  }

  return {
    ...acc,
    [behandlingstype]: koordinater,
  };
}, {});

const finnAntallForBehandlingstypeOgDato = (
  data: Record<string, Koordinat[]>,
  behandlingstype: string,
  dato: Date,
): number => {
  const koordinat = data[behandlingstype].find((d) => d.x.getTime() === dato.getTime());
  return koordinat ? koordinat.y : 0;
};

const finnBehandlingTypeNavn = (behandlingTyper: Kodeverk[], behandlingTypeKode: string): string => {
  const type = behandlingTyper.find((bt) => bt.kode === behandlingTypeKode);
  return type ? type.navn : '';
};

export interface OppgaveForDatoGraf {
  behandlingType: Kodeverk;
  opprettetDato: string;
  antall: number;
}

interface OwnProps {
  width: number;
  height: number;
  behandlingTyper: Kodeverk[];
  oppgaverPerDato: OppgaveForDatoGraf[];
  isToUkerValgt: boolean;
}

interface CrosshairValue {
  x: Date;
  y: number;
}

/**
 * TilBehandlingGraf.
 */
const TilBehandlingGraf: FunctionComponent<OwnProps> = ({
  width,
  height,
  oppgaverPerDato,
  isToUkerValgt,
  behandlingTyper,
}) => {
  const [crosshairValues, setCrosshairValues] = useState<CrosshairValue[]>([]);

  const onMouseLeave = useCallback(() => setCrosshairValues([]), []);
  const onNearestX = useCallback((value: {x: Date; y: number}) => {
    setCrosshairValues([value]);
  }, []);

  const periodeStart = dayjs().subtract(isToUkerValgt ? 2 : 4, 'w').add(1, 'd');
  const periodeSlutt = dayjs();

  const koordinater = useMemo(() => konverterTilKoordinaterGruppertPaBehandlingstype(oppgaverPerDato), [oppgaverPerDato]);
  const data = useMemo(() => fyllInnManglendeDatoerOgSorterEtterDato(koordinater, periodeStart, periodeSlutt), [koordinater, periodeStart, periodeSlutt]);

  const sorterteBehandlingstyper = Object.keys(data).sort(sorterBehandlingtyper);
  const reversertSorterteBehandlingstyper = sorterteBehandlingstyper.slice().reverse();

  const isEmpty = sorterteBehandlingstyper.length === 0;
  const plotPropsWhenEmpty = isEmpty ? {
    yDomain: [0, 5],
    xDomain: [periodeStart.toDate(), periodeSlutt.toDate()],
  } : {};

  return (
    <Panel className={styles.panel}>
      <FlexContainer>
        <FlexRow>
          <FlexColumn>
            {/* @ts-ignore Feil i @types/react-vis yDomain og xDomain har en funksjon */}
            <XYPlot
              dontCheckIfEmpty={isEmpty}
              width={width - LEGEND_WIDTH > 0 ? width - LEGEND_WIDTH : 100 + LEGEND_WIDTH}
              height={height}
              margin={{
                left: 40, right: 40, top: 20, bottom: 40,
              }}
              stackBy="y"
              xType="time"
              onMouseLeave={onMouseLeave}
              {...plotPropsWhenEmpty}
            >
              <MarkSeries data={[{ x: dayjs().subtract(1, 'd').toDate(), y: 0 }]} style={{ display: 'none' }} />
              <HorizontalGridLines />
              <XAxis
                tickTotal={5}
                tickFormat={(t) => dayjs(t).format(DDMMYYYY_DATE_FORMAT)}
                style={{ text: cssText }}
              />
              <YAxis style={{ text: cssText }} />
              {sorterteBehandlingstyper.map((k, index) => (
                // @ts-ignore Fiks desse feila
                <AreaSeries
                  key={k}
                  data={data[k]}
                  onNearestX={index === 0 ? onNearestX : () => undefined}
                  // @ts-ignore Fiks
                  fill={behandlingstypeFarger[k]}
                  // @ts-ignore Fiks
                  stroke={behandlingstypeFarger[k]}
                />
              ))}
              {crosshairValues.length > 0 && (
                <Crosshair
                  values={crosshairValues}
                  style={{
                    line: {
                      background: '#3e3832',
                    },
                  }}
                >
                  <div className={styles.crosshair}>
                    <Normaltekst>{`${dayjs(crosshairValues[0].x).format(DDMMYYYY_DATE_FORMAT)}`}</Normaltekst>
                    { reversertSorterteBehandlingstyper.map((key) => (
                      <Undertekst key={key}>
                        {`${finnBehandlingTypeNavn(behandlingTyper, key)}: ${finnAntallForBehandlingstypeOgDato(data, key, crosshairValues[0].x)}`}
                      </Undertekst>
                    ))}
                  </div>
                </Crosshair>
              )}
            </XYPlot>
          </FlexColumn>
          <FlexColumn>
            <DiscreteColorLegend
              items={reversertSorterteBehandlingstyper.map((key) => ({
                title: finnBehandlingTypeNavn(behandlingTyper, key),
                // @ts-ignore Fiks
                color: behandlingstypeFarger[key],
                strokeWidth: 12,
              }))}
            />
          </FlexColumn>
        </FlexRow>
      </FlexContainer>
    </Panel>
  );
};

export default TilBehandlingGraf;
