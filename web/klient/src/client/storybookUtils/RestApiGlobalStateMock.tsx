import {
  FunctionComponent, useContext, useEffect, useState,
} from 'react';
import { RestApiDispatchContext } from 'data/rest-api-hooks/src/global-data/RestApiContext';

interface Props {
    children: any;
    data: {
      key: string;
      data: any,
    }[];
}

const RestApiGlobalStateMock: FunctionComponent<Props> = ({ children, data }) => {
  const [erFerdig, setFerdig] = useState(false);
  const dispatch = useContext(RestApiDispatchContext);

  useEffect(() => {
    if (dispatch && !erFerdig) {
      const dispatchData = data.map((d) => () => dispatch({ type: 'success', key: d.key, data: d.data }));
      Promise.all(dispatchData.map((d) => d())).then(() => setFerdig(true));
    }
  }, [dispatch, data, erFerdig]);

  return erFerdig ? children : null;
};

export default RestApiGlobalStateMock;
