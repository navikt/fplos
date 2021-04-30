import React, {
  useMemo, FunctionComponent, useState, useCallback,
} from 'react';
import {
  XYPlot, XAxis, YAxis, HorizontalGridLines, VerticalRectSeries, DiscreteColorLegend, Hint,
} from 'react-vis';
import {
  injectIntl, IntlShape, WrappedComponentProps,
} from 'react-intl';
import moment from 'moment';
import Panel from 'nav-frontend-paneler';

import BehandlingVenteStatus from 'kodeverk/behandlingVenteStatus';
import { FlexContainer, FlexRow, FlexColumn } from 'sharedComponents/flexGrid';
import OppgaverSomErApneEllerPaVent from 'types/avdelingsleder/oppgaverSomErApneEllerPaVentTsType';

import 'react-vis/dist/style.css';
import styles from './oppgaverSomErApneEllerPaVentGraf.less';

const LEGEND_WIDTH = 210;

const UKJENT_DATO = 'UKJENT_DATO';

interface KoordinatDatoEllerUkjent {
  x: string;
  y: number;
}

interface KoordinatDato {
  x: moment.Moment;
  y: number;
}

interface Koordinat {
  x: number;
  x0: number;
  y: number;
  y0: number;
}

const getYearText = (month: number, intl: IntlShape): string => intl.formatMessage({ id: `OppgaverSomErApneEllerPaVentGraf.${month}` });

const finnGrafPeriode = (oppgaverSomErApneEllerPaVent: OppgaverSomErApneEllerPaVent[]): moment.Moment[] => {
  let periodeStart = moment().subtract(9, 'M');
  let periodeSlutt = moment().add(1, 'M');

  oppgaverSomErApneEllerPaVent
    .filter((oppgave) => !!oppgave.førsteUttakMåned)
    .forEach((oppgave) => {
      const dato = moment(oppgave.førsteUttakMåned);
      if (dato.isBefore(periodeStart)) {
        periodeStart = dato;
      }
      if (dato.isAfter(periodeSlutt)) {
        periodeSlutt = dato;
      }
    });

  // Eksta kolonne mellom y-akse og første stolpe + Ekstra kolonne for data med ukjent dato
  return [moment(periodeStart.subtract(1, 'months').startOf('month')), moment(periodeSlutt.add(1, 'months').startOf('month'))];
};

const finnAntallPerDato = (oppgaverSomErApneEllerPaVent: OppgaverSomErApneEllerPaVent[]): KoordinatDatoEllerUkjent[] => {
  const antallPerDatoOgUkjent = oppgaverSomErApneEllerPaVent
    .reduce((acc, oppgave) => {
      const { førsteUttakMåned, antall } = oppgave;
      const key = førsteUttakMåned || UKJENT_DATO;
      return {
        ...acc,
        [key]: (acc[key] ? acc[key] + antall : antall),
      };
    }, {} as Record<string, number>);

  return Object.keys(antallPerDatoOgUkjent)
    .map((k) => ({ x: k, y: antallPerDatoOgUkjent[k] }));
};

const lagKoordinatForDato = (dato: moment.Moment, oppgaver: KoordinatDatoEllerUkjent[]): KoordinatDato => {
  const eksisterendeDato = oppgaver.filter((o) => o.x !== UKJENT_DATO).find((o) => moment(o.x).isSame(dato));
  return {
    x: eksisterendeDato ? moment(eksisterendeDato.x) : dato,
    y: eksisterendeDato ? eksisterendeDato.y : 0,
  };
};

