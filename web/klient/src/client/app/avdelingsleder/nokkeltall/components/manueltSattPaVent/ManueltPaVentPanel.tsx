import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { injectIntl, intlShape, FormattedMessage } from 'react-intl';

import { Form } from 'react-final-form';
import moment from 'moment';
import { Element } from 'nav-frontend-typografi';
import { Row, Column } from 'nav-frontend-grid';

import StoreValuesInReduxState from 'form/reduxBinding/StoreValuesInReduxState';
import { getValuesFromReduxState } from 'form/reduxBinding/formDuck';
import { RadioGroupField, RadioOption, SelectField } from 'form/FinalFields';
import { Kodeverk } from 'kodeverk/kodeverkTsType';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import kodeverkPropType from 'kodeverk/kodeverkPropType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { getKodeverk } from 'kodeverk/duck';
import ManueltPaVentGraf from './ManueltPaVentGraf';
import { getOppgaverAvdelingManueltPaVent } from '../../duck';
import { OppgaverManueltPaVent } from './oppgaverManueltPaVentTsType';
import oppgaverManueltPaVentPropType from './oppgaverManueltPaVentPropType';

import styles from './manueltPaVentPanel.less';

const finnFagsakYtelseTypeNavn = (fagsakYtelseTyper, valgtFagsakYtelseType) => {
  const type = fagsakYtelseTyper.find(fyt => fyt.kode === valgtFagsakYtelseType);
  return type ? type.navn : '';
};

export const ALLE_YTELSETYPER_VALGT = 'ALLE';
const UKE_4 = '4';
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

interface InitialValues {
  valgtYtelsetype: string;
  ukevalg: string;
}

interface TsProps {
  intl: any;
  width: number;
  height: number;
  fagsakYtelseTyper: Kodeverk[];
  oppgaverManueltPaVent?: OppgaverManueltPaVent[];
  initialValues: InitialValues;
}

const formName = 'manueltPaVentForm';

/**
 * ManueltPaVentPanel.
 */
export const ManueltPaVentPanel = ({
  intl,
  width,
  height,
  fagsakYtelseTyper,
  oppgaverManueltPaVent,
  initialValues,
}: TsProps) => (
  <Form
    onSubmit={() => undefined}
    initialValues={initialValues}
    render={({ values }) => (
      <div>
        <StoreValuesInReduxState onUmount stateKey={formName} values={values} />
        <Element>
          <FormattedMessage id="ManueltPaVentPanel.SattPaVent" />
        </Element>
        <VerticalSpacer sixteenPx />
        <Row>
          <Column xs="2">
            <SelectField
              name="ukevalg"
              label=""
              selectValues={uker.map(u => <option key={u.kode} value={u.kode}>{intl.formatMessage({ id: u.tekstKode })}</option>)}
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
            .filter(ompv => (values.valgtYtelsetype === ALLE_YTELSETYPER_VALGT ? true : values.valgtYtelsetype === ompv.fagsakYtelseType.kode))
            .filter(ompv => erDatoInnenforPeriode(ompv.behandlingFrist, values.ukevalg))
          }
        />
      </div>
    )}
  />
);

ManueltPaVentPanel.propTypes = {
  intl: intlShape.isRequired,
  width: PropTypes.number.isRequired,
  height: PropTypes.number.isRequired,
  fagsakYtelseTyper: PropTypes.arrayOf(kodeverkPropType).isRequired,
  oppgaverManueltPaVent: PropTypes.arrayOf(oppgaverManueltPaVentPropType),
  initialValues: PropTypes.shape({
    valgtYtelsetype: PropTypes.string.isRequired,
    ukevalg: PropTypes.string.isRequired,
  }).isRequired,
};

ManueltPaVentPanel.defaultProps = {
  oppgaverManueltPaVent: [],
};

const formDefaultValues = { valgtYtelsetype: ALLE_YTELSETYPER_VALGT, ukevalg: UKE_4 };

const mapStateToProps = state => ({
  oppgaverManueltPaVent: getOppgaverAvdelingManueltPaVent(state),
  fagsakYtelseTyper: getKodeverk(kodeverkTyper.FAGSAK_YTELSE_TYPE)(state),
  initialValues: getValuesFromReduxState(state)[formName] || formDefaultValues,
});

export default connect(mapStateToProps)(injectIntl(ManueltPaVentPanel));
