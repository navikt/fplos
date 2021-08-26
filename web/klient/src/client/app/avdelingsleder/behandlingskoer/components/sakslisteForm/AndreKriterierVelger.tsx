import React, { Fragment, FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';
import { Undertekst } from 'nav-frontend-typografi';

import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import useKodeverk from 'data/useKodeverk';
import KodeverkType from 'kodeverk/kodeverkTyper';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import ArrowBox from 'sharedComponents/ArrowBox';
import { CheckboxField, RadioGroupField, RadioOption } from 'form/formIndex';

import styles from './andreKriterierVelger.less';

interface OwnProps {
  valgtSakslisteId: number;
  valgtAvdelingEnhet: string;
  values: any;
  hentAvdelingensSakslister: (params: {avdelingEnhet: string}) => void;
  hentAntallOppgaver: (sakslisteId: number, avdelingEnhet: string) => void;
}

/**
 * AndreKriterierVelger
 */
const AndreKriterierVelger: FunctionComponent<OwnProps> = ({
  valgtSakslisteId,
  valgtAvdelingEnhet,
  values,
  hentAvdelingensSakslister,
  hentAntallOppgaver,
}) => {
  const andreKriterierTyper = useKodeverk(KodeverkType.ANDRE_KRITERIER_TYPE);
  const { startRequest: lagreSakslisteAndreKriterier } = restApiHooks.useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER);

  return (
    <>
      <Undertekst>
        <FormattedMessage id="AndreKriterierVelger.AndreKriterier" />
      </Undertekst>
      <VerticalSpacer eightPx />
      {andreKriterierTyper.map((akt) => (
        <Fragment key={akt.kode}>
          <VerticalSpacer fourPx />
          <CheckboxField
            key={akt.kode}
            name={akt.kode}
            label={akt.navn}
            onChange={(isChecked) => lagreSakslisteAndreKriterier({
              sakslisteId: valgtSakslisteId,
              avdelingEnhet: valgtAvdelingEnhet,
              andreKriterierType: akt,
              checked: isChecked,
              inkluder: true,
            }).then(() => {
              hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
              hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
            })}
          />
          {values[akt.kode] && (
            <>
              <VerticalSpacer sixteenPx />
              <div className={styles.arrowbox}>
                <ArrowBox alignOffset={30}>
                  <RadioGroupField
                    name={`${akt.kode}_inkluder`}
                    onChange={(skalInkludere) => lagreSakslisteAndreKriterier({
                      sakslisteId: valgtSakslisteId,
                      avdelingEnhet: valgtAvdelingEnhet,
                      andreKriterierType: akt,
                      checked: true,
                      inkluder: skalInkludere,
                    }).then(() => {
                      hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
                      hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
                    })}
                  >
                    <RadioOption
                      value
                      label={<FormattedMessage id="AndreKriterierVelger.TaMed" />}
                    />
                    <RadioOption
                      value={false}
                      label={<FormattedMessage id="AndreKriterierVelger.Fjern" />}
                    />
                  </RadioGroupField>
                </ArrowBox>
              </div>
            </>
          )}
        </Fragment>
      ))}
    </>
  );
};

export default AndreKriterierVelger;