const fyllInnManglendeDatoerOgSorterEtterDato = (
  oppgaverPaVent: KoordinatDatoEllerUkjent[],
  oppgaverIkkePaVent: KoordinatDatoEllerUkjent[],
  periodeStart: moment.Moment,
  periodeSlutt: moment.Moment,
): { koordinaterPaVent: KoordinatDato[], koordinaterIkkePaVent: KoordinatDato[] } => {
  const koordinaterPaVent: KoordinatDato[] = [];
  const koordinaterIkkePaVent: KoordinatDato[] = [];

  let dato = moment(periodeStart);
  do {
    koordinaterPaVent.push(lagKoordinatForDato(dato, oppgaverPaVent));
    koordinaterIkkePaVent.push(lagKoordinatForDato(dato, oppgaverIkkePaVent));
    dato = moment(dato.add(1, 'month'));
  } while (dato.isBefore(periodeSlutt));

  koordinaterPaVent.push({
    x: periodeSlutt,
    y: oppgaverPaVent.find((d) => d.x === UKJENT_DATO)?.y || 0,
  });
  koordinaterIkkePaVent.push({
    x: periodeSlutt,
    y: oppgaverIkkePaVent.find((d) => d.x === UKJENT_DATO)?.y || 0,
  });

  return {
    koordinaterPaVent,
    koordinaterIkkePaVent,
  };
};

const settCustomBreddePaSoylene = (data: KoordinatDato[]): Koordinat[] => data.map((el, index) => ({
  ...el,
  x0: index + 1 - 0.30,
  x: index + 1 + 0.30,
  y: el.y,
  y0: 0,
}));

const cssText = {
  fontFamily: 'Source Sans Pro, Arial, sans-serif',
  fontSize: '1rem',
  lineHeight: '1.375rem',
  fontWeight: 400,
};

interface OwnProps {
  intl: any;
  width: number;
  height: number;
  oppgaverApneEllerPaVent: OppgaverSomErApneEllerPaVent[];
}

/**
 * OppgaverSomErApneEllerPaVentGraf.
 */
