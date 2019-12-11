import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl, intlShape } from 'react-intl';
import { bindActionCreators, Dispatch } from 'redux';
import { Undertekst } from 'nav-frontend-typografi';
import moment from 'moment';

import {
  RadioGroupField, RadioOption,
} from 'form/FinalFields';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { getKodeverk } from 'kodeverk/duck';
import kodeverkPropType from 'kodeverk/kodeverkPropType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { KoSorteringType } from 'kodeverk/KoSorteringTsType';
import { Kodeverk } from 'kodeverk/kodeverkTsType';
import {
  lagreSakslisteSortering as lagreSakslisteSorteringActionCreator,
  lagreSakslisteSorteringErDynamiskPeriode as lagreSakslisteSorteringErDynamiskPeriodeActionCreator,
  lagreSakslisteSorteringTidsintervallDato as lagreSakslisteSorteringTidsintervallDatoActionCreator,
  lagreSakslisteSorteringTidsintervallDager as lagreSakslisteSorteringTidsintervallDagerActionCreator,
} from '../../duck';
import DatoSorteringValg from './DatoSorteringValg';
import BelopSorteringValg from './BelopSorteringValg';

const finnDato = antallDager => moment().add(antallDager, 'd').format();


interface TsProps {
  intl: any;
  koSorteringTyper: KoSorteringType[];
  valgtSakslisteId: number;
  valgteBehandlingtyper: Kodeverk[];
  lagreSakslisteSortering: (sakslisteId: number, sakslisteSorteringValg: KoSorteringType, avdelingEnhet: string) => void;
  lagreSakslisteSorteringErDynamiskPeriode: (sakslisteId: number, avdelingEnhet: string) => void;
  lagreSakslisteSorteringTidsintervallDato: (sakslisteId: number, fomDato: string, tomDato: string, avdelingEnhet: string) => void;
  lagreSakslisteSorteringTidsintervallDager: (sakslisteId: number, fomDagr: number, tomDagr: number, avdelingEnhet: string) => void;
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
  valgteBehandlingtyper,
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
        (koSortering.kode != 'BELOP' || (valgteBehandlingtyper.length == 1 && valgteBehandlingtyper[0].kode == 'BT-009')) && (
        <RadioOption
          key={koSortering.kode}
          value={koSortering.kode}
          label={koSortering.navn}
        >

          {(koSortering.felttype === 'DATO') && (

          <DatoSorteringValg
            intl={intl}
            valgtSakslisteId={valgtSakslisteId}
            lagreSakslisteSorteringErDynamiskPeriode={lagreSakslisteSorteringErDynamiskPeriode}
            lagreSakslisteSorteringTidsintervallDato={lagreSakslisteSorteringTidsintervallDato}
            lagreSakslisteSorteringTidsintervallDager={lagreSakslisteSorteringTidsintervallDager}
            valgtAvdelingEnhet={valgtAvdelingEnhet}
            erDynamiskPeriode={erDynamiskPeriode}
            fomDager={fomDager}
            tomDager={tomDager}
            fomDato={fomDato}
            tomDato={tomDato}
          />
          )}
          {(koSortering.felttype === 'HELTALL') && (
          <BelopSorteringValg
            intl={intl}
            valgtSakslisteId={valgtSakslisteId}
            lagreSakslisteSorteringNumerisk={lagreSakslisteSorteringTidsintervallDager}
            valgtAvdelingEnhet={valgtAvdelingEnhet}
            fom={fomDager}
            tom={tomDager}
          />
          )}
        </RadioOption>
          )
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
