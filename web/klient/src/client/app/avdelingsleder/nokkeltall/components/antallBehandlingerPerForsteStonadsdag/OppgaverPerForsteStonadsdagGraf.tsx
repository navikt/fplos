import React, { Component } from 'react';
import { connect } from 'react-redux';
import {
  XYPlot, XAxis, YAxis, AreaSeries, Crosshair, HorizontalGridLines,
} from 'react-vis';
import { createSelector } from 'reselect';
import moment from 'moment';
import { FormattedMessage } from 'react-intl';
import Panel from 'nav-frontend-paneler';
import { Normaltekst, Undertekst } from 'nav-frontend-typografi';

import { DDMMYYYY_DATE_FORMAT } from 'utils/formats';

import styles from './oppgaverPerForsteStonadsdagGraf.less';

const cssText = {
  fontFamily: 'Source Sans Pro, Arial, sans-serif',
  fontSize: '1rem',
  lineHeight: '1.375rem',
  fontWeight: 400,
};

interface Koordinat {
  x: Date;
  y: number;
}

interface OwnProps {
  width: number;
  height: number;
  data: Koordinat[];
  isEmpty: boolean;
}

interface StateProps {
  crosshairValues: Koordinat[];
}

/**
 * OppgaverPerForsteStonadsdagGraf.
 */
export class OppgaverPerForsteStonadsdagGraf extends Component<OwnProps, StateProps> {
  constructor(props: OwnProps) {
    super(props);

    this.state = {
      crosshairValues: [],
    };
  }

  leggTilHintVerdi = (value: {x: Date; y: number}) => {
    this.setState((prevState) => ({ ...prevState, crosshairValues: [value] }));
  };

  fjernHintVerdi = () => {
    this.setState((prevState) => ({ ...prevState, crosshairValues: [] }));
  };

  render = () => {
    const {
      width, height, data, isEmpty,
    } = this.props;
    const {
      crosshairValues,
    } = this.state;

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
          onMouseLeave={this.fjernHintVerdi}
          {...(isEmpty ? { yDomain: [0, 50], xDomain: [moment().subtract(5, 'd'), moment().add(5, 'd')] } : {})}
        >
          <HorizontalGridLines />
          <XAxis
            tickTotal={5}
            tickFormat={(x) => moment(x).format(DDMMYYYY_DATE_FORMAT)}
            style={{ text: cssText }}
          />
          <YAxis style={{ text: cssText }} />
          <AreaSeries
            data={data}
            onNearestX={this.leggTilHintVerdi}
            fill="#337c9b"
            stroke="#337c9b"
          />
          <Crosshair
            values={[{ x: moment().toDate(), y: 0 }]}
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
  }
}

export const lagKoordinater = createSelector([(state, ownProps) => ownProps], (ownProps) => ownProps.oppgaverPerForsteStonadsdag.map((o) => ({
  x: moment(o.forsteStonadsdag).startOf('day').toDate(),
  y: o.antall,
})));

export const lagDatastruktur = createSelector([lagKoordinater], (koordinater: Koordinat[]) => {
  const nyeKoordinater = [];
  const periodeStart = koordinater
    .map((koordinat) => moment(koordinat.x))
    .reduce((tidligesteDato, dato) => (tidligesteDato.isSameOrBefore(dato) ? tidligesteDato : dato), moment().startOf('day'))
    .toDate();
  const periodeSlutt = koordinater
    .map((koordinat) => moment(koordinat.x))
    .reduce((senesteDato, dato) => (senesteDato.isSameOrAfter(dato) ? senesteDato : dato), moment().startOf('day'))
    .toDate();

  for (let dato = moment(periodeStart); dato.isSameOrBefore(periodeSlutt); dato = dato.add(1, 'days')) {
    const funnetKoordinat = koordinater.find((k) => moment(k.x).isSame(dato));
    nyeKoordinater.push({
      x: dato.toDate(),
      y: funnetKoordinat ? funnetKoordinat.y : 0,
    });
  }
  return nyeKoordinater;
});

export const harDatastrukturKun0Verdier = createSelector([lagKoordinater], (koordinater) => !koordinater.some((k) => k.y !== 0));

const mapStateToProps = (state, ownProps) => ({
  data: lagDatastruktur(state, ownProps),
  isEmpty: harDatastrukturKun0Verdier(state, ownProps),
});

export default connect(mapStateToProps)(OppgaverPerForsteStonadsdagGraf);
