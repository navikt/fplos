
import React from 'react';
import { connect } from 'react-redux';
import { FormattedMessage } from 'react-intl';

import { Form } from 'react-final-form';
import { Element } from 'nav-frontend-typografi';

import StoreValuesInReduxState from 'form/reduxBinding/StoreValuesInReduxState';
import { getValuesFromReduxState } from 'form/reduxBinding/formDuck';
import { RadioGroupField, RadioOption } from 'form/FinalFields';
import Kodeverk from 'kodeverk/kodeverkTsType';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { getKodeverk } from 'kodeverk/duck';
import FordelingAvBehandlingstypeGraf from './FordelingAvBehandlingstypeGraf';
import { getOppgaverForAvdeling } from '../../duck';
import OppgaverForAvdeling from './oppgaverForAvdelingTsType';

const finnFagsakYtelseTypeNavn = (fagsakYtelseTyper, valgtFagsakYtelseType) => {
  const type = fagsakYtelseTyper.find((fyt) => fyt.kode === valgtFagsakYtelseType);
  return type ? type.navn : '';
};

const ALLE_YTELSETYPER_VALGT = 'ALLE';

interface InitialValues {
  valgtYtelseType: string;
}

interface OwnProps {
  width: number;
  height: number;
  fagsakYtelseTyper: Kodeverk[];
  oppgaverForAvdeling?: OppgaverForAvdeling[];
  initialValues: InitialValues;
}

const formName = 'fordelingAvBehandlingstype';

/**
 * FordelingAvBehandlingstypePanel.
 */
export const FordelingAvBehandlingstypePanel = ({
  width,
  height,
  fagsakYtelseTyper,
  oppgaverForAvdeling,
  initialValues,
}: OwnProps) => (
  <Form
    onSubmit={() => undefined}
    initialValues={initialValues}
    render={({ values }) => (
      <div>
        <StoreValuesInReduxState onUmount stateKey={formName} values={values} />
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
          oppgaverForAvdeling={oppgaverForAvdeling ? oppgaverForAvdeling
            .filter((ofa) => (values.valgtYtelseType === ALLE_YTELSETYPER_VALGT ? true : values.valgtYtelseType === ofa.fagsakYtelseType.kode)) : []}
        />
      </div>
    )}
  />
);

FordelingAvBehandlingstypePanel.defaultProps = {
  oppgaverForAvdeling: [],
};

const formDefaultValues = { valgtYtelseType: ALLE_YTELSETYPER_VALGT };

const mapStateToProps = (state) => ({
  oppgaverForAvdeling: getOppgaverForAvdeling(state),
  fagsakYtelseTyper: getKodeverk(kodeverkTyper.FAGSAK_YTELSE_TYPE)(state),
  initialValues: getValuesFromReduxState(state)[formName] || formDefaultValues,
});

export default connect(mapStateToProps)(FordelingAvBehandlingstypePanel);
