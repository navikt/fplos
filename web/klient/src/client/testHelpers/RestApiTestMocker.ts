import sinon from 'sinon';

import { RestApiPathsKeys, RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import * as useRestApi from 'data/rest-api-hooks/src/local-data/useRestApi';
import * as useGlobalStateRestApi from 'data/rest-api-hooks/src/global-data/useGlobalStateRestApi';
import * as useGlobalStateRestApiData from 'data/rest-api-hooks/src/global-data/useGlobalStateRestApiData';
import * as useRestApiRunner from 'data/rest-api-hooks/src/local-data/useRestApiRunner';
import * as useKodeverk from 'data/rest-api-hooks/src/global-data/useKodeverk';
import { RestApiState } from 'data/rest-api-hooks';

class RestApiTestMocker {
  globalRestCallData =[];

  globalData = [];

  kodeverk =[];

  restCallData = [];

  useRunner = false;

  runnerData = [];

  public withGlobalRestCall = (key: RestApiGlobalStatePathsKeys, data) => {
    this.globalRestCallData.push({
      key,
      data,
    });
    return this;
  }

  public withGlobalData = (key: RestApiGlobalStatePathsKeys, data) => {
    this.globalData.push({
      key,
      data,
    });
    return this;
  }

  public withKodeverk = (key: string, data) => {
    this.kodeverk.push({
      key,
      data,
    });
    return this;
  }

  public withRestCall = (key: RestApiPathsKeys, data) => {
    this.restCallData.push({
      key,
      data,
    });
    return this;
  }

  public withDummyRunner = () => {
    this.useRunner = true;
    return this;
  }

  public withRestCallRunner = (
    key: RestApiPathsKeys,
    data: { state?: RestApiState, data?: any, startRequest?: (any) => any, resetRequestData?: () => void}
  ) => {
    this.runnerData.push({
      key,
      data,
    });
    return this;
  }

  public runTest = (test) => {
    const contextStubRestCallGlobalData = sinon.stub(useGlobalStateRestApi, 'default');
    this.globalRestCallData.forEach((data) => {
      contextStubRestCallGlobalData.withArgs(data.key).callsFake(() => ({ state: RestApiState.SUCCESS, data: data.data }));
    });
    const contextStubGlobalData = sinon.stub(useGlobalStateRestApiData, 'default');
    this.globalData.forEach((data) => {
      contextStubGlobalData.withArgs(data.key).callsFake(() => data.data);
    });
    const contextStubKodeverk = sinon.stub(useKodeverk, 'default');
    this.kodeverk.forEach((data) => {
      contextStubKodeverk.withArgs(data.key).callsFake(() => data.data);
    });
    const contextStub = sinon.stub(useRestApi, 'default');
    this.restCallData.forEach((data) => {
      contextStub.withArgs(data.key).callsFake(() => ({ state: RestApiState.SUCCESS, data: data.data }));
    });

    let contextStubRunner;
    if (this.useRunner) {
      contextStubRunner = sinon.stub(useRestApiRunner, 'default').callsFake(() => ({}));
    }
    if (this.runnerData.length > 0) {
      contextStubRunner = sinon.stub(useRestApiRunner, 'default');
      this.runnerData.forEach((data) => {
        contextStubRunner.withArgs(data.key).callsFake(() => data.data);
      });
    }

    try {
      test();
    } finally {
      contextStubRestCallGlobalData.restore();
      contextStubGlobalData.restore();
      contextStubKodeverk.restore();
      contextStub.restore();
      if (this.useRunner || this.runnerData.length > 0) {
        contextStubRunner.restore();
      }
    }
  }
}

export default RestApiTestMocker;
