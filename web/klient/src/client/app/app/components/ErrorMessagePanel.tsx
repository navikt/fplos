import React, { FunctionComponent, useMemo } from 'react';
import { injectIntl, WrappedComponentProps, IntlShape } from 'react-intl';
import { Row, Column } from 'nav-frontend-grid';
import { Undertekst } from 'nav-frontend-typografi';
import advarselImageUrl from 'images/advarsel-sirkel-fyll.svg';
import Driftsmelding from 'app/driftsmeldingTsType';

import Lukknapp from 'nav-frontend-lukknapp';
import decodeHtmlEntity from 'utils/decodeHtmlEntityUtils';

import Image from 'sharedComponents/Image';
import EventType from 'data/rest-api/src/requestApi/eventType';
import styles from './errorMessagePanel.less';

export const getErrorMessageList = (intl: IntlShape, queryStrings: { errorcode?: string; errormessage?: string}, allErrorMessages = []): string[] => {
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
  removeErrorMessage: () => void;
  errorMessages?: {
    type: EventType;
    code?: string;
    params?: {
      errorDetails?: string;
      location?: string;
    };
    text?: string;
  }[];
  queryStrings: {
    errormessage?: string;
    errorcode?: string;
  };
  driftsmeldinger: Driftsmelding[];
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
  removeErrorMessage,
  driftsmeldinger,
}) => {
  const feilmeldinger = useMemo(() => getErrorMessageList(intl, queryStrings, errorMessages), [queryStrings, errorMessages]);

  if (feilmeldinger.length === 0 && driftsmeldinger.length === 0) {
    return null;
  }


  function LukkeKnapp() {
    if (feilmeldinger.length === 0) {
      return null;
    }
    return (
      <div className={styles.lukkContainer}>
        <Lukknapp hvit onClick={removeErrorMessage}>{intl.formatMessage({ id: 'ErrorMessagePanel.Close' })}</Lukknapp>
      </div>
    );
  }

  return (
    <div className={styles.container}>
      {driftsmeldinger.map((message) => (
        <Row key={message.id}>
          <Column xs="11" className={styles.column}>
            <Image
              className={styles.driftsInfo}
              src={advarselImageUrl}
            />
            <Undertekst className={styles.wordWrap}>
              {`${message.melding}`}
            </Undertekst>
          </Column>
        </Row>
      ))}
      {feilmeldinger.map((message) => (
        <Row key={message}>
          <Column xs="11">
            <Undertekst className={styles.wordWrap}>
              {`${decodeHtmlEntity(message)} `}
            </Undertekst>
          </Column>
        </Row>
      ))}
      <LukkeKnapp />
    </div>
  );
};

export default injectIntl(ErrorMessagePanel);
