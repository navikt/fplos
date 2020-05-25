import { Undertekst } from 'nav-frontend-typografi';
import { FormattedMessage, WrappedComponentProps } from 'react-intl';
import ArrowBox from 'sharedComponents/ArrowBox';
import React, { FunctionComponent } from 'react';
import { FlexColumn, FlexContainer, FlexRow } from 'sharedComponents/flexGrid';
import { InputField } from 'form/FinalFields';
import { hasValidPosOrNegInteger } from 'utils/validation/validators';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import styles from './sorteringVelger.less';
import AutoLagringVedBlur from './AutoLagringVedBlur';

interface OwnProps {
  valgtSakslisteId: number;
  lagreSakslisteSorteringNumerisk: (sakslisteId: number, fra: number, til: number, avdelingEnhet: string) => void;
  valgtAvdelingEnhet: string;
  fra: number;
  til: number;
}

export const BelopSorteringValg: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  valgtSakslisteId,
  valgtAvdelingEnhet,
  lagreSakslisteSorteringNumerisk,
}) => (
  <ArrowBox>
    <Undertekst>
      <FormattedMessage id="SorteringVelger.FiltrerPaHeltall" />
    </Undertekst>
    <>
      <AutoLagringVedBlur
        lagre={(values) => lagreSakslisteSorteringNumerisk(valgtSakslisteId, values.fra, values.til, valgtAvdelingEnhet)}
        fieldNames={['fra', 'til']}
      />
      <FlexContainer>
        <FlexRow>
          <FlexColumn>
            <InputField
              name="fra"
              className={styles.dato}
              placeholder={intl.formatMessage({ id: 'SorteringVelger.Fra' })}
              validate={[hasValidPosOrNegInteger]}
              onBlurValidation
              bredde="XS"
            />

          </FlexColumn>
          <FlexColumn>
            <Undertekst className={styles.beløp}>
              <FormattedMessage id="SorteringVelger.Valuta" />
            </Undertekst>
          </FlexColumn>
          <FlexColumn>
            <InputField
              name="til"
              className={styles.dato}
              placeholder={intl.formatMessage({ id: 'SorteringVelger.Til' })}
              validate={[hasValidPosOrNegInteger]}
              onBlurValidation
              bredde="XS"
            />
          </FlexColumn>
          <FlexColumn>
            <Undertekst className={styles.beløp}>
              <FormattedMessage id="SorteringVelger.Valuta" />
            </Undertekst>
          </FlexColumn>
        </FlexRow>
      </FlexContainer>
    </>
    <VerticalSpacer eightPx />
  </ArrowBox>
);
export default BelopSorteringValg;
