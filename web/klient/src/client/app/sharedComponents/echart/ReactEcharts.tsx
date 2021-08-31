import React, { useRef, useEffect, FunctionComponent } from 'react';
import { init, getInstanceByDom } from 'echarts';
import type { CSSProperties } from 'react';
import type { EChartsOption, ECharts } from 'echarts';

export interface OwnProps {
  option: EChartsOption;
  style?: CSSProperties;
  width: number;
  height: number;
}

const ReactECharts: FunctionComponent<OwnProps> = ({
  option,
  style,
  width,
  height,
}): JSX.Element => {
  const chartRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    let chart: ECharts | undefined;
    if (chartRef.current !== null) {
      chart = init(chartRef.current);
    }

    const resizeChart = () => {
      chart?.resize();
    };
    window.addEventListener('resize', resizeChart);

    return () => {
      chart?.dispose();
      window.removeEventListener('resize', resizeChart);
    };
  }, []);

  useEffect(() => {
    if (chartRef.current !== null) {
      const chart = getInstanceByDom(chartRef.current);
      chart.setOption(option);
    }
  }, [chartRef.current, option]);

  return <div ref={chartRef} style={{ width, height, ...style }} />;
};

export default ReactECharts;
