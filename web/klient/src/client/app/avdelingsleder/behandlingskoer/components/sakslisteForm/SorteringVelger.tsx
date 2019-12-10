import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl, intlShape } from 'react-intl';
import { bindActionCreators, Dispatch } from 'redux';
import { Undertekst } from 'nav-frontend-typografi';
import moment from 'moment';

import { ISO_DATE_FORMAT, DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import DateLabel from 'sharedComponents/DateLabel';
import { hasValidPosOrNegInteger, hasValidDate } from 'utils/validation/validators';
import { FlexContainer, FlexRow, FlexColumn } from 'sharedComponents/flexGrid';
import ArrowBox from 'sharedComponents/ArrowBox';
import {
  RadioGroupField, RadioOption, InputField, CheckboxField, DatepickerField,
} from 'form/FinalFields';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { getKodeverk } from 'kodeverk/duck';
import { Kodeverk } from 'kodeverk/kodeverkTsType';
import kodeverkPropType from 'kodeverk/kodeverkPropType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { KoSorteringType } from 'kodeverk/KoSorteringTsType';
import AutoLagringVedBlur from './AutoLagringVedBlur';
import {
  lagreSakslisteSortering as lagreSakslisteSorteringActionCreator,
  lagreSakslisteSorteringErDynamiskPeriode as lagreSakslisteSorteringErDynamiskPeriodeActionCreator,
  lagreSakslisteSorteringTidsintervallDato as lagreSakslisteSorteringTidsintervallDatoActionCreator,
  lagreSakslisteSorteringTidsintervallDager as lagreSakslisteSorteringTidsintervallDagerActionCreator,
} from '../../duck';

import styles from './sorteringVelger.less';

const finnDato = antallDager => moment().add(antallDager, 'd').format();

const getLagreDatoFn = (lagreSakslisteSorteringTidsintervallDato, valgtSakslisteId, valgtAvdelingEnhet, annenDato, erFomDato) => (e) => {
  let dato = e.target.value;
  if (dato) {
    dato = moment(dato, DDMMYYYY_DATE_FORMAT, true);
  }
  if (!dato || dato.isValid()) {
    const d = dato ? dato.format(ISO_DATE_FORMAT) : dato;
    return erFomDato
     ? lagreSakslisteSorteringTidsintervallDato(valgtSakslisteId, d, annenDato, valgtAvdelingEnhet)
     : lagreSakslisteSorteringTidsintervallDato(valgtSakslisteId, annenDato, d, valgtAvdelingEnhet);
  }
  return undefined;
};

interface TsProps {
  intl: any;
  koSorteringTyper: Kodeverk[];
  valgtSakslisteId: number;
  lagreSakslisteSortering: (sakslisteId: number, sakslisteSorteringValg: Kodeverk, avdelingEnhet: string) => void;
  lagreSakslisteSorteringErDynamiskPeriode: (sakslisteId: number, avdelingEnhet: string) => void;
  lagreSakslisteSorteringTidsintervallDato: (sakslisteId: number, fomDato: string, tomDato: string, avdelingEnhet: string) => void;
  lagreSakslisteSorteringTidsintervallDager: (sakslisteId: number, fomDaget: number, tomDagr: number, avdelingEnhet: string) => void;
  valgtAvdelingEnhet: string;
  erDynamiskPeriode: boolean;
  fomDager: number;
  tomDager: number;
  fomDato: string;
  tomDato: string;
}

/**
 * SorteringVelger
 */
export const SorteringVelger = ({
  intl,
  koSorteringTyper,
  valgtSakslisteId,
  lagreSakslisteSortering,
  lagreSakslisteSorteringErDynamiskPeriode,
  valgtAvdelingEnhet,
  erDynamiskPeriode,
  lagreSakslisteSorteringTidsintervallDato,
  lagreSakslisteSorteringTidsintervallDager,
  fomDager,
  tomDager,
  fomDato,
  tomDato,
}: TsProps) => (
  <>
    <Undertekst>
      <FormattedMessage id="SorteringVelger.Sortering" />
    </Undertekst>
    <VerticalSpacer eightPx />
    <RadioGroupField
      name="sortering"
      direction="vertical"
      onChange={sorteringType => lagreSakslisteSortering(valgtSakslisteId, sorteringType, valgtAvdelingEnhet)}
    >
      {koSorteringTyper.map(koSortering => (
        <RadioOption
          key={koSortering.kode}
          value={koSortering.kode}
          label={koSortering.navn}
        >
          <ArrowBox>
            <Undertekst>
              <FormattedMessage id="SorteringVelger.FiltrerPaTidsintervall" />
            </Undertekst>

            {erDynamiskPeriode && (
            <>
              <AutoLagringVedBlur
                lagre={values => lagreSakslisteSorteringTidsintervallDager(valgtSakslisteId, values.fomDager, values.tomDager, valgtAvdelingEnhet)}
                fieldNames={['fomDager', 'tomDager']}
              />
              <FlexContainer>
                <FlexRow>
                  <FlexColumn>
                    <InputField
                      name="fomDager"
                      className={styles.dato}
                      label={intl.formatMessage({ id: 'SorteringVelger.Fom' })}
                      validate={[hasValidPosOrNegInteger]}
                      onBlurValidation
                      bredde="XS"
                    />
                    {(fomDager || fomDager === 0) && (
                    <Undertekst>
                      <DateLabel dateString={finnDato(fomDager)} />
                    </Undertekst>
                    )}
                  </FlexColumn>
                  <FlexColumn>
                    <Undertekst className={styles.dager}>
                      <FormattedMessage id="SorteringVelger.DagerMedBindestrek" />
                    </Undertekst>
                  </FlexColumn>
                  <FlexColumn>
                    <InputField
                      name="tomDager"
                      className={styles.dato}
                      label={intl.formatMessage({ id: 'SorteringVelger.Tom' })}
                      validate={[hasValidPosOrNegInteger]}
                      onBlurValidation
                      bredde="XS"
                    />
                    {(tomDager || tomDager === 0) && (
                    <Undertekst>
                      <DateLabel dateString={finnDato(tomDager)} />
                    </Undertekst>
                    )}
                  </FlexColumn>
                  <FlexColumn>
                    <Undertekst className={styles.dagerMedBindestrek}>
                      <FormattedMessage id="SorteringVelger.Dager" />
                    </Undertekst>
                  </FlexColumn>
                </FlexRow>
              </FlexContainer>
            </>
              )}
            {!erDynamiskPeriode && (
            <>
              <FlexContainer>
                <FlexRow>
                  <FlexColumn>
                    <DatepickerField
                      name="fomDato"
                      label={{ id: 'SorteringVelger.Fom' }}
                      onBlurValidation
                      validate={[hasValidDate]}
                      onBlur={getLagreDatoFn(lagreSakslisteSorteringTidsintervallDato, valgtSakslisteId, valgtAvdelingEnhet, tomDato, true)}
                    />
                  </FlexColumn>
                  <FlexColumn>
                    <Undertekst className={styles.dager}>
                      <FormattedMessage id="SorteringVelger.Bindestrek" />
                    </Undertekst>
                  </FlexColumn>
                  <FlexColumn className={styles.tomDato}>
                    <DatepickerField
                      name="tomDato"
                      label={{ id: 'SorteringVelger.Tom' }}
                      onBlurValidation
                      validate={[hasValidDate]}
                      onBlur={getLagreDatoFn(lagreSakslisteSorteringTidsintervallDato, valgtSakslisteId, valgtAvdelingEnhet, fomDato, false)}
                    />
                  </FlexColumn>
                </FlexRow>
              </FlexContainer>
            </>
            )}
            <CheckboxField
              name="erDynamiskPeriode"
              label={intl.formatMessage({ id: 'SorteringVelger.DynamiskPeriode' })}
              onChange={() => lagreSakslisteSorteringErDynamiskPeriode(valgtSakslisteId, valgtAvdelingEnhet)}
            />
            <VerticalSpacer eightPx />
          </ArrowBox>
        </RadioOption>
      ))}
    </RadioGroupField>
  </>
);

SorteringVelger.propTypes = {
  intl: intlShape.isRequired,
  koSorteringTyper: PropTypes.arrayOf(kodeverkPropType).isRequired,
  valgtSakslisteId: PropTypes.number.isRequired,
  lagreSakslisteSortering: PropTypes.func.isRequired,
  lagreSakslisteSorteringErDynamiskPeriode: PropTypes.func.isRequired,
  lagreSakslisteSorteringTidsintervallDato: PropTypes.func.isRequired,
  lagreSakslisteSorteringTidsintervallDager: PropTypes.func.isRequired,
  valgtAvdelingEnhet: PropTypes.string.isRequired,
  erDynamiskPeriode: PropTypes.bool.isRequired,
  fomDager: PropTypes.number,
  tomDager: PropTypes.number,
  fomDato: PropTypes.string,
  tomDato: PropTypes.string,
};

SorteringVelger.defaultProps = {
  fomDager: undefined,
  tomDager: undefined,
  fomDato: undefined,
  tomDato: undefined,
};

const mapStateToProps = state => ({
  koSorteringTyper: getKodeverk(kodeverkTyper.KO_SORTERING)(state),
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  ...bindActionCreators({
    lagreSakslisteSortering: lagreSakslisteSorteringActionCreator,
    lagreSakslisteSorteringErDynamiskPeriode: lagreSakslisteSorteringErDynamiskPeriodeActionCreator,
    lagreSakslisteSorteringTidsintervallDato: lagreSakslisteSorteringTidsintervallDatoActionCreator,
    lagreSakslisteSorteringTidsintervallDager: lagreSakslisteSorteringTidsintervallDagerActionCreator,
  }, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(SorteringVelger));
