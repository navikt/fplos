import React, { FunctionComponent, useMemo } from 'react';
import { injectIntl, WrappedComponentProps } from 'react-intl';
import Panel from 'nav-frontend-paneler';

import Kodeverk from 'types/kodeverkTsType';
import BehandlingType from 'kodeverk/behandlingType';
import OppgaverForAvdeling from 'types/avdelingsleder/oppgaverForAvdelingTsType';

import 'react-vis/dist/style.css';
import ReactECharts from 'sharedComponents/echart/ReactEcharts';

const behandlingstypeOrder = [
  BehandlingType.TILBAKEBETALING_REVURDERING,
  BehandlingType.TILBAKEBETALING,
  BehandlingType.DOKUMENTINNSYN,
  BehandlingType.KLAGE,
  BehandlingType.REVURDERING,
  BehandlingType.FORSTEGANGSSOKNAD];

const slåSammen = (oppgaverForAvdeling: OppgaverForAvdeling[]): number[] => {
  const test = oppgaverForAvdeling
    .reduce((acc, o) => {
      const index = behandlingstypeOrder.findIndex((bo) => bo === o.behandlingType.kode) + 1;
      return {
        ...acc,
        [index]: (acc[index] ? acc[index] + o.antall : o.antall),
      };
    }, {} as Record<string, number>);

  return behandlingstypeOrder.map((b, index) => test[index + 1]);
};

interface OwnProps {
  intl: any;
  width: number;
  height: number;
  behandlingTyper: Kodeverk[];
  oppgaverForAvdeling: OppgaverForAvdeling[];
}

/**
 * FordelingAvBehandlingstypeGraf.
 */
const FordelingAvBehandlingstypeGraf: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  width,
  height,
  oppgaverForAvdeling,
  behandlingTyper,
}) => {
  const tilBehandlingTekst = intl.formatMessage({ id: 'FordelingAvBehandlingstypeGraf.TilBehandling' });
  const tilBeslutterTekst = intl.formatMessage({ id: 'FordelingAvBehandlingstypeGraf.TilBeslutter' });

  const finnBehandlingTypeNavn = useMemo(() => behandlingstypeOrder.map((t) => {
    const type = behandlingTyper.find((bt) => bt.kode === t);
    return type ? type.navn : '';
  }), [behandlingTyper]);

  const tilBehandlingData = slåSammen(oppgaverForAvdeling.filter((o) => o.tilBehandling));
  const tilBeslutterData = slåSammen(oppgaverForAvdeling.filter((o) => !o.tilBehandling));

  return (
    <Panel>
      <ReactECharts
        width={width}
        height={height}
        option={{
          tooltip: {
            trigger: 'axis',
            axisPointer: { // Use axis to trigger tooltip
              type: 'shadow', // 'shadow' as default; can also be 'line' or 'shadow'
            },
          },
          legend: {
            data: [tilBehandlingTekst, tilBeslutterTekst],
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true,
          },
          xAxis: {
            type: 'value',
          },
          yAxis: {
            type: 'category',
            data: finnBehandlingTypeNavn,
          },
          series: [
            {
              name: tilBehandlingTekst,
              type: 'bar',
              stack: 'total',
              label: {
                show: true,
              },
              emphasis: {
                focus: 'series',
              },
              data: tilBehandlingData,
            },
            {
              name: tilBeslutterTekst,
              type: 'bar',
              stack: 'total',
              label: {
                show: true,
              },
              emphasis: {
                focus: 'series',
              },
              data: tilBeslutterData,
            },
          ],
          color: ['#337c9b', '#38a161'],
        }}
      />
    </Panel>
  );
};

export default injectIntl(FordelingAvBehandlingstypeGraf);
