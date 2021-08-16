import React, { FunctionComponent } from 'react';
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
  const dataMedGlobalMarkør = data.map((d) => {
    const erGlobalData = Object.values(RestApiGlobalStatePathsKeys).some((key) => key.name === d.key);
    return {
      ...d,
      erGlobalData,
    };
  });

  return (
    <RestApiGlobalStateMock data={dataMedGlobalMarkør.filter((d) => d.erGlobalData)}>
      <AxiosMock data={dataMedGlobalMarkør}>
        {children}
      </AxiosMock>
    </RestApiGlobalStateMock>
  );
};

export default RestApiMock;
