import React, { FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';
import { useForm } from 'react-hook-form';
import { Element } from 'nav-frontend-typografi';

import StoreValuesInLocalStorage from 'form/StoreValuesInLocalStorage';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import FagsakYtelseType from 'kodeverk/fagsakYtelseType';
import KodeverkType from 'kodeverk/kodeverkTyper';
import Kodeverk from 'types/kodeverkTsType';
import useKodeverk from 'data/useKodeverk';
import OppgaverForAvdeling from 'types/avdelingsleder/oppgaverForAvdelingTsType';
import FordelingAvBehandlingstypeGraf from './FordelingAvBehandlingstypeGraf';
import RadioGroupField from '../../../../formNew/RadioGroupField';
import RadioOption from '../../../../formNew/RadioOption';
import Form from '../../../../formNew/Form';

const finnFagsakYtelseTypeNavn = (fagsakYtelseTyper: Kodeverk[], valgtFagsakYtelseType: string) => {
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
  width: number;
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
  width,
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
      <Element>
        <FormattedMessage id="FordelingAvBehandlingstypePanel.Fordeling" />
      </Element>
      <VerticalSpacer sixteenPx />
      <RadioGroupField name="valgtYtelseType">
        <RadioOption
          value={FagsakYtelseType.FORELDREPRENGER}
          label={finnFagsakYtelseTypeNavn(fagsakYtelseTyper, FagsakYtelseType.FORELDREPRENGER)}
        />
        <RadioOption
          value={FagsakYtelseType.ENGANGSSTONAD}
          label={finnFagsakYtelseTypeNavn(fagsakYtelseTyper, FagsakYtelseType.ENGANGSSTONAD)}
        />
        <RadioOption
          value={FagsakYtelseType.SVANGERSKAPPENGER}
          label={finnFagsakYtelseTypeNavn(fagsakYtelseTyper, FagsakYtelseType.SVANGERSKAPPENGER)}
        />
        <RadioOption
          value={ALLE_YTELSETYPER_VALGT}
          label={<FormattedMessage id="FordelingAvBehandlingstypePanel.Alle" />}
        />
      </RadioGroupField>
      <FordelingAvBehandlingstypeGraf
        width={width}
        height={height}
        behandlingTyper={behandlingTyper}
        oppgaverForAvdeling={oppgaverForAvdeling ? oppgaverForAvdeling
          .filter((ofa) => (values.valgtYtelseType === ALLE_YTELSETYPER_VALGT ? true : values.valgtYtelseType === ofa.fagsakYtelseType.kode)) : []}
      />
    </Form>
  );
};

export default FordelingAvBehandlingstypePanel;
