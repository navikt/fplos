class RequestMock {
  keys = []

  result = []

  public withKeyAndResult = (key: string, result: any) => {
    this.keys.push(key);
    this.result.push(result);
    return this;
  }

  public build = () => ({
    startRequest: (key: string) => {
      const index = this.keys.findIndex((keyValue) => keyValue === key);
      const result = this.result[index];
      return Promise.resolve({ payload: result });
    },
    cancelRequest: () => undefined,
  })
}

export default RequestMock;
