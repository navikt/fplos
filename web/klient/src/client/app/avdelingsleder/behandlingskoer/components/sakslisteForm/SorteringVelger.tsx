import React, { FunctionComponent } from 'react';
import { FormattedMessage, injectIntl, WrappedComponentProps } from 'react-intl';

import { Undertekst } from 'nav-frontend-typografi';

import {
  RadioGroupField, RadioOption,
} from 'form/FinalFields';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import Kodeverk from 'kodeverk/kodeverkTsType';
import behandlingType from 'kodeverk/behandlingType';
import DatoSorteringValg from './DatoSorteringValg';
import BelopSorteringValg from './BelopSorteringValg';
import KoSorteringType from '../../KoSorteringTsType';

interface OwnProps {
  alleKodeverk: {[key: string]: KoSorteringType[]};
  valgtSakslisteId: number;
  valgteBehandlingtyper: Kodeverk[];
  valgtAvdelingEnhet: string;
  erDynamiskPeriode: boolean;
  fra: number;
  til: number;
  fomDato?: string;
  tomDato?: string;
  lagreSakslisteSortering: (sakslisteId: number, sakslisteSorteringValg: KoSorteringType, avdelingEnhet: string) => void;
  lagreSakslisteSorteringErDynamiskPeriode: (sakslisteId: number, avdelingEnhet: string) => void;
  lagreSakslisteSorteringTidsintervallDato: (sakslisteId: number, fomDato: string, tomDato: string, avdelingEnhet: string) => void;
  lagreSakslisteSorteringNumeriskIntervall: (sakslisteId: number, fra: number, til: number, avdelingEnhet: string) => void;
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
  alleKodeverk,
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
      {alleKodeverk[kodeverkTyper.KO_SORTERING].map((koSortering) => (
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

export default injectIntl(SorteringVelger);
