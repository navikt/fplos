import EventType from '../eventType';
import ErrorType from './errorTsType';
import { isHandledError } from './ErrorTypes';
import TimeoutError from './TimeoutError';
import { ErrorResponse } from '../ResponseTsType';

type NotificationEmitter = (eventType: keyof typeof EventType, data?: any, isPollingRequest?: boolean) => void

const isString = (value: any): boolean => typeof value === 'string';

const isOfTypeBlob = (error: ErrorType): boolean => error && !!error.config && error.config.responseType === 'blob';

const blobParser = (blob: any): Promise<string> => {
  const fileReader = new FileReader();

  return new Promise((resolve, reject) => {
    fileReader.onerror = () => {
      fileReader.abort();
      reject(new Error('Problem parsing blob'));
    };

    fileReader.onload = () => {
      if (fileReader.result && !(fileReader.result instanceof ArrayBuffer)) {
        resolve(fileReader.result);
      } else {
        reject(new Error('Problem parsing blob'));
      }
    };

    if (blob instanceof Blob) {
      fileReader.readAsText(blob);
    }
  });
};

interface FormatedError {
  data?: string | ErrorResponse;
  type?: string;
  status?: number;
  isForbidden?: boolean;
  isUnauthorized?: boolean;
  is418?: boolean;
  isGatewayTimeoutOrNotFound?: boolean;
  location?: string;
}

class RequestErrorEventHandler {
  notify: NotificationEmitter;

  isPollingRequest: boolean;

  constructor(notificationEmitter: NotificationEmitter, isPollingRequest: boolean) {
    this.notify = notificationEmitter;
    this.isPollingRequest = isPollingRequest;
  }

  public handleError = async (error: ErrorType | TimeoutError): Promise<void> => {
    if (error instanceof TimeoutError) {
      this.notify(EventType.POLLING_TIMEOUT, { location: error.location });
      return Promise.resolve();
    }

    const formattedError = this.formatError(error);

    if (isOfTypeBlob(error)) {
      const jsonErrorString = await blobParser(formattedError.data);
      if (isString(jsonErrorString)) {
        formattedError.data = JSON.parse(jsonErrorString);
      }
    }

    if (formattedError.isGatewayTimeoutOrNotFound) {
      this.notify(EventType.REQUEST_GATEWAY_TIMEOUT_OR_NOT_FOUND, { location: formattedError.location }, this.isPollingRequest);
    } else if (formattedError.isUnauthorized) {
      this.notify(EventType.REQUEST_UNAUTHORIZED, { message: error.message }, this.isPollingRequest);
    } else if (formattedError.isForbidden) {
      this.notify(EventType.REQUEST_FORBIDDEN, formattedError.data ? formattedError.data : { message: error.message });
    } else if (formattedError.is418) {
      this.notify(EventType.POLLING_HALTED_OR_DELAYED, formattedError.data);
    } else if (!error.response && error.message) {
      this.notify(EventType.REQUEST_ERROR, { message: error.message }, this.isPollingRequest);
    } else if (!isHandledError(formattedError.type)) {
      this.notify(EventType.REQUEST_ERROR, formattedError.data ? this.getFormattedData(formattedError.data) : undefined, this.isPollingRequest);
    }
    return Promise.resolve();
  };

  // eslint-disable-next-line class-methods-use-this
  private getFormattedData = (
    data: string | Record<string, any>,
  ): string | Record<string, any> => (isString(data) ? { message: data } : data);

  // eslint-disable-next-line class-methods-use-this
  private findErrorData = (
    response: {data?: any; status?: number; statusText?: string},
  ): string | ErrorResponse => (response.data ? response.data : response.statusText);

  private formatError = (error: ErrorType): FormatedError => {
    const response = error && error.response ? error.response : undefined;
    return {
      data: response ? this.findErrorData(response) : undefined,
      type: response && response.data ? response.data.type : undefined,
      status: response ? response.status : undefined,
      isForbidden: response ? response.status === 403 : undefined,
      isUnauthorized: response ? response.status === 401 : undefined,
      is418: response ? response.status === 418 : undefined,
      isGatewayTimeoutOrNotFound: response ? response.status === 504 || response.status === 404 : undefined,
      location: response && response.config ? response.config.url : undefined,
    };
  };
}

export default RequestErrorEventHandler;
