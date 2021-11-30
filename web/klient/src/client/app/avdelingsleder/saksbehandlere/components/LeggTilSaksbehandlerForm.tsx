import React, { FunctionComponent, useState, useMemo } from 'react';
import {
  injectIntl, WrappedComponentProps, FormattedMessage,
} from 'react-intl';
import { useForm } from 'react-hook-form';

import { Hovedknapp, Knapp } from 'nav-frontend-knapper';
import { Normaltekst, Element } from 'nav-frontend-typografi';

import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import { RestApiState } from 'data/rest-api-hooks';
import { required } from 'utils/validation/validators';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { FlexContainer, FlexRow, FlexColumn } from 'sharedComponents/flexGrid';
import Saksbehandler from 'types/avdelingsleder/saksbehandlerAvdelingTsType';
import { Form, InputField } from 'form/formIndex';

import styles from './leggTilSaksbehandlerForm.less';

const erSaksbehandlerLagtTilAllerede = (
  saksbehandler?: Saksbehandler,
  avdelingensSaksbehandlere: Saksbehandler[] = [],
) => avdelingensSaksbehandlere instanceof Array
    && avdelingensSaksbehandlere.some((s) => saksbehandler && s.brukerIdent.toLowerCase() === saksbehandler.brukerIdent.toLowerCase());

interface OwnProps {
  valgtAvdelingEnhet: string;
  avdelingensSaksbehandlere: Saksbehandler[];
  hentAvdelingensSaksbehandlere: (params: {avdelingEnhet: string}) => void;
}

type FormValues = {
  brukerIdent: string;
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
  } = restApiHooks.useRestApiRunner(RestApiPathsKeys.SAKSBEHANDLER_SOK);

  const { startRequest: leggTilSaksbehandler } = restApiHooks.useRestApiRunner(RestApiPathsKeys.OPPRETT_NY_SAKSBEHANDLER);

  const erLagtTilAllerede = erSaksbehandlerLagtTilAllerede(saksbehandler, avdelingensSaksbehandlere);

  const leggTilSaksbehandlerFn = (resetFormValues: () => void) => {
    if (saksbehandler) {
      setLeggetTilNySaksbehandler(true);
      leggTilSaksbehandler({
        brukerIdent: saksbehandler.brukerIdent,
        avdelingEnhet: valgtAvdelingEnhet,
      }).then(() => {
        resetSaksbehandlerSok();
        resetFormValues();
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

  const formMethods = useForm<FormValues>();

  return (
    <Form<FormValues> formMethods={formMethods} onSubmit={(values: { brukerIdent: string}) => finnSaksbehandler({ brukerIdent: values.brukerIdent })}>
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
              validate={[required(intl)]}
            />
          </FlexColumn>
          <FlexColumn>
            <Knapp
              mini
              htmlType="submit"
              className={styles.button}
              spinner={formMethods.formState.isSubmitting}
              disabled={formMethods.formState.isSubmitting || leggerTilNySaksbehandler}
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
                onClick={() => leggTilSaksbehandlerFn(formMethods.reset)}
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
                onClick={() => resetSaksbehandlerSokFn(formMethods.reset)}
              >
                <FormattedMessage id="LeggTilSaksbehandlerForm.Nullstill" />
              </Knapp>
            </FlexColumn>
          </FlexRow>
        </FlexContainer>
      </>
      )}
    </Form>
  );
};

export default injectIntl(LeggTilSaksbehandlerForm);
