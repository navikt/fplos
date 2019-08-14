import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import moment from 'moment';
import { injectIntl, intlShape, FormattedMessage } from 'react-intl';

import { Form } from 'react-final-form';
import { Element } from 'nav-frontend-typografi';
import { Row, Column } from 'nav-frontend-grid';

import StoreValuesInReduxState from 'form/reduxBinding/StoreValuesInReduxState';
import { getValuesFromReduxState } from 'form/reduxBinding/formDuck';
import { RadioGroupField, RadioOption, SelectField } from 'form/FinalFields';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import kodeverkPropType from 'kodeverk/kodeverkPropType';
import { Kodeverk } from 'kodeverk/kodeverkTsType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { getKodeverk } from 'kodeverk/duck';
import TilBehandlingGraf from './TilBehandlingGraf';
import { OppgaveForDato } from './oppgaverForDatoTsType';
import oppgaverForDatoPropType from './oppgaverForDatoPropType';
import { getOppgaverPerDato } from '../../duck';

import styles from './tilBehandlingPanel.less';

export const ALLE_YTELSETYPER_VALGT = 'ALLE';

const UKE_2 = '2';
const uker = [{
  kode: UKE_2,
  tekstKode: 'TilBehandlingPanel.ToSisteUker',
}, {
  kode: '4',
  tekstKode: 'TilBehandlingPanel.FireSisteUker',
}];

const erDatoInnenforPeriode = (oppgaveForAvdeling, ukevalg) => {
  if (ukevalg === uker[1].kode) {
    return true;
  }
  const toUkerSiden = moment().subtract(2, 'w');
  return moment(oppgaveForAvdeling.opprettetDato).isSameOrAfter(toUkerSiden);
};

const finnFagsakYtelseTypeNavn = (fagsakYtelseTyper, valgtFagsakYtelseType) => {
  const type = fagsakYtelseTyper.find(fyt => fyt.kode === valgtFagsakYtelseType);
  return type ? type.navn : '';
};

const slaSammenLikeBehandlingstyperOgDatoer = (oppgaverForAvdeling) => {
  const sammenslatte = [];

  oppgaverForAvdeling.forEach((o) => {
    const index = sammenslatte.findIndex(s => s.behandlingType.kode === o.behandlingType.kode && s.opprettetDato === o.opprettetDato);
    if (index === -1) {
      sammenslatte.push(o);
    } else {
      sammenslatte[index] = {
        behandlingType: sammenslatte[index].behandlingType,
        opprettetDato: sammenslatte[index].opprettetDato,
        antall: sammenslatte[index].antall + o.antall,
      };
    }
  });

  return sammenslatte;
};

interface InitialValues {
  ytelseType: string;
  ukevalg: string;
}

interface TsProps {
  intl: any;
  width: number;
  height: number;
  fagsakYtelseTyper: Kodeverk[];
  oppgaverPerDato?: OppgaveForDato[];
  initialValues: InitialValues;
}

const formName = 'tilBehandlingForm';

/**
 * TilBehandlingPanel.
 */
export const TilBehandlingPanel = ({
  intl,
  width,
  height,
  fagsakYtelseTyper,
  oppgaverPerDato,
  initialValues,
}: TsProps) => (
  <Form
    onSubmit={() => undefined}
    initialValues={initialValues}
    render={({ values }) => (
      <div>
        <StoreValuesInReduxState onUmount stateKey={formName} values={values} />
        <Element>
          <FormattedMessage id="TilBehandlingPanel.TilBehandling" />
        </Element>
        <VerticalSpacer eightPx />
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
              <RadioGroupField name="ytelseType">
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
            </div>
          </Column>
        </Row>
        <TilBehandlingGraf
          width={width}
          height={height}
          isToUkerValgt={values.ukevalg === UKE_2}
          oppgaverPerDato={oppgaverPerDato ? slaSammenLikeBehandlingstyperOgDatoer(oppgaverPerDato
            .filter(ofa => (values.ytelseType === ALLE_YTELSETYPER_VALGT ? true : values.ytelseType === ofa.fagsakYtelseType.kode))
            .filter(ofa => erDatoInnenforPeriode(ofa, values.ukevalg))) : []
          }
        />
      </div>
    )}
  />
);

TilBehandlingPanel.propTypes = {
  intl: intlShape.isRequired,
  width: PropTypes.number.isRequired,
  height: PropTypes.number.isRequired,
  fagsakYtelseTyper: PropTypes.arrayOf(kodeverkPropType).isRequired,
  oppgaverPerDato: PropTypes.arrayOf(oppgaverForDatoPropType),
  initialValues: PropTypes.shape({
    ytelseType: PropTypes.string.isRequired,
    ukevalg: PropTypes.string.isRequired,
  }).isRequired,
};

TilBehandlingPanel.defaultProps = {
  oppgaverPerDato: [],
};

const formDefaultValues = { ytelseType: ALLE_YTELSETYPER_VALGT, ukevalg: UKE_2 };

const mapStateToProps = state => ({
  fagsakYtelseTyper: getKodeverk(kodeverkTyper.FAGSAK_YTELSE_TYPE)(state),
  oppgaverPerDato: getOppgaverPerDato(state),
  initialValues: getValuesFromReduxState(state)[formName] || formDefaultValues,
});

export default connect(mapStateToProps)(injectIntl(TilBehandlingPanel));
