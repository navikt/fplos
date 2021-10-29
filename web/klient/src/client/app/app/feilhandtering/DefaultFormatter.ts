import ErrorMessage from './ErrorMessage';
import Formatter from './Formatter';

interface ErrorData {
   feilmelding?: string;
   message?: string;
   type?: any;
}

class DefaultFormatter implements Formatter<ErrorData | string> {
  // eslint-disable-next-line class-methods-use-this
  isOfType = () => true;

  // eslint-disable-next-line class-methods-use-this
  format = (errorData: ErrorData | string): ErrorMessage | undefined => {
    if (typeof errorData === 'string') {
      return ErrorMessage.withMessage(errorData);
    }

    if (errorData.feilmelding) {
      return ErrorMessage.withMessage(errorData.feilmelding, errorData.type);
    }
    if (errorData.message) {
      return ErrorMessage.withMessage(errorData.message, errorData.type);
    }
    return undefined;
  };
}

export default DefaultFormatter;
