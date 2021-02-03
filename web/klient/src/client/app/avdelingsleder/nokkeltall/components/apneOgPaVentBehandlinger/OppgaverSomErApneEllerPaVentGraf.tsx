import React, {
  useMemo, FunctionComponent, useState, useCallback,
} from 'react';
import {
  XYPlot, XAxis, YAxis, HorizontalGridLines, VerticalRectSeries, DiscreteColorLegend, Hint,
} from 'react-vis';
import {
  FormattedMessage, injectIntl, IntlShape, WrappedComponentProps,
} from 'react-intl';
import moment from 'moment';
import Panel from 'nav-frontend-paneler';
import { Normaltekst } from 'nav-frontend-typografi';

import behandlingVenteStatus from 'kodeverk/behandlingVenteStatus';
import { FlexContainer, FlexRow, FlexColumn } from 'sharedComponents/flexGrid';

import 'react-vis/dist/style.css';
import styles from './oppgaverSomErApneEllerPaVentGraf.less';
import OppgaverSomErApneEllerPaVent from './oppgaverSomErApneEllerPaVentTsType';

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
  const [hintVerdi, setHintVerdi] = useState<Koordinat>();

  const leggTilHintVerdi = useCallback((verdi: Koordinat): void => {
    setHintVerdi(verdi);
  }, []);
  const fjernHintVerdi = useCallback((): void => {
    setHintVerdi(undefined);
  }, []);

  const [periodeStart, periodeSlutt] = useMemo(() => finnGrafPeriode(oppgaverApneEllerPaVent), [oppgaverApneEllerPaVent]);

  const oppgaverPaVentPerDato = useMemo(() => finnAntallPerDato(oppgaverApneEllerPaVent
    .filter((o) => o.behandlingVenteStatus.kode === behandlingVenteStatus.PA_VENT)), [oppgaverApneEllerPaVent]);
  const oppgaverIkkePaVentPerDato = useMemo(() => finnAntallPerDato(oppgaverApneEllerPaVent
    .filter((o) => o.behandlingVenteStatus.kode === behandlingVenteStatus.IKKE_PA_VENT)), [oppgaverApneEllerPaVent]);

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
                onValueMouseOver={leggTilHintVerdi}
                onValueMouseOut={fjernHintVerdi}
                fill="#337c9b"
                stroke="#337c9b"
              />
              <VerticalRectSeries
                data={rectSeriesKoordinaterPaVent}
                // @ts-ignore Feil i @types/react-vis
                onValueMouseOver={leggTilHintVerdi}
                onValueMouseOut={fjernHintVerdi}
                fill="#38a161"
                stroke="#38a161"
              />
              {hintVerdi && (
                <Hint value={hintVerdi}>
                  <div className={styles.hint}>
                    {intl.formatMessage({ id: 'OppgaverSomErApneEllerPaVentGraf.Antall' }, {
                      antall: hintVerdi.y0 ? hintVerdi.y - hintVerdi.y0 : hintVerdi.y,
                    })}
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
                  <FormattedMessage id="OppgaverSomErApneEllerPaVentGraf.IkkePaVent" />
                </Normaltekst>,
                <Normaltekst className={styles.displayInline}>
                  <FormattedMessage id="OppgaverSomErApneEllerPaVentGraf.PaVent" />
                </Normaltekst>,
              ]}
            />
          </FlexColumn>
        </FlexRow>
      </FlexContainer>
    </Panel>
  );
};

export default injectIntl(OppgaverSomErApneEllerPaVentGraf);
