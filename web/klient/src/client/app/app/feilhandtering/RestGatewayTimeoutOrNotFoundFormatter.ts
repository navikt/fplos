import ErrorEventType from './errorEventType';
import ErrorMessage from './ErrorMessage';

const TIMEOUT_MESSAGE_CODE = 'Rest.ErrorMessage.GatewayTimeoutOrNotFound';

const findContextPath = (location: string): string => location.split('/')[1].toUpperCase();

export type ErrorData = {
  type: string;
  message: string;
  location: string;
}

class RestGatewayTimeoutOrNotFoundFormatter {
  type = ErrorEventType.REQUEST_GATEWAY_TIMEOUT_OR_NOT_FOUND;

  isOfType = (type: string) => type === this.type;

  // eslint-disable-next-line class-methods-use-this
  format = (errorData: ErrorData) => ErrorMessage.withMessageCode(TIMEOUT_MESSAGE_CODE, {
    contextPath: errorData.location ? findContextPath(errorData.location) : '',
    location: errorData.location,
  });
}

export default RestGatewayTimeoutOrNotFoundFormatter;
