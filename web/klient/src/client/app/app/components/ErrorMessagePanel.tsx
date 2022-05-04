import React, { FunctionComponent, useMemo } from 'react';
import { injectIntl, WrappedComponentProps, IntlShape } from 'react-intl';
import { Row, Column } from 'nav-frontend-grid';
import { Undertekst } from 'nav-frontend-typografi';

import Lukknapp from 'nav-frontend-lukknapp';
import { decodeHtmlEntity } from '@navikt/ft-utils';

import EventType from 'data/rest-api/src/requestApi/eventType';
import styles from './errorMessagePanel.less';

type ErrorMessage = {
  type?: EventType;
  code?: string;
  params?: {
    errorDetails?: string;
    location?: string;
    contextPath?: string;
    message?: string;
    date?: string;
    time?: string;
  };
  text?: string;
};

export const getErrorMessageList = (
  intl: IntlShape,
  queryStrings: { errorcode?: string; errormessage?: string},
  allErrorMessages: ErrorMessage[] = [],
): string[] => {
  const errorMessages = [];
  if (queryStrings.errorcode) {
    errorMessages.push(intl.formatMessage({ id: queryStrings.errorcode }));
  }
  if (queryStrings.errormessage) {
    errorMessages.push(queryStrings.errormessage);
  }
  allErrorMessages.forEach((message) => errorMessages.push(message.code ? intl.formatMessage({ id: message.code }, message.params) : message.text));
  return errorMessages;
};

interface OwnProps {
  removeErrorMessages: () => void;
  errorMessages?: ErrorMessage[];
  queryStrings: {
    errormessage?: string;
    errorcode?: string;
  };
}

/**
 * ErrorMessagePanel
 *
 * Presentasjonskomponent. Definerer hvordan feilmeldinger vises.
 */
const ErrorMessagePanel: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  errorMessages,
  queryStrings,
  removeErrorMessages,
}) => {
  const feilmeldinger = useMemo(() => getErrorMessageList(intl, queryStrings, errorMessages), [queryStrings, errorMessages]);

  if (feilmeldinger.length === 0) {
    return null;
  }

  return (
    <div className={styles.container}>
      {feilmeldinger.map((message) => (
        <Row key={message}>
          <Column xs="11">
            <Undertekst className={styles.wordWrap}>
              {`${decodeHtmlEntity(message)} `}
            </Undertekst>
          </Column>
        </Row>
      ))}
      <div className={styles.lukkContainer}>
        <Lukknapp hvit onClick={removeErrorMessages}>{intl.formatMessage({ id: 'ErrorMessagePanel.Close' })}</Lukknapp>
      </div>
    </div>
  );
};

export default injectIntl(ErrorMessagePanel);
