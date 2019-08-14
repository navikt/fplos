import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import moment from 'moment';
import {
  XYPlot, XAxis, YAxis, HorizontalGridLines, AreaSeries, DiscreteColorLegend, Crosshair,
} from 'react-vis';
import Panel from 'nav-frontend-paneler';
import { Normaltekst, Undertekst } from 'nav-frontend-typografi';

import { FlexContainer, FlexRow, FlexColumn } from 'sharedComponents/flexGrid';
import { DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import behandlingType from 'kodeverk/behandlingType';
import { Kodeverk } from 'kodeverk/kodeverkTsType';
import kodeverkPropType from 'kodeverk/kodeverkPropType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { getKodeverk } from 'kodeverk/duck';


import 'react-vis/dist/style.css';

import styles from './tilBehandlingGraf.less';

const LEGEND_WIDTH = 260;

const behandlingstypeOrder = [behandlingType.DOKUMENTINNSYN, behandlingType.KLAGE, behandlingType.REVURDERING, behandlingType.FORSTEGANGSSOKNAD];

const behandlingstypeFarger = {
  [behandlingType.DOKUMENTINNSYN]: '#ffa733',
  [behandlingType.KLAGE]: '#826ba1',
  [behandlingType.REVURDERING]: '#3385d1',
  [behandlingType.FORSTEGANGSSOKNAD]: '#85d5f0',
};

const cssText = {
  fontFamily: 'Source Sans Pro, Arial, sans-serif',
  fontSize: '1rem',
  lineHeight: '1.375rem',
  fontWeight: 400,
};

const sorterBehandlingtyper = (b1, b2) => {
  const index1 = behandlingstypeOrder.indexOf(b1);
  const index2 = behandlingstypeOrder.indexOf(b2);
  if (index1 === index2) {
    return 0;
  }
  return index1 > index2 ? -1 : 1;
};

const konverterTilKoordinaterGruppertPaBehandlingstype = oppgaverForAvdeling => oppgaverForAvdeling.reduce((acc, o) => {
  const nyKoordinat = {
    x: moment(o.opprettetDato).startOf('day').toDate(),
    y: o.antall,
  };

  const eksisterendeKoordinater = acc[o.behandlingType.kode];
  return {
    ...acc,
    [o.behandlingType.kode]: (eksisterendeKoordinater ? eksisterendeKoordinater.concat(nyKoordinat) : [nyKoordinat]),
  };
}, {});

const fyllInnManglendeDatoerOgSorterEtterDato = (data, periodeStart, periodeSlutt) => Object.keys(data).reduce((acc, behandlingstype) => {
  const behandlingstypeData = data[behandlingstype];
  const koordinater = [];

  for (let dato = moment(periodeStart); dato.isSameOrBefore(periodeSlutt); dato = dato.add(1, 'days')) {
    const funnetDato = behandlingstypeData.find(d => moment(d.x).startOf('day').isSame(dato.startOf('day')));
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

const finnAntallForBehandlingstypeOgDato = (data, behandlingstype, dato) => {
  const koordinat = data[behandlingstype].find(d => d.x.getTime() === dato.getTime());
  return koordinat.y;
};

export interface OppgaveForDato {
  behandlingType: Kodeverk;
  opprettetDato: string;
  antall: number;
}

interface TsProps {
  width: number;
  height: number;
  behandlingTyper: Kodeverk[];
  oppgaverPerDato: OppgaveForDato[];
  isToUkerValgt: boolean;
}

interface CrosshairValue {
  x: Date;
  y: number;
}

interface StateTsProps {
  crosshairValues: CrosshairValue[];
}

/**
 * TilBehandlingGraf.
 */
export class TilBehandlingGraf extends Component<TsProps, StateTsProps> {
  static propTypes = {
    width: PropTypes.number.isRequired,
    height: PropTypes.number.isRequired,
    behandlingTyper: PropTypes.arrayOf(kodeverkPropType).isRequired,
    oppgaverPerDato: PropTypes.arrayOf(PropTypes.shape({
      behandlingType: kodeverkPropType.isRequired,
      opprettetDato: PropTypes.string.isRequired,
      antall: PropTypes.number.isRequired,
    })).isRequired,
    isToUkerValgt: PropTypes.bool.isRequired,
  };

  constructor(props: TsProps) {
    super(props);

    this.state = {
      crosshairValues: [],
    };
  }

  onMouseLeave = () => this.setState({ crosshairValues: [] });

  onNearestX = (value: {x: Date; y: number}) => {
    this.setState({
      crosshairValues: [value],
    });
  }

  finnBehandlingTypeNavn = (behandlingTypeKode: string) => {
    const {
      behandlingTyper,
    } = this.props;
    const type = behandlingTyper.find(bt => bt.kode === behandlingTypeKode);
    return type ? type.navn : '';
  }

  render = () => {
    const {
      width, height, oppgaverPerDato, isToUkerValgt,
    } = this.props;
    const {
      crosshairValues,
    } = this.state;

    const periodeStart = moment().subtract(isToUkerValgt ? 2 : 4, 'w').add(1, 'd');
    const periodeSlutt = moment();

    const koordinater = konverterTilKoordinaterGruppertPaBehandlingstype(oppgaverPerDato);
    const data = fyllInnManglendeDatoerOgSorterEtterDato(koordinater, periodeStart, periodeSlutt);

    const sorterteBehandlingstyper = Object.keys(data).sort(sorterBehandlingtyper);
    const revsersertSorterteBehandlingstyper = sorterteBehandlingstyper.slice().reverse();

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
              <XYPlot
                dontCheckIfEmpty={isEmpty}
                width={width - LEGEND_WIDTH > 0 ? width - LEGEND_WIDTH : 100 + LEGEND_WIDTH}
                height={height}
                margin={{
                  left: 40, right: 40, top: 20, bottom: 40,
                }}
                stackBy="y"
                xType="time"
                onMouseLeave={this.onMouseLeave}
                {...plotPropsWhenEmpty}
              >
                <HorizontalGridLines />
                <XAxis
                  tickTotal={5}
                  tickFormat={t => moment(t).format(DDMMYYYY_DATE_FORMAT)}
                  style={{ text: cssText }}
                />
                <YAxis style={{ text: cssText }} />
                {sorterteBehandlingstyper.map((k, index) => (
                  <AreaSeries
                    key={k}
                    data={data[k]}
                    onNearestX={index === 0 ? this.onNearestX : () => undefined}
                    fill={behandlingstypeFarger[k]}
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
                    <Normaltekst>{`${moment(crosshairValues[0].x).format(DDMMYYYY_DATE_FORMAT)}`}</Normaltekst>
                    { revsersertSorterteBehandlingstyper.map(key => (
                      <Undertekst key={key}>
                        {`${this.finnBehandlingTypeNavn(key)}: ${finnAntallForBehandlingstypeOgDato(data, key, crosshairValues[0].x)}`}
                      </Undertekst>
                    ))}
                  </div>
                </Crosshair>
                )}
              </XYPlot>
            </FlexColumn>
            <FlexColumn>
              <DiscreteColorLegend
                colors={revsersertSorterteBehandlingstyper.map(key => behandlingstypeFarger[key])}
                items={revsersertSorterteBehandlingstyper.map(key => (
                  <Normaltekst className={styles.displayInline}>{this.finnBehandlingTypeNavn(key)}</Normaltekst>
                ))}
              />
            </FlexColumn>
          </FlexRow>
        </FlexContainer>
      </Panel>
    );
  }
}

const mapStateToProps = state => ({
  behandlingTyper: getKodeverk(kodeverkTyper.BEHANDLING_TYPE)(state),
});

export default connect(mapStateToProps)(TilBehandlingGraf);
