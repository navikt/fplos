import React, { FunctionComponent, useMemo } from 'react';
import { injectIntl, WrappedComponentProps } from 'react-intl';
import { Panel } from '@navikt/ds-react';
import NyeOgFerdigstilteOppgaver from 'types/saksbehandler/nyeOgFerdigstilteOppgaverTsType';
import BehandlingType from 'kodeverk/behandlingType';
import ReactECharts from 'app/ReactECharts';
import { KodeverkMedNavn } from '@navikt/ft-types';

const behandlingstypeOrder = [
  BehandlingType.TILBAKEBETALING_REVURDERING,
  BehandlingType.TILBAKEBETALING,
  BehandlingType.DOKUMENTINNSYN,
  BehandlingType.KLAGE,
  BehandlingType.REVURDERING,
  BehandlingType.FORSTEGANGSSOKNAD];

interface OwnProps {
  height: number;
  behandlingTyper: KodeverkMedNavn[];
  nyeOgFerdigstilteOppgaver: NyeOgFerdigstilteOppgaver[];
}

/**
 * NyeOgFerdigstilteOppgaverForIdagGraf
 */
export const NyeOgFerdigstilteOppgaverForIdagGraf: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  height,
  nyeOgFerdigstilteOppgaver,
  behandlingTyper,
}) => {
  const behandlingTypeNavnForYAkse = useMemo(() => behandlingstypeOrder.map((bType) => {
    if (bType === BehandlingType.FORSTEGANGSSOKNAD) {
      return intl.formatMessage({ id: 'NyeOgFerdigstilteOppgaverForIdagGraf.Førstegangsbehandling' });
    }

    const type = behandlingTyper.find((bt) => bt.kode === bType);
    return type ? type.navn : '';
  }), [behandlingTyper]);

  const ferdigLabel = intl.formatMessage({ id: 'NyeOgFerdigstilteOppgaverForIdagGraf.Ferdigstilte' });
  const nyLabel = intl.formatMessage({ id: 'NyeOgFerdigstilteOppgaverForIdagGraf.Nye' });

  const dataFerdigstilte = useMemo(() => behandlingstypeOrder.map((type) => {
    const oppgave = nyeOgFerdigstilteOppgaver.find((o) => o.behandlingType === type);
    if (oppgave) {
      return oppgave.antallFerdigstilte;
    }
    return 0;
  }), [nyeOgFerdigstilteOppgaver]);
  const dataNye = useMemo(() => behandlingstypeOrder.map((type) => {
    const oppgave = nyeOgFerdigstilteOppgaver.find((o) => o.behandlingType === type);
    if (oppgave) {
      return oppgave.antallNye;
    }
    return 0;
  }), [nyeOgFerdigstilteOppgaver]);

  return (
    <Panel>
      <ReactECharts
        height={height}
        option={{
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow',
            },
          },
          legend: {
            data: [ferdigLabel, nyLabel],
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true,
          },
          xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01],
          },
          yAxis: {
            type: 'category',
            data: behandlingTypeNavnForYAkse,
          },
          series: [
            {
              name: ferdigLabel,
              type: 'bar',
              data: dataFerdigstilte,
            },
            {
              name: nyLabel,
              type: 'bar',
              data: dataNye,
            },
          ],
          color: ['#38a161', '#337c9b'],
        }}
      />
    </Panel>
  );
};

export default injectIntl(NyeOgFerdigstilteOppgaverForIdagGraf);
