type Error = Readonly<{
  response?: {
    data: {
      type?: string;
    };
    status?: string;
  };
  type?: string;
}>;

export default Error;
