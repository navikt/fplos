import React, { FunctionComponent } from 'react';
import { FormattedMessage, injectIntl, WrappedComponentProps } from 'react-intl';
import { Undertekst } from 'nav-frontend-typografi';

import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import KodeverkType from 'kodeverk/kodeverkTyper';
import BehandlingType from 'kodeverk/behandlingType';
import useKodeverk from 'data/useKodeverk';
import KoSorteringType from 'types/avdelingsleder/koSorteringTsType';
import { RadioOption, RadioGroupField } from 'form/formIndex';

import DatoSorteringValg from './DatoSorteringValg';
import BelopSorteringValg from './BelopSorteringValg';

interface OwnProps {
  valgtSakslisteId: number;
  valgteBehandlingtyper?: string[];
  valgtAvdelingEnhet: string;
  erDynamiskPeriode: boolean;
  fra?: number;
  til?: number;
  fomDato?: string;
  tomDato?: string;
  hentAvdelingensSakslister: (params: {avdelingEnhet: string}) => void;
  hentAntallOppgaver: (sakslisteId: number, avdelingEnhet: string) => void;
}

const bareTilbakekrevingValgt = (valgteBehandlingtyper?: string[]) => valgteBehandlingtyper
  && valgteBehandlingtyper.some((type) => type === BehandlingType.TILBAKEBETALING
    || type === BehandlingType.TILBAKEBETALING_REVURDERING)
  && !valgteBehandlingtyper.some((type) => (type !== BehandlingType.TILBAKEBETALING && type !== BehandlingType.TILBAKEBETALING_REVURDERING));

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
  const koSorteringer = useKodeverk<KoSorteringType>(KodeverkType.KO_SORTERING);

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
        {koSorteringer
          .filter((koSortering) => koSortering.feltkategori !== 'TILBAKEKREVING' || bareTilbakekrevingValgt(valgteBehandlingtyper))
          .map((koSortering) => (
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
          ))}
      </RadioGroupField>
    </>
  );
};

export default injectIntl(SorteringVelger);
