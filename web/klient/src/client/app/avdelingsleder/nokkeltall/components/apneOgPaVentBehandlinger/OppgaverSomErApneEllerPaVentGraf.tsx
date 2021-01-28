import React, {
  useMemo, FunctionComponent,
} from 'react';
import {
  XYPlot, XAxis, YAxis, HorizontalGridLines, VerticalRectSeries, DiscreteColorLegend,
} from 'react-vis';
import {
  FormattedMessage, injectIntl, WrappedComponentProps,
} from 'react-intl';
import moment from 'moment';
import Panel from 'nav-frontend-paneler';
import { Normaltekst } from 'nav-frontend-typografi';

import behandlingVenteStatus from 'kodeverk/behandlingVenteStatus';
import { FlexContainer, FlexRow, FlexColumn } from 'sharedComponents/flexGrid';
import Kodeverk from 'kodeverk/kodeverkTsType';

import 'react-vis/dist/style.css';
import styles from './oppgaverSomErApneEllerPaVentGraf.less';
import OppgaverSomErApneEllerPaVent from './oppgaverSomErApneEllerPaVentTsType';

const LEGEND_WIDTH = 210;

const settCustomBreddePaSoylene = (data: KoordinatDato[]): Koordinat[] => {
  const transformert = data.map((el, index) => ({
    ...el,
    x0: index + 1 - 0.30,
    x: index + 1 + 0.30,
    y: el.y,
  }));
  /* transformert.unshift({
    x: 0, y0: 0, x0: 0, y: 0.5,
  });
  transformert.push({
    x: 0, y0: 0, x0: 0, y: 4.5,
  }); */
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

  for (let dato = periodeStart; dato.isBefore(periodeSlutt); dato = dato.add(1, 'month')) {
    koordinaterPaVent.push(lagKoordinat(dato, oppgaverPaVent));
    koordinaterIkkePaVent.push(lagKoordinat(dato, oppgaverIkkePaVent));
  }

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
  behandlingTyper: Kodeverk[];
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
  behandlingTyper,
}) => {
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
                left: 170, right: 40, top: 40, bottom: 30,
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
                  if (index === koordinaterIkkePaVent.length) {
                    return 'Ukjent';
                  }
                  return `${moment(koordinaterIkkePaVent[index - 1].x).month() + 1}`;
                }}
              />
              <YAxis
                style={{ text: cssText }}
              />
              <VerticalRectSeries
                data={dataKoorIkkePaVent}
                fill="#337c9b"
                stroke="#337c9b"
              />
              <VerticalRectSeries
                data={dataKoor}
                fill="#38a161"
                stroke="#38a161"
              />
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
