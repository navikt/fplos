class RequestMock {
  keys = []

  result = []

  public withKeyAndResult = (key: string, result: any) => {
    this.keys.push(key);
    this.result.push(result);
    return this;
  }

  public build = () => ({
    getRequestRunner: (key: string) => {
      const index = this.keys.findIndex((keyValue) => keyValue === key);
      const result = this.result[index];
      return {
        startProcess: () => Promise.resolve({ payload: result }),
      };
    },
  })
}

export default RequestMock;
