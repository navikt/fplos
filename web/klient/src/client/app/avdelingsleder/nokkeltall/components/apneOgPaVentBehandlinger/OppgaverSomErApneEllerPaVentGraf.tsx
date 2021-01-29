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

const getYearText = (month: number, intl: IntlShape) => intl.formatMessage({ id: `OppgaverSomErApneEllerPaVentGraf.${month}` });

const settCustomBreddePaSoylene = (data: KoordinatDato[]): Koordinat[] => {
  const transformert = data.map((el, index) => ({
    ...el,
    x0: index + 1 - 0.30,
    x: index + 1 + 0.30,
    y: el.y,
  }));
  return transformert;
};

const grupperAntallPaDato = (oppgaverSomErApneEllerPaVent: OppgaverSomErApneEllerPaVent[]): { x: string; y: number }[] => {
  const sammenslatteBehandlingstyper = oppgaverSomErApneEllerPaVent
    .reduce((acc, oppgave) => {
      const { førsteUttakMåned, antall } = oppgave;
      const key = førsteUttakMåned || 'ukjent';
      return {
        ...acc,
        [key]: (acc[key] ? acc[key] + antall : antall),
      };
    }, {});

  return Object.keys(sammenslatteBehandlingstyper)
    .map((k) => ({ x: k, y: parseInt(sammenslatteBehandlingstyper[k], 10) }));
};

const finnForsteOgSisteDato = (oppgaverSomErApneEllerPaVent: OppgaverSomErApneEllerPaVent[]): moment.Moment[] => {
  let forste;
  let siste;

  oppgaverSomErApneEllerPaVent.filter((oppgave) => !!oppgave.førsteUttakMåned).forEach((oppgave) => {
    const dato = moment(oppgave.førsteUttakMåned);
    if (dato.isBefore(forste)) {
      forste = dato;
    }
    if (!siste || dato.isAfter(siste)) {
      siste = dato;
    }
  });

  if (oppgaverSomErApneEllerPaVent.some((oppgave) => !oppgave.førsteUttakMåned)) {
    return [forste, siste.add(1, 'months').add(1, 'day')];
  }

  return [forste, siste];
};

const lagKoordinat = (dato: moment.Moment, oppgaver: { x: string; y: number }[]): KoordinatDato => {
  const eksisterendeDato2 = oppgaver.find((d) => moment(d.x).isSame(dato));
  if (eksisterendeDato2) {
    return {
      x: moment(eksisterendeDato2.x).toDate(),
      y: eksisterendeDato2.y,
    };
  }
  return {
    x: dato.toDate(),
    y: 0,
  };
};

const fyllInnManglendeDatoerOgSorterEtterDato = (
  oppgaverPaVent: { x: string; y: number }[],
  oppgaverIkkePaVent: { x: string; y: number }[],
  periodeStart: moment.Moment,
  periodeSlutt: moment.Moment,
): { koordinaterPaVent: KoordinatDato[], koordinaterIkkePaVent: KoordinatDato[] } => {
  const koordinaterPaVent: KoordinatDato[] = [];
  const koordinaterIkkePaVent: KoordinatDato[] = [];
  if (!periodeStart || !periodeSlutt) {
    return {
      koordinaterPaVent,
      koordinaterIkkePaVent,
    };
  }

  let dato = periodeStart;
  do {
    koordinaterPaVent.push(lagKoordinat(dato, oppgaverPaVent));
    koordinaterIkkePaVent.push(lagKoordinat(dato, oppgaverIkkePaVent));
    dato = dato.add(1, 'month');
  } while (dato.isBefore(periodeSlutt));

  koordinaterPaVent.push({
    x: periodeSlutt.toDate(),
    y: oppgaverPaVent.find((d) => d.x === 'ukjent')?.y || 0,
  });
  koordinaterIkkePaVent.push({
    x: periodeSlutt.toDate(),
    y: oppgaverIkkePaVent.find((d) => d.x === 'ukjent')?.y || 0,
  });

  return {
    koordinaterPaVent,
    koordinaterIkkePaVent,
  };
};

