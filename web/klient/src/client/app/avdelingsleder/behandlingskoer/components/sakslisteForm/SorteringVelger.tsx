import React, { FunctionComponent } from 'react';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl, WrappedComponentProps } from 'react-intl';
import { bindActionCreators, Dispatch } from 'redux';
import { Undertekst } from 'nav-frontend-typografi';

import {
  RadioGroupField, RadioOption,
} from 'form/FinalFields';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { getKodeverk } from 'kodeverk/duck';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { KoSorteringType } from 'kodeverk/KoSorteringTsType';
import { Kodeverk } from 'kodeverk/kodeverkTsType';
import behandlingType from 'kodeverk/behandlingType';
import {
  lagreSakslisteSortering as lagreSakslisteSorteringActionCreator,
  lagreSakslisteSorteringErDynamiskPeriode as lagreSakslisteSorteringErDynamiskPeriodeActionCreator,
  lagreSakslisteSorteringTidsintervallDato as lagreSakslisteSorteringTidsintervallDatoActionCreator,
  lagreSakslisteSorteringNumeriskIntervall as lagreSakslisteSorteringNumeriskIntervallActionCreator,
} from '../../duck';
import DatoSorteringValg from './DatoSorteringValg';
import BelopSorteringValg from './BelopSorteringValg';

interface OwnProps {
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

const bareTilbakekrevingValgt = (valgteBehandlingtyper: Kodeverk[]) => valgteBehandlingtyper
  && valgteBehandlingtyper.some((type) => type.kode === behandlingType.TILBAKEBETALING
    || type.kode === behandlingType.TILBAKEBETALING_REVURDERING)
  && !valgteBehandlingtyper.some((type) => (type.kode !== behandlingType.TILBAKEBETALING && type.kode !== behandlingType.TILBAKEBETALING_REVURDERING));

/**
 * SorteringVelger
 */
export const SorteringVelger: FunctionComponent<OwnProps & WrappedComponentProps> = ({
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
}) => (
  <>
    <Undertekst>
      <FormattedMessage id="SorteringVelger.Sortering" />
    </Undertekst>
    <VerticalSpacer eightPx />
    <RadioGroupField
      name="sortering"
      direction="vertical"
      onChange={(sorteringType) => lagreSakslisteSortering(valgtSakslisteId, sorteringType, valgtAvdelingEnhet)}
    >
      {koSorteringTyper.map((koSortering) => (
        (koSortering.feltkategori !== 'TILBAKEKREVING' || bareTilbakekrevingValgt(valgteBehandlingtyper)) && (
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

const mapStateToProps = (state) => ({
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
