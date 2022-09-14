import React, { FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';
import { useForm } from 'react-hook-form';
import { Label } from '@navikt/ds-react';

import { VerticalSpacer } from '@navikt/ft-ui-komponenter';
import FagsakYtelseType from 'kodeverk/fagsakYtelseType';
import KodeverkType from 'kodeverk/kodeverkTyper';
import useKodeverk from 'data/useKodeverk';
import StoreValuesInLocalStorage from 'data/StoreValuesInLocalStorage';
import { Form, RadioGroupPanel } from '@navikt/ft-form-hooks';
import OppgaverForAvdeling from 'types/avdelingsleder/oppgaverForAvdelingTsType';
import { KodeverkMedNavn } from '@navikt/ft-types';
import FordelingAvBehandlingstypeGraf from './FordelingAvBehandlingstypeGraf';

const finnFagsakYtelseTypeNavn = (fagsakYtelseTyper: KodeverkMedNavn[], valgtFagsakYtelseType: string) => {
  const type = fagsakYtelseTyper.find((fyt) => fyt.kode === valgtFagsakYtelseType);
  return type ? type.navn : '';
};

export const ALLE_YTELSETYPER_VALGT = 'ALLE';

interface InitialValues {
  valgtYtelseType: string;
}

type FormValues = {
  valgtYtelseType: string;
}

interface OwnProps {
  height: number;
  oppgaverForAvdeling: OppgaverForAvdeling[];
  getValueFromLocalStorage: (key: string) => string| undefined;
}

const formName = 'fordelingAvBehandlingstype';
const formDefaultValues: InitialValues = { valgtYtelseType: ALLE_YTELSETYPER_VALGT };

/**
 * FordelingAvBehandlingstypePanel.
 */
export const FordelingAvBehandlingstypePanel: FunctionComponent<OwnProps> = ({
  height,
  oppgaverForAvdeling,
  getValueFromLocalStorage,
}) => {
  const fagsakYtelseTyper = useKodeverk(KodeverkType.FAGSAK_YTELSE_TYPE);
  const behandlingTyper = useKodeverk(KodeverkType.BEHANDLING_TYPE);
  const stringFromStorage = getValueFromLocalStorage(formName);
  const lagredeVerdier = stringFromStorage ? JSON.parse(stringFromStorage) : undefined;

  const formMethods = useForm<FormValues>({
    defaultValues: lagredeVerdier || formDefaultValues,
  });

  const values = formMethods.watch();

  return (
    <Form<FormValues> formMethods={formMethods}>
      <StoreValuesInLocalStorage stateKey={formName} values={values} />
      <Label size="small">
        <FormattedMessage id="FordelingAvBehandlingstypePanel.Fordeling" />
      </Label>
      <VerticalSpacer sixteenPx />
      <RadioGroupPanel
        name="valgtYtelseType"
        isHorizontal
        radios={[{
          value: FagsakYtelseType.FORELDREPRENGER,
          label: finnFagsakYtelseTypeNavn(fagsakYtelseTyper, FagsakYtelseType.FORELDREPRENGER),
        }, {
          value: FagsakYtelseType.ENGANGSSTONAD,
          label: finnFagsakYtelseTypeNavn(fagsakYtelseTyper, FagsakYtelseType.ENGANGSSTONAD),
        }, {
          value: FagsakYtelseType.SVANGERSKAPPENGER,
          label: finnFagsakYtelseTypeNavn(fagsakYtelseTyper, FagsakYtelseType.SVANGERSKAPPENGER),
        }, {
          value: ALLE_YTELSETYPER_VALGT,
          label: <FormattedMessage id="FordelingAvBehandlingstypePanel.Alle" />,
        }]}
      />
      <VerticalSpacer sixteenPx />
      <FordelingAvBehandlingstypeGraf
        height={height}
        behandlingTyper={behandlingTyper}
        oppgaverForAvdeling={oppgaverForAvdeling ? oppgaverForAvdeling
          .filter((ofa) => (values.valgtYtelseType === ALLE_YTELSETYPER_VALGT ? true : values.valgtYtelseType === ofa.fagsakYtelseType)) : []}
      />
    </Form>
  );
};

export default FordelingAvBehandlingstypePanel;
