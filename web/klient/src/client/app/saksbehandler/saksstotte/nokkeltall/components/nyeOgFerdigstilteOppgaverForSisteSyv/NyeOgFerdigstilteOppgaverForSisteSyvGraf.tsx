import React, {
  FunctionComponent, useState, useMemo, useCallback,
} from 'react';
import dayjs from 'dayjs';
import {
  XYPlot, XAxis, YAxis, HorizontalGridLines, AreaSeries, DiscreteColorLegend, Crosshair,
} from 'react-vis';
import { FormattedMessage, injectIntl, WrappedComponentProps } from 'react-intl';
import Panel from 'nav-frontend-paneler';
import { Normaltekst, Undertekst } from 'nav-frontend-typografi';

import { DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import NyeOgFerdigstilteOppgaver from 'types/saksbehandler/nyeOgFerdigstilteOppgaverTsType';

import 'react-vis/dist/style.css';
import styles from './nyeOgFerdigstilteOppgaverForSisteSyvGraf.less';

export const slaSammenBehandlingstyperOgFyllInnTomme = (nyeOgFerdigstilteOppgaver: NyeOgFerdigstilteOppgaver[]):
{ antallNye: number; antallFerdigstilte: number; dato: Date}[] => {
  const oppgaver = [];
  if (nyeOgFerdigstilteOppgaver.length > 0) {
    const iDag = dayjs().startOf('day');
    const atteDagerSiden = dayjs().subtract(7, 'days').startOf('day');

    for (let dato = atteDagerSiden; dato.isBefore(iDag); dato = dato.add(1, 'days')) {
      const dataForDato = nyeOgFerdigstilteOppgaver.filter((o) => dayjs(o.dato).startOf('day').isSame(dato));
      if (dataForDato.length === 0) {
        oppgaver.push({
          antallNye: 0,
          antallFerdigstilte: 0,
          dato: dato.toDate(),
        });
      } else {
        oppgaver.push({
          antallNye: dataForDato.reduce((acc, d) => acc + d.antallNye, 0),
          antallFerdigstilte: dataForDato.reduce((acc, d) => acc + d.antallFerdigstilte, 0),
          dato: dato.toDate(),
        });
      }
    }
  }

  return oppgaver;
};

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
  nyeOgFerdigstilteOppgaver: NyeOgFerdigstilteOppgaver[];
}

/**
 * NyeOgFerdigstilteOppgaverForSisteSyvGraf
 */
export const NyeOgFerdigstilteOppgaverForSisteSyvGraf: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  width,
  height,
  nyeOgFerdigstilteOppgaver,
}) => {
  const [crosshairValues, setCrosshairValues] = useState<Koordinat[]>([]);

  const onMouseLeave = useCallback(() => setCrosshairValues([]), []);
  const onNearestX = useCallback((value: {x: number; y: number}) => {
    setCrosshairValues([value]);
  }, []);

  const isEmpty = nyeOgFerdigstilteOppgaver.length === 0;

  const sammenslatteOppgaver = useMemo(() => slaSammenBehandlingstyperOgFyllInnTomme(nyeOgFerdigstilteOppgaver), [nyeOgFerdigstilteOppgaver]);
  const ferdigstilteOppgaver = useMemo(() => sammenslatteOppgaver.map((o) => ({
    x: o.dato.getTime(),
    y: o.antallFerdigstilte,
  })), [sammenslatteOppgaver]);
  const nyeOppgaver = useMemo(() => sammenslatteOppgaver.map((o) => ({
    x: o.dato.getTime(),
    y: o.antallNye,
  })), [sammenslatteOppgaver]);

  const getAntall = (oppgaver: Koordinat[]): number | string => {
    const oppgave = oppgaver.find((o) => o.x === crosshairValues[0].x);
    return oppgave ? oppgave.y : '';
  };

  const plotPropsWhenEmpty = useMemo(() => (isEmpty ? {
    yDomain: [0, 50],
    xDomain: [dayjs().subtract(7, 'd').startOf('day').toDate(), dayjs().subtract(1, 'd').startOf('day').toDate()],
  } : {}), [isEmpty]);

  return (
    <Panel>
      {/* @ts-ignore Feil i @types/react-vis yDomain/xDomain har en funksjon */}
      <XYPlot
        dontCheckIfEmpty={isEmpty}
        margin={{
          left: 40, right: 60, top: 10, bottom: 30,
        }}
        width={width}
        height={height}
        xType="time"
        onMouseLeave={onMouseLeave}
        {...plotPropsWhenEmpty}
      >
        <HorizontalGridLines />
        <XAxis
          tickTotal={3}
          tickFormat={(t) => dayjs(t).format(DDMMYYYY_DATE_FORMAT)}
          style={{ text: cssText }}
        />
        <YAxis style={{ text: cssText }} />
        <AreaSeries
          data={ferdigstilteOppgaver}
          fill="#38a161"
          stroke="#38a161"
          opacity={0.5}
          onNearestX={onNearestX}
        />
        <AreaSeries
          data={nyeOppgaver}
          fill="#337c9b"
          stroke="#337c9b"
          opacity={0.5}
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
            <Normaltekst>{`${dayjs(crosshairValues[0].x).format(DDMMYYYY_DATE_FORMAT)}`}</Normaltekst>
            <Undertekst>
              <FormattedMessage
                id="NyeOgFerdigstilteOppgaverForSisteSyvGraf.FerdigstiltAntall"
                values={{ antall: getAntall(ferdigstilteOppgaver) }}
              />
            </Undertekst>
            <Undertekst>
              <FormattedMessage id="NyeOgFerdigstilteOppgaverForSisteSyvGraf.NyeAntall" values={{ antall: getAntall(nyeOppgaver) }} />
            </Undertekst>
          </div>
        </Crosshair>
        )}
      </XYPlot>
      <div className={styles.center}>
        <DiscreteColorLegend
          orientation="horizontal"
          // @ts-ignore Feil i @types/react-vis
          colors={['#38a161', '#337c9b']}
          items={[
            <Normaltekst className={styles.displayInline}>
              <FormattedMessage id="NyeOgFerdigstilteOppgaverForSisteSyvGraf.Ferdigstilte" />
            </Normaltekst>,
            <Normaltekst className={styles.displayInline}>
              <FormattedMessage id="NyeOgFerdigstilteOppgaverForSisteSyvGraf.Nye" />
            </Normaltekst>,
          ]}
        />
      </div>
    </Panel>
  );
};

export default injectIntl(NyeOgFerdigstilteOppgaverForSisteSyvGraf);
