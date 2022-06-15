import React, { FunctionComponent, useEffect, useState } from 'react';
import MockAdapter from 'axios-mock-adapter';
import { requestApi } from 'data/fplosRestApi';
import { LoadingPanel } from '@navikt/ft-ui-komponenter';

interface Props {
  children: any;
  data: {
    key: string;
    data: any,
  }[];
}

const AxiosMock: FunctionComponent<Props> = ({ children, data }) => {
  const [showChildren, setShowChildren] = useState(false);

  useEffect(() => {
    const apiMock = new MockAdapter(requestApi.getAxios());
    data.forEach((d) => {
      if (requestApi.getRestType(d.key) === 'GET') {
        apiMock.onGet(requestApi.getUrl(d.key)).reply(200, d.data);
      } else if (requestApi.getRestType(d.key) === 'GET_ASYNC') {
        apiMock.onGet(requestApi.getUrl(d.key)).replyOnce(200, d.data);
      } else {
        apiMock.onPost(requestApi.getUrl(d.key)).reply(200, d.data);
      }
    });

    setShowChildren(true);

    return () => {
      apiMock.reset();
    };
  }, []);
  return showChildren ? children : <LoadingPanel />;
};

export default AxiosMock;
