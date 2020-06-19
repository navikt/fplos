import React, { FunctionComponent } from 'react';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';

import { Form } from 'react-final-form';
import moment from 'moment';
import { Element } from 'nav-frontend-typografi';
import { Row, Column } from 'nav-frontend-grid';

import StoreValuesInLocalStorage from 'form/StoreValuesInLocalStorage';
import { getValueFromLocalStorage } from 'utils/localStorageHelper';
import { RadioGroupField, RadioOption, SelectField } from 'form/FinalFields';
import { useKodeverk } from 'data/rest-api-hooks';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import ManueltPaVentGraf from './ManueltPaVentGraf';
import OppgaverManueltPaVent from './oppgaverManueltPaVentTsType';

import styles from './manueltPaVentPanel.less';

const finnFagsakYtelseTypeNavn = (fagsakYtelseTyper, valgtFagsakYtelseType) => {
  const type = fagsakYtelseTyper.find((fyt) => fyt.kode === valgtFagsakYtelseType);
  return type ? type.navn : '';
};

export const ALLE_YTELSETYPER_VALGT = 'ALLE';
export const UKE_4 = '4';

const uker = [{
  kode: UKE_4,
  tekstKode: 'ManueltPaVentPanel.FireSisteUker',
}, {
  kode: '8',
  tekstKode: 'ManueltPaVentPanel.AtteSisteUker',
}];

const erDatoInnenforPeriode = (behandlingFrist, ukevalg) => {
  if (ukevalg === uker[1].kode) {
    return true;
  }

  const dataOmFireUker = moment().add(4, 'w');
  return moment(behandlingFrist).isSameOrBefore(dataOmFireUker);
};

interface OwnProps {
  intl: any;
  width: number;
  height: number;
  oppgaverManueltPaVent: OppgaverManueltPaVent[];
}

const formName = 'manueltPaVentForm';
const formDefaultValues = { valgtYtelsetype: ALLE_YTELSETYPER_VALGT, ukevalg: UKE_4 };


/**
 * ManueltPaVentPanel.
 */
export const ManueltPaVentPanel: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  width,
  height,
  oppgaverManueltPaVent,
}) => {
  const fagsakYtelseTyper = useKodeverk(kodeverkTyper.FAGSAK_YTELSE_TYPE);
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
            <FormattedMessage id="ManueltPaVentPanel.SattPaVent" />
          </Element>
          <VerticalSpacer sixteenPx />
          <Row>
            <Column xs="2">
              <SelectField
                name="ukevalg"
                label=""
                selectValues={uker.map((u) => <option key={u.kode} value={u.kode}>{intl.formatMessage({ id: u.tekstKode })}</option>)}
                bredde="l"
              />
            </Column>
            <Column xs="8">
              <div className={styles.radioPadding}>
                <RadioGroupField name="valgtYtelsetype">
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
                    label={<FormattedMessage id="ManueltPaVentPanel.Alle" />}
                  />
                </RadioGroupField>
              </div>
            </Column>
          </Row>
          <ManueltPaVentGraf
            width={width}
            height={height}
            isFireUkerValgt={values.ukevalg === UKE_4}
            oppgaverManueltPaVent={oppgaverManueltPaVent && oppgaverManueltPaVent
              .filter((ompv) => (values.valgtYtelsetype === ALLE_YTELSETYPER_VALGT ? true : values.valgtYtelsetype === ompv.fagsakYtelseType.kode))
              .filter((ompv) => erDatoInnenforPeriode(ompv.behandlingFrist, values.ukevalg))}
          />
        </div>
      )}
    />
  );
};

export default injectIntl(ManueltPaVentPanel);
