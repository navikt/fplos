import React, { FunctionComponent, useState, useMemo } from 'react';
import {
  XYPlot, XAxis, YAxis, AreaSeries, Crosshair, HorizontalGridLines,
} from 'react-vis';
import dayjs from 'dayjs';
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore';
import isSameOrAfter from 'dayjs/plugin/isSameOrAfter';
import { FormattedMessage } from 'react-intl';
import Panel from 'nav-frontend-paneler';
import { Normaltekst, Undertekst } from 'nav-frontend-typografi';

import { DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import OppgaverForForsteStonadsdag from 'types/avdelingsleder/oppgaverForForsteStonadsdagTsType';

import styles from './oppgaverPerForsteStonadsdagGraf.less';

dayjs.extend(isSameOrBefore);
dayjs.extend(isSameOrAfter);

export const lagKoordinater = (oppgaverPerForsteStonadsdag: OppgaverForForsteStonadsdag[]): Koordinat[] => oppgaverPerForsteStonadsdag
  .map((o) => ({
    x: dayjs(o.forsteStonadsdag).startOf('day').toDate().getTime(),
    y: o.antall,
  }));

export const lagDatastruktur = (koordinater: Koordinat[]): Koordinat[] => {
  const nyeKoordinater = [];
  const periodeStart = koordinater
    .map((koordinat) => dayjs(koordinat.x))
    .reduce((tidligesteDato, dato) => (tidligesteDato.isSameOrBefore(dato) ? tidligesteDato : dato), dayjs().startOf('day'))
    .toDate();
  const periodeSlutt = koordinater
    .map((koordinat) => dayjs(koordinat.x))
    .reduce((senesteDato, dato) => (senesteDato.isSameOrAfter(dato) ? senesteDato : dato), dayjs().startOf('day'))
    .toDate();

  for (let dato = dayjs(periodeStart); dato.isSameOrBefore(periodeSlutt); dato = dato.add(1, 'days')) {
    const funnetKoordinat = koordinater.find((k) => dayjs(k.x).isSame(dato));
    nyeKoordinater.push({
      x: dato.toDate().getTime(),
      y: funnetKoordinat ? funnetKoordinat.y : 0,
    });
  }
  return nyeKoordinater;
};

export const harDatastrukturKun0Verdier = (koordinater: Koordinat[]): boolean => !koordinater.some((k) => k.y !== 0);

const cssText = {
  fontFamily: 'Source Sans Pro, Arial, sans-serif',
  fontSize: '1rem',
  lineHeight: '1.375rem',
  fontWeight: 400,
};

interface Koordinat {
  x: number;
  y: number;
}

interface OwnProps {
  width: number;
  height: number;
  oppgaverPerForsteStonadsdag: OppgaverForForsteStonadsdag[];
}

/**
 * OppgaverPerForsteStonadsdagGraf.
 */
const OppgaverPerForsteStonadsdagGraf: FunctionComponent<OwnProps> = ({
  width,
  height,
  oppgaverPerForsteStonadsdag,
}) => {
  const [crosshairValues, setCrosshairValues] = useState<Koordinat[]>([]);

  const koordinater = useMemo(() => lagKoordinater(oppgaverPerForsteStonadsdag), [oppgaverPerForsteStonadsdag]);
  const data = useMemo(() => lagDatastruktur(koordinater), [koordinater]);
  const isEmpty = useMemo(() => harDatastrukturKun0Verdier(koordinater), [koordinater]);

  return (
    <Panel className={styles.panel}>
      <XYPlot
        dontCheckIfEmpty={isEmpty}
        margin={{
          left: 40, right: 70, top: 10, bottom: 30,
        }}
        width={width}
        height={height}
        xType="time"
        onMouseLeave={() => setCrosshairValues([])}
        {...(isEmpty ? { yDomain: [0, 50], xDomain: [dayjs().subtract(5, 'd'), dayjs().add(5, 'd')] } : {})}
      >
        <HorizontalGridLines />
        <XAxis
          tickTotal={5}
          tickFormat={(x) => dayjs(x).format(DDMMYYYY_DATE_FORMAT)}
          style={{ text: cssText }}
        />
        <YAxis style={{ text: cssText }} />
        <AreaSeries
          data={data}
          onNearestX={(value: {x: number; y: number}) => setCrosshairValues([value])}
          fill="#337c9b"
          stroke="#337c9b"
        />
        <Crosshair
          values={[{ x: dayjs().toDate(), y: 0 }]}
          style={{
            line: {
              background: '#c30000',
            },
          }}
        >
          <div className={styles.crosshairDagensDato}>
            <FormattedMessage id="OppgaverPerForsteStonadsdagGraf.DagensDato" values={{ br: <br /> }} />
          </div>
        </Crosshair>

        {!isEmpty && crosshairValues.length > 0 && (
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
              <Undertekst>
                <FormattedMessage id="ManueltPaVentGraf.Antall" values={{ antall: crosshairValues[0].y }} />
              </Undertekst>
            </div>
          </Crosshair>
        )}
      </XYPlot>
    </Panel>
  );
};

export default OppgaverPerForsteStonadsdagGraf;
