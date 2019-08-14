import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import {
  XYPlot, XAxis, YAxis, VerticalGridLines, HorizontalRectSeries, Hint, DiscreteColorLegend,
} from 'react-vis';
import { FormattedMessage, injectIntl, intlShape } from 'react-intl';
import { Normaltekst } from 'nav-frontend-typografi';
import Panel from 'nav-frontend-paneler';

import { FlexContainer, FlexRow, FlexColumn } from 'sharedComponents/flexGrid';
import { Kodeverk } from 'kodeverk/kodeverkTsType';
import behandlingType from 'kodeverk/behandlingType';
import kodeverkPropType from 'kodeverk/kodeverkPropType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { getKodeverk } from 'kodeverk/duck';
import { OppgaverForAvdeling } from './oppgaverForAvdelingTsType';
import oppgaverForAvdelingPropType from './oppgaverForAvdelingPropType';

import 'react-vis/dist/style.css';
import styles from './fordelingAvBehandlingstypeGraf.less';

const LEGEND_WIDTH = 210;

const behandlingstypeOrder = [behandlingType.DOKUMENTINNSYN, behandlingType.KLAGE, behandlingType.REVURDERING, behandlingType.FORSTEGANGSSOKNAD];

const settCustomHoydePaSoylene = (data) => {
  const transformert = data.map(el => ({
    ...el,
    y0: el.y + 0.30,
    y: el.y - 0.30,
  }));
  transformert.unshift({ x: 0, y: 0.5 });
  transformert.push({ x: 0, y: 4.5 });
  return transformert;
};

const formatData = (oppgaverForAvdeling) => {
  const sammenslatteBehandlingstyper = oppgaverForAvdeling
    .reduce((acc, o) => {
      const index = behandlingstypeOrder.indexOf(o.behandlingType.kode) + 1;
      return {
        ...acc,
        [index]: (acc[index] ? acc[index] + o.antall : o.antall),
      };
    }, {});

  return Object.keys(sammenslatteBehandlingstyper)
    .map(k => ({ x: sammenslatteBehandlingstyper[k], y: parseInt(k, 10) }));
};

const cssText = {
  fontFamily: 'Source Sans Pro, Arial, sans-serif',
  fontSize: '1rem',
  lineHeight: '1.375rem',
  fontWeight: 400,
};

const getHintAntall = (verdi, intl) => intl.formatMessage({ id: 'FordelingAvBehandlingstypeGraf.Antall' }, {
  antall: verdi.x0 ? verdi.x - verdi.x0 : verdi.x,
});
const getHintTotalAntall = (verdi, tilBeslutter, tilSaksbehandling, intl) => {
  const y = Math.ceil(verdi.y);
  const beslutterAntall = tilBeslutter.find(b => b.y === y);
  const sum1 = beslutterAntall ? beslutterAntall.x : 0;
  const saksbehandlingAntall = tilSaksbehandling.find(b => b.y === y);
  const sum2 = saksbehandlingAntall ? saksbehandlingAntall.x : 0;
  return intl.formatMessage({ id: 'FordelingAvBehandlingstypeGraf.TotaltAntall' }, { antall: sum1 + sum2 });
};

interface TsProps {
  intl: any;
  width: number;
  height: number;
  behandlingTyper: Kodeverk[];
  oppgaverForAvdeling: OppgaverForAvdeling[];
}

interface StateTsProps {
  hintVerdi: any;
}

/**
 * FordelingAvBehandlingstypeGraf.
 */
export class FordelingAvBehandlingstypeGraf extends Component<TsProps, StateTsProps> {
  static propTypes = {
    intl: intlShape.isRequired,
    width: PropTypes.number.isRequired,
    height: PropTypes.number.isRequired,
    behandlingTyper: PropTypes.arrayOf(kodeverkPropType).isRequired,
    oppgaverForAvdeling: PropTypes.arrayOf(oppgaverForAvdelingPropType).isRequired,
  };

  constructor(props: TsProps) {
    super(props);

    this.state = {
      hintVerdi: undefined,
    };
  }

  leggTilHintVerdi = (hintVerdi: {x: number; x0: number; y: number}) => {
    this.setState(prevState => ({ ...prevState, hintVerdi }));
  };

  fjernHintVerdi = () => {
    this.setState(prevState => ({ ...prevState, hintVerdi: undefined }));
  };

  finnBehandlingTypeNavn = (behandlingTypeKode: string) => {
    const {
      behandlingTyper,
    } = this.props;
    const type = behandlingTyper.find(bt => bt.kode === behandlingTypeKode);
    return type ? type.navn : '';
  }

  render = () => {
    const {
      intl, width, height, oppgaverForAvdeling,
    } = this.props;
    const {
      hintVerdi,
    } = this.state;


    const tilSaksbehandling = formatData(oppgaverForAvdeling.filter(o => o.tilBehandling));
    const tilBeslutter = formatData(oppgaverForAvdeling.filter(o => !o.tilBehandling));
    const isEmpty = tilSaksbehandling.length === 0 && tilBeslutter.length === 0;

    return (
      <Panel className={styles.panel}>
        <FlexContainer>
          <FlexRow>
            <FlexColumn>
              <XYPlot
                dontCheckIfEmpty={isEmpty}
                margin={{
                  left: 170, right: 40, top: 40, bottom: 0,
                }}
                width={width - LEGEND_WIDTH > 0 ? width - LEGEND_WIDTH : 100 + LEGEND_WIDTH}
                height={height}
                stackBy="x"
                yDomain={[0, 5]}
                {...(isEmpty ? { xDomain: [0, 100] } : {})}
              >
                <VerticalGridLines />
                <XAxis orientation="top" style={{ text: cssText }} />
                <YAxis
                  style={{ text: cssText }}
                  tickFormat={(v, i) => this.finnBehandlingTypeNavn(behandlingstypeOrder[i])}
                  tickValues={[1, 2, 3, 4]}
                />
                <HorizontalRectSeries
                  data={settCustomHoydePaSoylene(tilSaksbehandling)}
                  onValueMouseOver={this.leggTilHintVerdi}
                  onValueMouseOut={this.fjernHintVerdi}
                  fill="#337c9b"
                  stroke="#337c9b"
                />
                <HorizontalRectSeries
                  data={settCustomHoydePaSoylene(tilBeslutter)}
                  onValueMouseOver={this.leggTilHintVerdi}
                  onValueMouseOut={this.fjernHintVerdi}
                  fill="#38a161"
                  stroke="#38a161"
                />
                {hintVerdi && (
                <Hint value={hintVerdi}>
                  <div className={styles.hint}>
                    {getHintAntall(hintVerdi, intl)}
                    <br />
                    {getHintTotalAntall(hintVerdi, tilBeslutter, tilSaksbehandling, intl)}
                  </div>
                </Hint>
                )}
              </XYPlot>
            </FlexColumn>
            <FlexColumn>
              <DiscreteColorLegend
                colors={['#337c9b', '#38a161']}
                items={[
                  <Normaltekst className={styles.displayInline}>
                    <FormattedMessage id="FordelingAvBehandlingstypeGraf.TilBehandling" />
                  </Normaltekst>,
                  <Normaltekst className={styles.displayInline}>
                    <FormattedMessage id="FordelingAvBehandlingstypeGraf.TilBeslutter" />
                  </Normaltekst>,
                ]}
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

export default connect(mapStateToProps)(injectIntl(FordelingAvBehandlingstypeGraf));
