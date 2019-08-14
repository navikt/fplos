import DefaultFormatter from './formatters/DefaultFormatter';
import RestTimeoutFormatter from './formatters/RestTimeoutFormatter';
import RestHaltedOrDelayedFormatter from './formatters/RestHaltedOrDelayedFormatter';

const defaultFormatter = new DefaultFormatter();
const formatters = [new RestTimeoutFormatter(), new RestHaltedOrDelayedFormatter(), defaultFormatter];

interface ErrorMessage {
  type: string;
}

class ErrorFormatter {
  format = (errorMessages: ErrorMessage[], crashMessage?: string) => {
    const allErrorMessages = [];
    if (crashMessage) {
      allErrorMessages.push(defaultFormatter.format(crashMessage));
    }

    if (errorMessages.length > 0) {
      errorMessages.map((e: any) => {
        const formatter = formatters.find(f => f.isOfType(e.type));
        return formatter ? formatter.format(e) : undefined;
      })
        .filter(e => e)
        .forEach(e => allErrorMessages.push(e));
    }

    return allErrorMessages;
  }
}

export default ErrorFormatter;
