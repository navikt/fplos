import DatoSorteringValg from 'avdelingsleder/behandlingskoer/components/sakslisteForm/DatoSorteringValg';
import { Undertekst } from 'nav-frontend-typografi';
import { FormattedMessage } from 'react-intl';
import ArrowBox from 'sharedComponents/ArrowBox';
import React from 'react';
import AutoLagringVedBlur from 'avdelingsleder/behandlingskoer/components/sakslisteForm/AutoLagringVedBlur';
import { FlexColumn, FlexContainer, FlexRow } from 'sharedComponents/flexGrid';
import { InputField } from 'form/FinalFields';
import styles from 'avdelingsleder/behandlingskoer/components/sakslisteForm/sorteringVelger.less';
import { hasValidPosOrNegInteger } from 'utils/validation/validators';
import DateLabel from 'sharedComponents/DateLabel';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';

interface TsProps {
    intl: any;
    valgtSakslisteId: number;
    lagreSakslisteSorteringNumerisk: (sakslisteId: number, fom: number, tom: number, avdelingEnhet: string) => void;
    valgtAvdelingEnhet: string;
    fom: number;
    tom: number;
}

export const BeløpSorteringValg = ({
    intl,
    valgtSakslisteId,
    valgtAvdelingEnhet,
    lagreSakslisteSorteringNumerisk,
    fom,
    tom,
}: TsProps) => (
  <ArrowBox>
    <Undertekst>
      <FormattedMessage id="SorteringVelger.FiltrerPaHeltall" />
    </Undertekst>

    <>
      <AutoLagringVedBlur
        lagre={values => lagreSakslisteSorteringNumerisk(valgtSakslisteId, values.fom, values.tom, valgtAvdelingEnhet)}
        fieldNames={['fom', 'tom']}
      />
      <FlexContainer>
        <FlexRow>
          <FlexColumn>
            <InputField
              name="fom"
              className={styles.dato}
              label={intl.formatMessage({ id: 'SorteringVelger.Fom' })}
              validate={[hasValidPosOrNegInteger]}
              onBlurValidation
              bredde="XS"
            />

          </FlexColumn>
          <FlexColumn>
            <Undertekst className={styles.dager}>
              <FormattedMessage id="SorteringVelger.Valuta" />
            </Undertekst>
          </FlexColumn>
          <FlexColumn>
            <InputField
              name="tom"
              className={styles.dato}
              label={intl.formatMessage({ id: 'SorteringVelger.Tom' })}
              validate={[hasValidPosOrNegInteger]}
              onBlurValidation
              bredde="XS"
            />
          </FlexColumn>
          <FlexColumn>
            <Undertekst className={styles.dagerMedBindestrek}>
              <FormattedMessage id="SorteringVelger.Valuta" />
            </Undertekst>
          </FlexColumn>
        </FlexRow>
      </FlexContainer>
    </>
    <VerticalSpacer eightPx />
  </ArrowBox>
);
export default BeløpSorteringValg;
