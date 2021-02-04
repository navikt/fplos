import React, { FunctionComponent, useMemo } from 'react';
import { FormattedMessage } from 'react-intl';
import { Element } from 'nav-frontend-typografi';
import { Form } from 'react-final-form';

import { FlexColumn, FlexContainer, FlexRow } from 'sharedComponents/flexGrid';
import StoreValuesInLocalStorage from 'form/StoreValuesInLocalStorage';
import { CheckboxField } from 'form/FinalFields';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import useKodeverk from 'data/useKodeverk';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import behandlingType from 'kodeverk/behandlingType';
import OppgaverSomErApneEllerPaVent from './oppgaverSomErApneEllerPaVentTsType';
import OppgaverSomErApneEllerPaVentGraf from './OppgaverSomErApneEllerPaVentGraf';

const formName = 'oppgaverSomErApneEllerPaVent';

interface OwnProps {
  width: number;
  height: number;
  oppgaverApneEllerPaVent: OppgaverSomErApneEllerPaVent[];
  getValueFromLocalStorage: (key: string) => string | undefined;
}

/**
 * OppgaverSomErApneEllerPaVentPanel.
 */
export const OppgaverSomErApneEllerPaVentPanel: FunctionComponent<OwnProps> = ({
  width,
  height,
  oppgaverApneEllerPaVent,
  getValueFromLocalStorage,
}) => {
  const behandlingTyper = useKodeverk(kodeverkTyper.BEHANDLING_TYPE);
  const stringFromStorage = getValueFromLocalStorage(formName);
  const lagredeVerdier = stringFromStorage ? JSON.parse(stringFromStorage) : undefined;

  const filtrerteBehandlingstyper = useMemo(() => behandlingTyper
    .filter((type) => type.kode !== behandlingType.TILBAKEBETALING && type.kode !== behandlingType.TILBAKEBETALING_REVURDERING), []);

  const formDefaultValues = useMemo(() => Object.values(filtrerteBehandlingstyper).reduce((app, type) => ({
    ...app,
    [type.kode]: true,
  }), {}), []);

  return (
    <Form
      onSubmit={() => undefined}
      initialValues={lagredeVerdier || formDefaultValues}
      render={({ values }) => (
        <div>
          <StoreValuesInLocalStorage stateKey={formName} values={values} />
          <Element>
            <FormattedMessage id="OppgaverSomErApneEllerPaVentPanel.Apne" />
          </Element>
          <VerticalSpacer sixteenPx />
          <FlexContainer>
            <FlexRow>
              {filtrerteBehandlingstyper.map((type) => (
                <FlexColumn key={type.kode}>
                  <CheckboxField
                    name={type.kode}
                    label={type.navn}
                  />
                </FlexColumn>
              ))}
            </FlexRow>
          </FlexContainer>
          <VerticalSpacer sixteenPx />
          <OppgaverSomErApneEllerPaVentGraf
            width={width}
            height={height}
            oppgaverApneEllerPaVent={oppgaverApneEllerPaVent.filter((oav) => values[oav.behandlingType.kode])}
          />
        </div>
      )}
    />
  );
};

export default OppgaverSomErApneEllerPaVentPanel;
