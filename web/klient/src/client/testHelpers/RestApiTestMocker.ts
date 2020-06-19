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

  mocks = [];

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

  mock = () => {
    const contextStubRestCallGlobalData = sinon.stub(useGlobalStateRestApi, 'default');
    this.mocks.push(contextStubRestCallGlobalData);
    this.globalRestCallData.forEach((data) => {
      contextStubRestCallGlobalData.withArgs(data.key).callsFake(() => ({ state: RestApiState.SUCCESS, data: data.data }));
    });
    const contextStubGlobalData = sinon.stub(useGlobalStateRestApiData, 'default');
    this.mocks.push(contextStubGlobalData);
    this.globalData.forEach((data) => {
      contextStubGlobalData.withArgs(data.key).callsFake(() => data.data);
    });
    const contextStubKodeverk = sinon.stub(useKodeverk, 'default');
    this.mocks.push(contextStubKodeverk);
    this.kodeverk.forEach((data) => {
      contextStubKodeverk.withArgs(data.key).callsFake(() => data.data);
    });
    const contextStub = sinon.stub(useRestApi, 'default');
    this.mocks.push(contextStub);
    this.restCallData.forEach((data) => {
      contextStub.withArgs(data.key).callsFake(() => ({ state: RestApiState.SUCCESS, data: data.data }));
    });

    let contextStubRunner;
    if (this.useRunner) {
      contextStubRunner = sinon.stub(useRestApiRunner, 'default').callsFake(() => ({
        state: RestApiState.NOT_STARTED,
        cancelRequest: () => undefined,
        startRequest: () => undefined,
        resetRequestData: () => undefined,
      }));
      this.mocks.push(contextStubRunner);
    }
    if (this.runnerData.length > 0) {
      contextStubRunner = sinon.stub(useRestApiRunner, 'default');
      this.mocks.push(contextStubRunner);
      this.runnerData.forEach((data) => {
        contextStubRunner.withArgs(data.key).callsFake(() => data.data);
      });
    }
  }

  resetMock = () => {
    this.mocks.forEach((mock) => {
      mock.restore();
    });
  }

  public runTest = (test) => {
    this.mock();
    try {
      test();
    } finally {
      this.resetMock();
    }
  }
}

export default RestApiTestMocker;
