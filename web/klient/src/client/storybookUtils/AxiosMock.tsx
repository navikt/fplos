import { FunctionComponent, useEffect } from 'react';
import MockAdapter from 'axios-mock-adapter';
import { requestApi } from 'data/fplosRestApi';

interface Props {
    children: any;
    data: {
      key: string;
      data: any,
    }[];
}

const apiMock = new MockAdapter(requestApi.getAxios());

const AxiosMock: FunctionComponent<Props> = ({ children, data }) => {
  useEffect(() => {
    data.forEach((d) => {
      if (requestApi.getRestType(d.key) === 'GET') {
        apiMock.onGet(requestApi.getUrl(d.key)).reply(200, d.data);
      } else if (requestApi.getRestType(d.key) === 'GET_ASYNC') {
        apiMock.onGet(requestApi.getUrl(d.key)).replyOnce(200, d.data);
      } else {
        apiMock.onPost(requestApi.getUrl(d.key)).reply(200, d.data);
      }
    });

    return () => {
      apiMock.reset();
    };
  });
  return children;
};

export default AxiosMock;
