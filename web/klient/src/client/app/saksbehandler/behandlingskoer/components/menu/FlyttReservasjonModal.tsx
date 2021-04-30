import React, { FunctionComponent, useEffect, useCallback } from 'react';
import {
  injectIntl, WrappedComponentProps, FormattedMessage, IntlShape,
} from 'react-intl';
import { Form } from 'react-final-form';
import { Hovedknapp, Knapp } from 'nav-frontend-knapper';
import { Normaltekst, Element } from 'nav-frontend-typografi';

import { RestApiState } from 'data/rest-api-hooks';
import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { FlexContainer, FlexRow, FlexColumn } from 'sharedComponents/flexGrid';
import {
  hasValidText, maxLength, minLength, required,
} from 'utils/validation/validators';
import { TextAreaField, InputField } from 'form/FinalFields';
import Modal from 'sharedComponents/Modal';
import SaksbehandlerForFlytting from 'types/saksbehandler/saksbehandlerForFlyttingTsType';

import styles from './flyttReservasjonModal.less';

const minLength3 = minLength(3);
const maxLength500 = maxLength(500);
const minLength7 = minLength(7);
const maxLength7 = maxLength(7);

const formatText = (state: RestApiState, intl: IntlShape, saksbehandler?: SaksbehandlerForFlytting): string => {
  if (state === RestApiState.SUCCESS && !saksbehandler) {
    return intl.formatMessage({ id: 'LeggTilSaksbehandlerForm.FinnesIkke' });
  }

  return saksbehandler
    ? `${saksbehandler.navn}, ${saksbehandler.avdelingsnavn.join(', ')}`
    : '';
};

interface OwnProps {
  showModal: boolean;
  oppgaveId: number;
  closeModal: () => void;
  toggleMenu: () => void;
  hentReserverteOppgaver: (params: any, keepData: boolean) => void;
}

/**
 * FlyttReservasjonModal
 *
 * Presentasjonskomponent. Modal som lar en søke opp en saksbehandler som saken skal flyttes til. En kan også begrunne hvorfor saken skal flyttes.
 */
export const FlyttReservasjonModal: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  showModal,
  closeModal,
  oppgaveId,
  toggleMenu,
  hentReserverteOppgaver,
}) => {
  const {
    startRequest, state, data: saksbehandler, resetRequestData,
  } = restApiHooks.useRestApiRunner(RestApiPathsKeys.FLYTT_RESERVASJON_SAKSBEHANDLER_SOK);
  const finnSaksbehandler = useCallback((brukerIdent: string) => startRequest(brukerIdent), []);

  const { startRequest: flyttOppgavereservasjon } = restApiHooks.useRestApiRunner(RestApiPathsKeys.FLYTT_RESERVASJON);
  const flyttReservasjon = useCallback((brukerident: string, begrunnelse: string) => flyttOppgavereservasjon({
    oppgaveId, brukerIdent: brukerident, begrunnelse,
  }).then(() => hentReserverteOppgaver({}, true)),
  []);

  useEffect(() => () => {
    resetRequestData();
  }, []);

  return (
    <Modal
      className={styles.modal}
      isOpen={showModal}
      closeButton={false}
      contentLabel={intl.formatMessage({ id: 'FlyttReservasjonModal.FlyttReservasjon' })}
      onRequestClose={closeModal}
    >
      <Form
        onSubmit={(values) => finnSaksbehandler(values.brukerIdent)}
        render={({
          handleSubmit, values,
        }) => (
          <form onSubmit={handleSubmit}>
            <Element>
              <FormattedMessage id="FlyttReservasjonModal.FlyttReservasjon" />
            </Element>
            <VerticalSpacer eightPx />
            <FlexContainer>
              <FlexRow>
                <FlexColumn>
                  <InputField
                    name="brukerIdent"
                    label={intl.formatMessage({ id: 'FlyttReservasjonModal.Brukerident' })}
                    bredde="S"
                    validate={[required, minLength7, maxLength7]}
                    autoFocus
                  />
                </FlexColumn>
                <FlexColumn>
                  <Hovedknapp
                    mini
                    htmlType="submit"
                    className={styles.button}
                    spinner={state === RestApiState.LOADING}
                    disabled={!values.brukerIdent || state === RestApiState.LOADING}
                  >
                    <FormattedMessage id="FlyttReservasjonModal.Sok" />
                  </Hovedknapp>
                </FlexColumn>
              </FlexRow>
            </FlexContainer>
            {state === RestApiState.SUCCESS && (
              <>
                <Normaltekst>{formatText(state, intl, saksbehandler)}</Normaltekst>
                <VerticalSpacer sixteenPx />
              </>
            )}
          </form>
        )}
      />
      <VerticalSpacer sixteenPx />
      <Form
        onSubmit={(values) => {
          toggleMenu();
          flyttReservasjon(saksbehandler ? saksbehandler.brukerIdent : '', values.begrunnelse);
        }}
        render={({
          handleSubmit, values,
        }) => (
          <form onSubmit={handleSubmit}>
            <TextAreaField
              name="begrunnelse"
              label={intl.formatMessage({ id: 'FlyttReservasjonModal.Begrunn' })}
              validate={[required, maxLength500, minLength3, hasValidText]}
              maxLength={500}
            />
            <Hovedknapp
              className={styles.submitButton}
              mini
              htmlType="submit"
              disabled={!saksbehandler || (!values.begrunnelse || values.begrunnelse.length < 3)}
            >
              {intl.formatMessage({ id: 'FlyttReservasjonModal.Ok' })}
            </Hovedknapp>
            <Knapp
              className={styles.cancelButton}
              mini
              htmlType="reset"
              onClick={closeModal}
            >
              {intl.formatMessage({ id: 'FlyttReservasjonModal.Avbryt' })}
            </Knapp>
          </form>
        )}
      />
    </Modal>
  );
};

export default injectIntl(FlyttReservasjonModal);