const getHintAntall = (verdi: Koordinat, intl: IntlShape): string => intl.formatMessage({ id: 'FordelingAvBehandlingstypeGraf.Antall' }, {
  antall: verdi.y0 ? verdi.y - verdi.y0 : verdi.y,
});

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

interface KoordinatDato {
  x: Date;
  y: number;
}

interface Koordinat {
  x: number;
  x0: number;
  y: number;
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

  const [forsteDato, sisteDato] = finnForsteOgSisteDato(oppgaverApneEllerPaVent);

  const oppgaverPaVent = useMemo(() => grupperAntallPaDato(oppgaverApneEllerPaVent
    .filter((o) => o.behandlingVenteStatus.kode === behandlingVenteStatus.PA_VENT)), [oppgaverApneEllerPaVent]);
  const oppgaverIkkePaVent = useMemo(() => grupperAntallPaDato(oppgaverApneEllerPaVent
    .filter((o) => o.behandlingVenteStatus.kode === behandlingVenteStatus.IKKE_PA_VENT)), [oppgaverApneEllerPaVent]);
  const isEmpty = oppgaverPaVent.length === 0 && oppgaverIkkePaVent.length === 0;

  const { koordinaterPaVent, koordinaterIkkePaVent } = fyllInnManglendeDatoerOgSorterEtterDato(oppgaverPaVent, oppgaverIkkePaVent, forsteDato, sisteDato);

  const dataKoor = settCustomBreddePaSoylene(koordinaterPaVent);
  const dataKoorIkkePaVent = settCustomBreddePaSoylene(koordinaterIkkePaVent);

  const plotPropsWhenEmpty = isEmpty ? {
    yDomain: [0, 10],
    xDomain: [moment(forsteDato).toDate(), moment(sisteDato).toDate()],
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
                left: 170, right: 40, top: 40, bottom: 50,
              }}
              width={width - LEGEND_WIDTH > 0 ? width - LEGEND_WIDTH : 100 + LEGEND_WIDTH}
              height={height}
              stackBy="y"
              {...plotPropsWhenEmpty}
            >
              <HorizontalGridLines />
              <XAxis
                style={{ text: cssText }}
                tickFormat={(index) => {
                  if (isEmpty) {
                    return '';
                  }
                  console.log(koordinaterPaVent);
                  console.log('test');
                  console.log(koordinaterIkkePaVent);
                  const koordinat = koordinaterPaVent.length > 0 ? koordinaterPaVent : koordinaterIkkePaVent;

                  if (index === koordinat.length) {
                    return (
                      <tspan>
                        <tspan x="0" dy="1em">Ukjent</tspan>
                        <tspan x="0" dy="1em">dato</tspan>
                      </tspan>
                    );
                  }

                  return (
                    <tspan>
                      <tspan x="0" dy="1em">{getYearText(moment(koordinat[index - 1].x).month(), intl)}</tspan>
                      <tspan x="0" dy="1em">{moment(koordinat[index - 1].x).year()}</tspan>
                    </tspan>
                  );
                }}
              />
              <YAxis
                style={{ text: cssText }}
                title="Antall"
              />
              <VerticalRectSeries
                data={dataKoorIkkePaVent}
                onValueMouseOver={leggTilHintVerdi}
                onValueMouseOut={fjernHintVerdi}
                fill="#337c9b"
                stroke="#337c9b"
              />
              <VerticalRectSeries
                data={dataKoor}
                onValueMouseOver={leggTilHintVerdi}
                onValueMouseOut={fjernHintVerdi}
                fill="#38a161"
                stroke="#38a161"
              />
              {hintVerdi && (
                <Hint value={hintVerdi}>
                  <div className={styles.hint}>
                    {getHintAntall(hintVerdi, intl)}
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
