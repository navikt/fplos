import React, { FunctionComponent } from 'react';
import { connect } from 'react-redux';
import { injectIntl, WrappedComponentProps } from 'react-intl';
import { createSelector } from 'reselect';
import { Row, Column } from 'nav-frontend-grid';
import { Undertekst } from 'nav-frontend-typografi';
import Lukknapp from 'nav-frontend-lukknapp';

import decodeHtmlEntity from 'utils/decodeHtmlEntityUtils';
import errorHandler from 'data/error-api-redux';

import styles from './errorMessagePanel.less';

interface OwnProps {
  errorMessages: string[];
  removeErrorMessage: () => void;
}

/**
 * ErrorMessagePanel
 *
 * Presentasjonskomponent. Definerer hvordan feilmeldinger vises.
 */
export const ErrorMessagePanel: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  errorMessages,
  removeErrorMessage,
}) => {
  if (errorMessages.length === 0) {
    return null;
  }

  return (
    <div className={styles.container}>
      {errorMessages.map((message) => (
        <Row key={message}>
          <Column xs="11">
            <Undertekst className={styles.wordWrap}>
              {`${decodeHtmlEntity(message)} `}
            </Undertekst>
          </Column>
        </Row>
      ))}
      <div className={styles.lukkContainer}>
        <Lukknapp hvit onClick={removeErrorMessage}>{intl.formatMessage({ id: 'ErrorMessagePanel.Close' })}</Lukknapp>
      </div>
    </div>
  );
};


export const getErrorMessageList = createSelector([(state, ownProps) => ownProps,
  errorHandler.getAllErrorMessages], (ownProps, allErrorMessages = []) => {
  const { queryStrings, intl } = ownProps;
  const errorMessages = [];
  if (queryStrings.errorcode) {
    errorMessages.push(intl.formatMessage({ id: queryStrings.errorcode }));
  }
  if (queryStrings.errormessage) {
    errorMessages.push(queryStrings.errormessage);
  }
  allErrorMessages.forEach((message) => errorMessages.push(message.code ? intl.formatMessage({ id: message.code }, message.params) : message.text));
  return errorMessages;
});

const mapStateToProps = (state, ownProps) => ({
  errorMessages: getErrorMessageList(state, ownProps),
});

export default connect(mapStateToProps)(injectIntl(ErrorMessagePanel));