const OppgaverSomErApneEllerPaVentGraf: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  width,
  height,
  oppgaverApneEllerPaVent,
}) => {
  const [hintVerdi, setHintVerdi] = useState<{ paVent: boolean, verdi: Koordinat }>();

  const leggTilHintVerdiPaVent = useCallback((verdi: Koordinat): void => {
    setHintVerdi({ paVent: true, verdi });
  }, []);
  const leggTilHintVerdiIkkePaVent = useCallback((verdi: Koordinat): void => {
    setHintVerdi({ paVent: false, verdi });
  }, []);
  const fjernHintVerdi = useCallback((): void => {
    setHintVerdi(undefined);
  }, []);

  const [periodeStart, periodeSlutt] = useMemo(() => finnGrafPeriode(oppgaverApneEllerPaVent), [oppgaverApneEllerPaVent]);

  const oppgaverPaVentPerDato = useMemo(() => finnAntallPerDato(oppgaverApneEllerPaVent
    .filter((o) => o.behandlingVenteStatus.kode === BehandlingVenteStatus.PA_VENT)), [oppgaverApneEllerPaVent]);
  const oppgaverIkkePaVentPerDato = useMemo(() => finnAntallPerDato(oppgaverApneEllerPaVent
    .filter((o) => o.behandlingVenteStatus.kode === BehandlingVenteStatus.IKKE_PA_VENT)), [oppgaverApneEllerPaVent]);

  const isEmpty = oppgaverPaVentPerDato.length === 0 && oppgaverIkkePaVentPerDato.length === 0;

  const { koordinaterPaVent, koordinaterIkkePaVent } = useMemo(() => fyllInnManglendeDatoerOgSorterEtterDato(
    oppgaverPaVentPerDato, oppgaverIkkePaVentPerDato, periodeStart, periodeSlutt,
  ), [oppgaverPaVentPerDato, oppgaverIkkePaVentPerDato, periodeStart, periodeSlutt]);

  const rectSeriesKoordinaterPaVent = useMemo(() => settCustomBreddePaSoylene(koordinaterPaVent), [koordinaterPaVent]);
  const rectSeriesKoordinaterIkkePaVent = useMemo(() => settCustomBreddePaSoylene(koordinaterIkkePaVent), [koordinaterIkkePaVent]);

  const plotPropsWhenEmpty = isEmpty ? {
    yDomain: [0, 10],
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
              margin={{
                left: 50, right: 10, top: 30, bottom: 50,
              }}
              width={width - LEGEND_WIDTH > 0 ? width - LEGEND_WIDTH : 100 + LEGEND_WIDTH}
              height={height}
              stackBy="y"
              {...plotPropsWhenEmpty}
            >
              <HorizontalGridLines />
              <XAxis
                style={{ text: cssText }}
                // @ts-ignore Feil i @types/react-vis Kan returnere Element
                tickFormat={(index) => {
                  // TODO (TOR) Kvifor får ein av og til flyttall her?
                  const erFlyttall = index % 1 !== 0;
                  if (isEmpty || erFlyttall) {
                    return '';
                  }

                  const koordinat = koordinaterPaVent.length > 0 ? koordinaterPaVent : koordinaterIkkePaVent;
                  const erSiste = index === koordinat.length;
                  const xVerdi = koordinat[index - 1].x;
                  const dato = erSiste ? intl.formatMessage({ id: 'OppgaverSomErApneEllerPaVentGraf.Ukjent' }) : getYearText(xVerdi.month(), intl);
                  const ar = erSiste ? intl.formatMessage({ id: 'OppgaverSomErApneEllerPaVentGraf.Dato' }) : xVerdi.year();

                  return (
                    <tspan>
                      <tspan x="0" dy="1em">{dato}</tspan>
                      <tspan x="0" dy="1em">{ar}</tspan>
                    </tspan>
                  );
                }}
              />
              <YAxis
                style={{ text: cssText }}
                title={intl.formatMessage({ id: 'OppgaverSomErApneEllerPaVentGraf.AntallGraf' })}
              />
              <VerticalRectSeries
                data={rectSeriesKoordinaterIkkePaVent}
                // @ts-ignore Feil i @types/react-vis
                onValueMouseOver={leggTilHintVerdiIkkePaVent}
                onValueMouseOut={fjernHintVerdi}
                fill="#38a161"
                stroke="#38a161"
              />
              <VerticalRectSeries
                data={rectSeriesKoordinaterPaVent}
                // @ts-ignore Feil i @types/react-vis
                onValueMouseOver={leggTilHintVerdiPaVent}
                onValueMouseOut={fjernHintVerdi}
                fill="#85d5f0"
                stroke="#85d5f0"
              />
              {hintVerdi && (
                <Hint value={hintVerdi.verdi}>
                  <div className={styles.hint}>
                    {intl.formatMessage({
                      id: hintVerdi.paVent
                        ? 'OppgaverSomErApneEllerPaVentGraf.AntallPaVent' : 'OppgaverSomErApneEllerPaVentGraf.AntallIkkePaVent',
                    }, {
                      antall: hintVerdi.verdi.y0 ? hintVerdi.verdi.y - hintVerdi.verdi.y0 : hintVerdi.verdi.y,
                    })}
                  </div>
                </Hint>
              )}
            </XYPlot>
          </FlexColumn>
          <FlexColumn>
            <DiscreteColorLegend
              items={[
                // @ts-ignore Feil i @types/react-vis
                { title: intl.formatMessage({ id: 'OppgaverSomErApneEllerPaVentGraf.PaVent' }), color: '#85d5f0', strokeWidth: 12 },
                // @ts-ignore Feil i @types/react-vis
                { title: intl.formatMessage({ id: 'OppgaverSomErApneEllerPaVentGraf.IkkePaVent' }), color: '#38a161', strokeWidth: 12 },
              ]}
            />
          </FlexColumn>
        </FlexRow>
      </FlexContainer>
    </Panel>
  );
};

export default injectIntl(OppgaverSomErApneEllerPaVentGraf);
