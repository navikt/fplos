import React, { FunctionComponent, useMemo } from 'react';
import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import AxiosMock from './AxiosMock';
import RestApiGlobalStateMock from './RestApiGlobalStateMock';

interface Props {
    children: any;
    data: {
      key: string;
      data: any,
    }[];
}

const RestApiMock: FunctionComponent<Props> = ({
  children,
  data,
}) => {
  const dataMedGlobalMarkør = useMemo(() => data.map((d) => {
    const erGlobalData = Object.values(RestApiGlobalStatePathsKeys).some((key) => key.name === d.key);
    return {
      ...d,
      erGlobalData,
    };
  }), [data]);

  const globalData = useMemo(() => dataMedGlobalMarkør.filter((d) => d.erGlobalData), [dataMedGlobalMarkør]);

  return (
    <RestApiGlobalStateMock data={globalData}>
      <AxiosMock data={data}>
        {children}
      </AxiosMock>
    </RestApiGlobalStateMock>
  );
};

export default RestApiMock;
