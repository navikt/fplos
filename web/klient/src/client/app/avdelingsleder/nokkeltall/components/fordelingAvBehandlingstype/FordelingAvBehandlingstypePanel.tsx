
import React, { FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';

import { Form } from 'react-final-form';
import { Element } from 'nav-frontend-typografi';

import StoreValuesInLocalStorage from 'form/StoreValuesInLocalStorage';
import { getValueFromLocalStorage } from 'utils/localStorageHelper';
import { RadioGroupField, RadioOption } from 'form/FinalFields';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { useKodeverk } from 'data/rest-api-hooks';
import FordelingAvBehandlingstypeGraf from './FordelingAvBehandlingstypeGraf';
import OppgaverForAvdeling from './oppgaverForAvdelingTsType';

const finnFagsakYtelseTypeNavn = (fagsakYtelseTyper, valgtFagsakYtelseType) => {
  const type = fagsakYtelseTyper.find((fyt) => fyt.kode === valgtFagsakYtelseType);
  return type ? type.navn : '';
};

export const ALLE_YTELSETYPER_VALGT = 'ALLE';

interface InitialValues {
  valgtYtelseType: string;
}

interface OwnProps {
  width: number;
  height: number;
  oppgaverForAvdeling: OppgaverForAvdeling[];
  initialValues: InitialValues;
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
}) => {
  const fagsakYtelseTyper = useKodeverk(kodeverkTyper.FAGSAK_YTELSE_TYPE);
  const behandlingTyper = useKodeverk(kodeverkTyper.BEHANDLING_TYPE);
  const stringFromStorage = getValueFromLocalStorage(formName);
  const lagredeVerdier = stringFromStorage ? JSON.parse(stringFromStorage) : undefined;
  return (
    <Form
      onSubmit={() => undefined}
      initialValues={lagredeVerdier || formDefaultValues}
      render={({ values }) => (
        <div>
          <StoreValuesInLocalStorage stateKey={formName} values={values} />
          <Element>
            <FormattedMessage id="FordelingAvBehandlingstypePanel.Fordeling" />
          </Element>
          <VerticalSpacer sixteenPx />
          <RadioGroupField name="valgtYtelseType">
            <RadioOption
              value={fagsakYtelseType.FORELDREPRENGER}
              label={finnFagsakYtelseTypeNavn(fagsakYtelseTyper, fagsakYtelseType.FORELDREPRENGER)}
            />
            <RadioOption
              value={fagsakYtelseType.ENGANGSSTONAD}
              label={finnFagsakYtelseTypeNavn(fagsakYtelseTyper, fagsakYtelseType.ENGANGSSTONAD)}
            />
            <RadioOption
              value={fagsakYtelseType.SVANGERSKAPPENGER}
              label={finnFagsakYtelseTypeNavn(fagsakYtelseTyper, fagsakYtelseType.SVANGERSKAPPENGER)}
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
        </div>
      )}
    />
  );
};

export default FordelingAvBehandlingstypePanel;
