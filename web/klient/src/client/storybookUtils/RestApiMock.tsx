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

  return (
    <RestApiGlobalStateMock data={dataMedGlobalMarkør.filter((d) => d.erGlobalData)}>
      <AxiosMock data={dataMedGlobalMarkør.filter((d) => !d.erGlobalData)}>
        {children}
      </AxiosMock>
    </RestApiGlobalStateMock>
  );
};

export default RestApiMock;
