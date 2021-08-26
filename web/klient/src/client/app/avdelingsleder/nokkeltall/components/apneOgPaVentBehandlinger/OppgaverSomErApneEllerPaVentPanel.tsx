import React, { FunctionComponent, useMemo } from 'react';
import { FormattedMessage } from 'react-intl';
import { useForm } from 'react-hook-form';
import { Element } from 'nav-frontend-typografi';

import { FlexColumn, FlexContainer, FlexRow } from 'sharedComponents/flexGrid';
import StoreValuesInLocalStorage from 'form/StoreValuesInLocalStorage';
import KodeverkType from 'kodeverk/kodeverkTyper';
import useKodeverk from 'data/useKodeverk';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import BehandlingType from 'kodeverk/behandlingType';
import OppgaverSomErApneEllerPaVent from 'types/avdelingsleder/oppgaverSomErApneEllerPaVentTsType';
import OppgaverSomErApneEllerPaVentGraf from './OppgaverSomErApneEllerPaVentGraf';
import CheckboxField from '../../../../formNew/CheckboxField';
import Form from '../../../../formNew/Form';

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
  const behandlingTyper = useKodeverk(KodeverkType.BEHANDLING_TYPE);
  const stringFromStorage = getValueFromLocalStorage(formName);
  const lagredeVerdier = stringFromStorage ? JSON.parse(stringFromStorage) : undefined;

  const filtrerteBehandlingstyper = useMemo(() => behandlingTyper
    .filter((type) => type.kode !== BehandlingType.TILBAKEBETALING
    && type.kode !== BehandlingType.TILBAKEBETALING_REVURDERING
    && type.kode !== BehandlingType.SOKNAD), []);

  const formDefaultValues = useMemo(() => Object.values(filtrerteBehandlingstyper).reduce((app, type) => ({
    ...app,
    [type.kode]: true,
  }), {}), []);

  const formMethods = useForm({
    defaultValues: lagredeVerdier || formDefaultValues,
  });

  const values = formMethods.watch();

  return (
    <Form formMethods={formMethods}>
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
    </Form>
  );
};

export default OppgaverSomErApneEllerPaVentPanel;
