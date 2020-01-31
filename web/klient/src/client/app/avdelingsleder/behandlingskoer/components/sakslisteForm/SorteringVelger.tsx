import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl, intlShape } from 'react-intl';
import { bindActionCreators, Dispatch } from 'redux';
import { Undertekst } from 'nav-frontend-typografi';

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
  lagreSakslisteSorteringNumeriskIntervall as lagreSakslisteSorteringNumeriskIntervallActionCreator,
} from '../../duck';
import DatoSorteringValg from './DatoSorteringValg';
import BelopSorteringValg from './BelopSorteringValg';

interface TsProps {
  intl: any;
  koSorteringTyper: KoSorteringType[];
  valgtSakslisteId: number;
  valgteBehandlingtyper: Kodeverk[];
  lagreSakslisteSortering: (sakslisteId: number, sakslisteSorteringValg: KoSorteringType, avdelingEnhet: string) => void;
  lagreSakslisteSorteringErDynamiskPeriode: (sakslisteId: number, avdelingEnhet: string) => void;
  lagreSakslisteSorteringTidsintervallDato: (sakslisteId: number, fomDato: string, tomDato: string, avdelingEnhet: string) => void;
  lagreSakslisteSorteringNumeriskIntervall: (sakslisteId: number, fra: number, til: number, avdelingEnhet: string) => void;
  valgtAvdelingEnhet: string;
  erDynamiskPeriode: boolean;
  fra: number;
  til: number;
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
  lagreSakslisteSorteringNumeriskIntervall,
  fra,
  til,
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
        (koSortering.feltkategori !== 'TILBAKEKREVING' || (valgteBehandlingtyper.length === 1 && valgteBehandlingtyper[0].kode === 'BT-009')) && (
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
            lagreSakslisteSorteringTidsintervallDager={lagreSakslisteSorteringNumeriskIntervall}
            valgtAvdelingEnhet={valgtAvdelingEnhet}
            erDynamiskPeriode={erDynamiskPeriode}
            fra={fra}
            til={til}
            fomDato={fomDato}
            tomDato={tomDato}
          />
          )}
          {(koSortering.felttype === 'HELTALL') && (
          <BelopSorteringValg
            intl={intl}
            valgtSakslisteId={valgtSakslisteId}
            lagreSakslisteSorteringNumerisk={lagreSakslisteSorteringNumeriskIntervall}
            valgtAvdelingEnhet={valgtAvdelingEnhet}
            fra={fra}
            til={til}
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
  lagreSakslisteSorteringNumeriskIntervall: PropTypes.func.isRequired,
  valgtAvdelingEnhet: PropTypes.string.isRequired,
  erDynamiskPeriode: PropTypes.bool.isRequired,
  fra: PropTypes.number,
  til: PropTypes.number,
  fomDato: PropTypes.string,
  tomDato: PropTypes.string,
};

SorteringVelger.defaultProps = {
  fra: undefined,
  til: undefined,
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
    lagreSakslisteSorteringNumeriskIntervall: lagreSakslisteSorteringNumeriskIntervallActionCreator,
  }, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(SorteringVelger));
