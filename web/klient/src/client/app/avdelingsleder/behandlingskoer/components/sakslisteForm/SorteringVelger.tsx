import React, { FunctionComponent } from 'react';
import { FormattedMessage, injectIntl, WrappedComponentProps } from 'react-intl';

import { Undertekst } from 'nav-frontend-typografi';

import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import {
  RadioGroupField, RadioOption,
} from 'form/FinalFields';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import Kodeverk from 'kodeverk/kodeverkTsType';
import behandlingType from 'kodeverk/behandlingType';
import useKodeverk from 'data/useKodeverk';
import DatoSorteringValg from './DatoSorteringValg';
import BelopSorteringValg from './BelopSorteringValg';
import KoSorteringType from '../../KoSorteringTsType';

interface OwnProps {
  valgtSakslisteId: number;
  valgteBehandlingtyper?: Kodeverk[];
  valgtAvdelingEnhet: string;
  erDynamiskPeriode: boolean;
  fra?: number;
  til?: number;
  fomDato: string;
  tomDato: string;
  hentAvdelingensSakslister: (params: {avdelingEnhet: string}) => void;
  hentAntallOppgaver: (sakslisteId: number, avdelingEnhet: string) => void;
}

const bareTilbakekrevingValgt = (valgteBehandlingtyper?: Kodeverk[]) => valgteBehandlingtyper
  && valgteBehandlingtyper.some((type) => type.kode === behandlingType.TILBAKEBETALING
    || type.kode === behandlingType.TILBAKEBETALING_REVURDERING)
  && !valgteBehandlingtyper.some((type) => (type.kode !== behandlingType.TILBAKEBETALING && type.kode !== behandlingType.TILBAKEBETALING_REVURDERING));

/**
 * SorteringVelger
 */
const SorteringVelger: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  valgtSakslisteId,
  valgteBehandlingtyper,
  valgtAvdelingEnhet,
  erDynamiskPeriode,
  fra,
  til,
  fomDato,
  tomDato,
  hentAvdelingensSakslister,
  hentAntallOppgaver,
}) => {
  const { startRequest: lagreSakslisteSortering } = restApiHooks.useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING);
  const { startRequest: lagreSakslisteSorteringNumeriskIntervall } = restApiHooks.useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_INTERVALL);
  const koSorteringer = useKodeverk<KoSorteringType>(kodeverkTyper.KO_SORTERING);

  return (
    <>
      <Undertekst>
        <FormattedMessage id="SorteringVelger.Sortering" />
      </Undertekst>
      <VerticalSpacer eightPx />
      <RadioGroupField
        name="sortering"
        direction="vertical"
        onChange={(sorteringType) => lagreSakslisteSortering({
          sakslisteId: valgtSakslisteId,
          sakslisteSorteringValg: sorteringType,
          avdelingEnhet: valgtAvdelingEnhet,
        }).then(() => {
          hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
          hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
        })}
      >
        {koSorteringer.map((koSortering) => (
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
              lagreSakslisteSorteringTidsintervallDager={lagreSakslisteSorteringNumeriskIntervall}
              valgtAvdelingEnhet={valgtAvdelingEnhet}
              erDynamiskPeriode={erDynamiskPeriode}
              fra={fra}
              til={til}
              fomDato={fomDato}
              tomDato={tomDato}
              hentAvdelingensSakslister={hentAvdelingensSakslister}
              hentAntallOppgaver={hentAntallOppgaver}
            />
            )}
            {(koSortering.felttype === 'HELTALL') && (
            <BelopSorteringValg
              intl={intl}
              valgtSakslisteId={valgtSakslisteId}
              lagreSakslisteSorteringNumerisk={lagreSakslisteSorteringNumeriskIntervall}
              valgtAvdelingEnhet={valgtAvdelingEnhet}
              hentAvdelingensSakslister={hentAvdelingensSakslister}
              hentAntallOppgaver={hentAntallOppgaver}
            />
            )}
          </RadioOption>
          )
        ))}
      </RadioGroupField>
    </>
  );
};

export default injectIntl(SorteringVelger);
