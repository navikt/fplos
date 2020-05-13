class ErrorMessage {
  text: string

  code: string

  params: any

  static withMessage(message: string, type) {
    const errorMessage = new ErrorMessage();
    errorMessage.text = message;
    if (type !== undefined) {
      errorMessage.type = type;
    }
    return errorMessage;
  }

  static withMessageCode(messageCode: string, params: any, type) {
    const errorMessage = new ErrorMessage();
    errorMessage.code = messageCode;
    errorMessage.params = params;
    if (type !== undefined) {
      errorMessage.type = type;
    }
    return errorMessage;
  }
}

export default ErrorMessage;
