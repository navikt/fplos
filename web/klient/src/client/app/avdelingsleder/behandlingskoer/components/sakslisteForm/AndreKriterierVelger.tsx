import React, { Fragment } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { FormattedMessage } from 'react-intl';
import { Undertekst } from 'nav-frontend-typografi';

import kodeverkTyper from 'kodeverk/kodeverkTyper';
import kodeverkPropType from 'kodeverk/kodeverkPropType';
import { getKodeverk } from 'kodeverk/duck';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import ArrowBox from 'sharedComponents/ArrowBox';
import { Kodeverk } from 'kodeverk/kodeverkTsType';
import { CheckboxField, RadioGroupField, RadioOption } from 'form/FinalFields';

import styles from './andreKriterierVelger.less';

interface TsProps {
  valgtSakslisteId: number;
  lagreSakslisteAndreKriterier: (sakslisteId: number, andreKriterierType: Kodeverk, isChecked: boolean, skalInkludere: boolean, avdelingEnhet: string) => void;
  valgtAvdelingEnhet: string;
  andreKriterierTyper: Kodeverk[];
  values: any;
}

/**
 * AndreKriterierVelger
 */
export const AndreKriterierVelger = ({
  valgtSakslisteId,
  lagreSakslisteAndreKriterier,
  valgtAvdelingEnhet,
  andreKriterierTyper,
  values,
}: TsProps) => (
  <>
    <Undertekst>
      <FormattedMessage id="AndreKriterierVelger.AndreKriterier" />
    </Undertekst>
    <VerticalSpacer eightPx />
    {andreKriterierTyper.map(akt => (
      <Fragment key={akt.kode}>
        <CheckboxField
          key={akt.kode}
          name={akt.kode}
          label={akt.navn}
          onChange={isChecked => lagreSakslisteAndreKriterier(valgtSakslisteId, akt, isChecked, true, valgtAvdelingEnhet)}
        />
        {values[akt.kode] && (
          <div className={styles.arrowbox}>
            <ArrowBox alignOffset={30}>
              <RadioGroupField
                name={`${akt.kode}_inkluder`}
                onChange={skalInkludere => lagreSakslisteAndreKriterier(valgtSakslisteId, akt, true, skalInkludere, valgtAvdelingEnhet)}
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
        )}
      </Fragment>
    ))
    }
  </>
);

AndreKriterierVelger.propTypes = {
  valgtSakslisteId: PropTypes.number.isRequired,
  lagreSakslisteAndreKriterier: PropTypes.func.isRequired,
  valgtAvdelingEnhet: PropTypes.string.isRequired,
  andreKriterierTyper: PropTypes.arrayOf(kodeverkPropType).isRequired,
};

const mapStateToProps = state => ({
  andreKriterierTyper: getKodeverk(kodeverkTyper.ANDRE_KRITERIER_TYPE)(state),
});

export default connect(mapStateToProps)(AndreKriterierVelger);
