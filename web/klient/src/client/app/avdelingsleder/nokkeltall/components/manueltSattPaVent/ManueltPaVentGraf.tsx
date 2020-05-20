import React, {
  useState, useMemo, FunctionComponent, useCallback,
} from 'react';
import {
  XYPlot, XAxis, YAxis, AreaSeries, Crosshair, HorizontalGridLines,
} from 'react-vis';
import moment from 'moment';
import { FormattedMessage } from 'react-intl';
import Panel from 'nav-frontend-paneler';
import { Normaltekst, Undertekst } from 'nav-frontend-typografi';

import { DDMMYYYY_DATE_FORMAT } from 'utils/formats';

import OppgaverManueltPaVent from './oppgaverManueltPaVentTsType';

import styles from './manueltPaVentGraf.less';

const cssText = {
  fontFamily: 'Source Sans Pro, Arial, sans-serif',
  fontSize: '1rem',
  lineHeight: '1.375rem',
  fontWeight: 400,
};

const lagKoordinater = (oppgaverManueltPaVent) => oppgaverManueltPaVent.map((o) => ({
  x: moment(o.behandlingFrist).startOf('day').toDate(),
  y: o.antall,
}));

const lagDatastruktur = (koordinater: Koordinat[], isFireUkerValgt) => {
  const nyeKoordinater = [];
  const periodeStart = moment().startOf('day').toDate();
  const periodeSlutt = moment().add(isFireUkerValgt ? 4 : 8, 'w').toDate();

  for (let dato = moment(periodeStart); dato.isSameOrBefore(periodeSlutt); dato = dato.add(1, 'days')) {
    const funnetKoordinat = koordinater.find((k) => moment(k.x).isSame(dato));
    nyeKoordinater.push({
      x: dato.toDate(),
      y: funnetKoordinat ? funnetKoordinat.y : 0,
    });
  }

  return nyeKoordinater;
};

const harDatastrukturKun0Verdier = (koordinater) => !koordinater.some((k) => k.y !== 0);

interface Koordinat {
  x: Date;
  y: number;
}

interface OwnProps {
  width: number;
  height: number;
  isFireUkerValgt: boolean;
  oppgaverManueltPaVent?: OppgaverManueltPaVent[];
}

/**
 * ManueltPaVentGraf.
 */
const ManueltPaVentGraf: FunctionComponent<OwnProps> = ({
  width,
  height,
  isFireUkerValgt,
  oppgaverManueltPaVent,
}) => {
  const [crosshairValues, setCrosshairValues] = useState<Koordinat[]>([]);

  const koordinater = useMemo(() => lagKoordinater(oppgaverManueltPaVent), [oppgaverManueltPaVent]);
  const data = useMemo(() => lagDatastruktur(koordinater, isFireUkerValgt), [koordinater, isFireUkerValgt]);
  const isEmpty = useMemo(() => harDatastrukturKun0Verdier(koordinater), [koordinater]);

  const leggTilHintVerdi = useCallback((value: {x: Date; y: number}) => {
    setCrosshairValues([value]);
  }, []);
  const fjernHintVerdi = useCallback(() => {
    setCrosshairValues([]);
  }, []);

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
        stackBy="y"
        onMouseLeave={fjernHintVerdi}
        {...(isEmpty ? { yDomain: [0, 50] } : {})}
      >
        <HorizontalGridLines />
        <XAxis
          tickTotal={6}
          tickFormat={(x) => moment(x).format(DDMMYYYY_DATE_FORMAT)}
          style={{ text: cssText }}
        />
        <YAxis style={{ text: cssText }} />
        <AreaSeries
          data={data}
          onNearestX={leggTilHintVerdi}
          fill="#337c9b"
          stroke="#337c9b"
        />
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
            <Normaltekst>{`${moment(crosshairValues[0].x).format(DDMMYYYY_DATE_FORMAT)}`}</Normaltekst>
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

export default ManueltPaVentGraf;
