import React, { FunctionComponent, useState, useMemo } from 'react';
import {
  injectIntl, WrappedComponentProps, FormattedMessage,
} from 'react-intl';

import { Form } from 'react-final-form';
import { Hovedknapp, Knapp } from 'nav-frontend-knapper';
import { Normaltekst, Element } from 'nav-frontend-typografi';

import { RestApiState, useRestApiRunner } from 'data/rest-api-hooks';
import { RestApiPathsKeys } from 'data/restApiPaths';
import { required } from 'utils/validation/validators';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { InputField } from 'form/FinalFields';
import { FlexContainer, FlexRow, FlexColumn } from 'sharedComponents/flexGrid';
import Saksbehandler from '../saksbehandlerTsType';

import styles from './leggTilSaksbehandlerForm.less';

const erSaksbehandlerLagtTilAllerede = (saksbehandler: Saksbehandler, avdelingensSaksbehandlere = []) => avdelingensSaksbehandlere instanceof Array
    && avdelingensSaksbehandlere.some((s) => saksbehandler && s.brukerIdent.toLowerCase() === saksbehandler.brukerIdent.toLowerCase());


interface OwnProps {
  valgtAvdelingEnhet: string;
  avdelingensSaksbehandlere: Saksbehandler[];
  hentAvdelingensSaksbehandlere: (params: {avdelingEnhet: string}) => void;
}

/**
 * LeggTilSaksbehandlerForm
 */
export const LeggTilSaksbehandlerForm: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  valgtAvdelingEnhet,
  avdelingensSaksbehandlere,
  hentAvdelingensSaksbehandlere,
}) => {
  const [leggerTilNySaksbehandler, setLeggetTilNySaksbehandler] = useState(false);

  const {
    data: saksbehandler, startRequest: finnSaksbehandler, state, resetRequestData: resetSaksbehandlerSok,
  } = useRestApiRunner<Saksbehandler>(RestApiPathsKeys.SAKSBEHANDLER_SOK);

  const { startRequest: leggTilSaksbehandler } = useRestApiRunner<Saksbehandler>(RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER);

  const erLagtTilAllerede = erSaksbehandlerLagtTilAllerede(saksbehandler, avdelingensSaksbehandlere);

  const leggTilSaksbehandlerFn = () => {
    if (saksbehandler) {
      setLeggetTilNySaksbehandler(true);
      leggTilSaksbehandler({
        brukerIdent: saksbehandler.brukerIdent,
        avdelingEnhet: valgtAvdelingEnhet,
      }).then(() => {
        resetSaksbehandlerSok();
        setLeggetTilNySaksbehandler(false);
        hentAvdelingensSaksbehandlere({ avdelingEnhet: valgtAvdelingEnhet });
      });
    }
  };

  const resetSaksbehandlerSokFn = (resetFormValues: () => void) => {
    resetSaksbehandlerSok();
    resetFormValues();
  };

  const formattedText = useMemo((): string => {
    if (state === RestApiState.SUCCESS && !saksbehandler) {
      return intl.formatMessage({ id: 'LeggTilSaksbehandlerForm.FinnesIkke' });
    }
    if (!saksbehandler) {
      return '';
    }

    const brukerinfo = `${saksbehandler.navn}, ${saksbehandler.avdelingsnavn.join(', ')}`;
    return erLagtTilAllerede
      ? `${brukerinfo} (${intl.formatMessage({ id: 'LeggTilSaksbehandlerForm.FinnesAllerede' })})`
      : brukerinfo;
  }, [state, saksbehandler, erLagtTilAllerede]);

  return (
    <Form
      onSubmit={(values: { brukerIdent: string}) => finnSaksbehandler({ brukerIdent: values.brukerIdent })}
      render={({
        submitting, handleSubmit, form,
      }) => (
        <form onSubmit={handleSubmit}>
          <Element>
            <FormattedMessage id="LeggTilSaksbehandlerForm.LeggTil" />
          </Element>
          <VerticalSpacer eightPx />
          <FlexContainer>
            <FlexRow>
              <FlexColumn>
                <InputField
                  name="brukerIdent"
                  label={intl.formatMessage({ id: 'LeggTilSaksbehandlerForm.Brukerident' })}
                  bredde="S"
                  validate={[required]}
                />
              </FlexColumn>
              <FlexColumn>
                <Knapp
                  mini
                  htmlType="submit"
                  className={styles.button}
                  spinner={submitting}
                  disabled={submitting || leggerTilNySaksbehandler}
                  tabIndex={0}
                >
                  <FormattedMessage id="LeggTilSaksbehandlerForm.Sok" />
                </Knapp>
              </FlexColumn>
            </FlexRow>
          </FlexContainer>
          {state === RestApiState.SUCCESS && (
          <>
            <Normaltekst>
              {formattedText}
            </Normaltekst>
            <VerticalSpacer sixteenPx />
            <FlexContainer>
              <FlexRow>
                <FlexColumn>
                  <Hovedknapp
                    mini
                    autoFocus
                    htmlType="button"
                    onClick={() => leggTilSaksbehandlerFn()}
                    spinner={leggerTilNySaksbehandler}
                    disabled={leggerTilNySaksbehandler || erLagtTilAllerede || !saksbehandler}
                  >
                    <FormattedMessage id="LeggTilSaksbehandlerForm.LeggTilIListen" />
                  </Hovedknapp>
                </FlexColumn>
                <FlexColumn>
                  <Knapp
                    mini
                    htmlType="button"
                    tabIndex={0}
                    disabled={leggerTilNySaksbehandler}
                    onClick={() => resetSaksbehandlerSokFn(form.reset)}
                  >
                    <FormattedMessage id="LeggTilSaksbehandlerForm.Nullstill" />
                  </Knapp>
                </FlexColumn>
              </FlexRow>
            </FlexContainer>
          </>
          )}
        </form>
      )}
    />
  );
};

export default injectIntl(LeggTilSaksbehandlerForm);
