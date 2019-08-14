import React, { Component } from 'react';
import PropTypes from 'prop-types';
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

import styles from './manueltPaVentGraf.less';

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

interface TsProps {
  width: number;
  height: number;
  isFireUkerValgt: boolean;
  data: Koordinat[];
  isEmpty: boolean;
}

interface StateTsProps {
  crosshairValues: Koordinat[];
}

/**
 * ManueltPaVentGraf.
 */
export class ManueltPaVentGraf extends Component<TsProps, StateTsProps> {
  static propTypes = {
    width: PropTypes.number.isRequired,
    height: PropTypes.number.isRequired,
    isFireUkerValgt: PropTypes.bool.isRequired,
    data: PropTypes.arrayOf(PropTypes.shape({
      x: PropTypes.instanceOf(Date).isRequired,
      y: PropTypes.number.isRequired,
    })).isRequired,
    isEmpty: PropTypes.bool.isRequired,
  };

  constructor(props: TsProps) {
    super(props);

    this.state = {
      crosshairValues: [],
    };
  }

  leggTilHintVerdi = (value: {x: Date; y: number}) => {
    this.setState(prevState => ({ ...prevState, crosshairValues: [value] }));
  };

  fjernHintVerdi = () => {
    this.setState(prevState => ({ ...prevState, crosshairValues: [] }));
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
          stackBy="y"
          onMouseLeave={this.fjernHintVerdi}
          {...(isEmpty ? { yDomain: [0, 50] } : {})}
        >
          <HorizontalGridLines />
          <XAxis
            tickTotal={6}
            tickFormat={x => moment(x).format(DDMMYYYY_DATE_FORMAT)}
            style={{ text: cssText }}
          />
          <YAxis style={{ text: cssText }} />
          <AreaSeries
            data={data}
            onNearestX={this.leggTilHintVerdi}
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
  }
}

export const lagKoordinater = createSelector([(state, ownProps) => ownProps], ownProps => ownProps.oppgaverManueltPaVent.map(o => ({
  x: moment(o.behandlingFrist).startOf('day').toDate(),
  y: o.antall,
})));

export const lagDatastruktur = createSelector([lagKoordinater, (state, ownProps) => ownProps], (koordinater: Koordinat[], ownProps) => {
  const nyeKoordinater = [];
  const periodeStart = moment().startOf('day').toDate();
  const periodeSlutt = moment().add(ownProps.isFireUkerValgt ? 4 : 8, 'w').toDate();

  for (let dato = moment(periodeStart); dato.isSameOrBefore(periodeSlutt); dato = dato.add(1, 'days')) {
    const funnetKoordinat = koordinater.find(k => moment(k.x).isSame(dato));
    nyeKoordinater.push({
      x: dato.toDate(),
      y: funnetKoordinat ? funnetKoordinat.y : 0,
    });
  }

  return nyeKoordinater;
});

export const harDatastrukturKun0Verdier = createSelector([lagKoordinater], koordinater => !koordinater.some(k => k.y !== 0));

const mapStateToProps = (state, ownProps) => ({
  data: lagDatastruktur(state, ownProps),
  isEmpty: harDatastrukturKun0Verdier(state, ownProps),
});

export default connect(mapStateToProps)(ManueltPaVentGraf);
