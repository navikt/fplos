import React, { Fragment, FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';
import { Undertekst } from 'nav-frontend-typografi';

import kodeverkTyper from 'kodeverk/kodeverkTyper';
import useKodeverk from 'data/useKodeverk';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import ArrowBox from 'sharedComponents/ArrowBox';
import Kodeverk from 'kodeverk/kodeverkTsType';
import { CheckboxField, RadioGroupField, RadioOption } from 'form/FinalFields';

import styles from './andreKriterierVelger.less';

interface OwnProps {
  valgtSakslisteId: number;
  lagreSakslisteAndreKriterier: (sakslisteId: number, andreKriterierType: Kodeverk, isChecked: boolean, skalInkludere: boolean, avdelingEnhet: string) => void;
  valgtAvdelingEnhet: string;
  values: any;
}

/**
 * AndreKriterierVelger
 */
const AndreKriterierVelger: FunctionComponent<OwnProps> = ({
  valgtSakslisteId,
  lagreSakslisteAndreKriterier,
  valgtAvdelingEnhet,
  values,
}) => {
  const andreKriterierTyper = useKodeverk(kodeverkTyper.ANDRE_KRITERIER_TYPE);
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
            onChange={(isChecked) => lagreSakslisteAndreKriterier(valgtSakslisteId, akt, isChecked, true, valgtAvdelingEnhet)}
          />
          {values[akt.kode] && (
            <>
              <VerticalSpacer sixteenPx />
              <div className={styles.arrowbox}>
                <ArrowBox alignOffset={30}>
                  <RadioGroupField
                    name={`${akt.kode}_inkluder`}
                    onChange={(skalInkludere) => lagreSakslisteAndreKriterier(valgtSakslisteId, akt, true, skalInkludere, valgtAvdelingEnhet)}
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
