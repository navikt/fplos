import React, { FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';

import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import KodeverkType from 'kodeverk/kodeverkTyper';
import BehandlingType from 'kodeverk/behandlingType';
import useKodeverk from 'data/useKodeverk';
import KoSorteringType from 'types/avdelingsleder/koSorteringTsType';
import { formHooks, RadioGroupPanel } from '@navikt/ft-form-hooks';

import DatoSorteringValg from './DatoSorteringValg';
import BelopSorteringValg from './BelopSorteringValg';

interface OwnProps {
  valgtSakslisteId: number;
  valgteBehandlingtyper?: string[];
  valgtAvdelingEnhet: string;
  erDynamiskPeriode: boolean;
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
const SorteringVelger: FunctionComponent<OwnProps> = ({
  valgtSakslisteId,
  valgteBehandlingtyper,
  valgtAvdelingEnhet,
  erDynamiskPeriode,
  hentAvdelingensSakslister,
  hentAntallOppgaver,
}) => {
  const { resetField } = formHooks.useFormContext();

  const { startRequest: lagreSakslisteSortering } = restApiHooks.useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING);
  const { startRequest: lagreSakslisteSorteringNumeriskIntervall } = restApiHooks.useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_INTERVALL);
  const koSorteringer = useKodeverk<KoSorteringType>(KodeverkType.KO_SORTERING);

  return (
    <RadioGroupPanel
      name="sortering"
      label={<FormattedMessage id="SorteringVelger.Sortering" />}
      onChange={(sorteringType) => {
        resetField('fra', { defaultValue: '' });
        resetField('til', { defaultValue: '' });
        resetField('fomDato', { defaultValue: '' });
        resetField('tomDato', { defaultValue: '' });
        resetField('erDynamiskPeriode', { defaultValue: '' });

        return lagreSakslisteSortering({
          sakslisteId: valgtSakslisteId,
          sakslisteSorteringValg: sorteringType,
          avdelingEnhet: valgtAvdelingEnhet,
        }).then(() => {
          hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
          hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
        });
      }}
      radios={koSorteringer
        .filter((koSortering) => koSortering.feltkategori !== 'TILBAKEKREVING' || bareTilbakekrevingValgt(valgteBehandlingtyper))
        .map((koSortering) => ({
          value: koSortering.kode,
          label: koSortering.navn,
          element: (
            <>
              {(koSortering.felttype === 'DATO') && (
                <DatoSorteringValg
                  valgtSakslisteId={valgtSakslisteId}
                  lagreSakslisteSorteringTidsintervallDager={lagreSakslisteSorteringNumeriskIntervall}
                  valgtAvdelingEnhet={valgtAvdelingEnhet}
                  erDynamiskPeriode={erDynamiskPeriode}
                  hentAvdelingensSakslister={hentAvdelingensSakslister}
                  hentAntallOppgaver={hentAntallOppgaver}
                />
              )}
              {(koSortering.felttype === 'HELTALL') && (
                <BelopSorteringValg
                  valgtSakslisteId={valgtSakslisteId}
                  lagreSakslisteSorteringNumerisk={lagreSakslisteSorteringNumeriskIntervall}
                  valgtAvdelingEnhet={valgtAvdelingEnhet}
                  hentAvdelingensSakslister={hentAvdelingensSakslister}
                  hentAntallOppgaver={hentAntallOppgaver}
                />
              )}
            </>
          ),
        }))}
    />
  );
};

export default SorteringVelger;
