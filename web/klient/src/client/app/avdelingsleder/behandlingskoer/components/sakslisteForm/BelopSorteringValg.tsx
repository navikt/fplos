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
  lagreSakslisteSorteringNumerisk: (params: {sakslisteId: number, fra: number, til: number, avdelingEnhet: string}) => Promise<any>;
  valgtAvdelingEnhet: string;
  fra: number;
  til: number;
  hentAvdelingensSakslister: (params: {avdelingEnhet: string}) => void;
  hentAntallOppgaver: (sakslisteId: number, avdelingEnhet: string) => void;
}

export const BelopSorteringValg: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  valgtSakslisteId,
  valgtAvdelingEnhet,
  lagreSakslisteSorteringNumerisk,
  hentAvdelingensSakslister,
  hentAntallOppgaver,
}) => (
  <ArrowBox>
    <Undertekst>
      <FormattedMessage id="SorteringVelger.FiltrerPaHeltall" />
    </Undertekst>
    <>
      <AutoLagringVedBlur
        lagre={(values) => lagreSakslisteSorteringNumerisk({
          sakslisteId: valgtSakslisteId, fra: values.fra, til: values.til, avdelingEnhet: valgtAvdelingEnhet,
        }).then(() => {
          hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
          hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
        })}
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
