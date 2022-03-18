import { Undertekst } from 'nav-frontend-typografi';
import { FormattedMessage, WrappedComponentProps } from 'react-intl';
import ArrowBox from 'sharedComponents/ArrowBox';
import React, { FunctionComponent } from 'react';
import { FlexColumn, FlexContainer, FlexRow } from 'sharedComponents/flexGrid';
import { hasValidPosOrNegInteger } from 'utils/validation/validators';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { InputField } from 'form/formIndex';
import useDebounce from 'form/useDebounce';

import { useFormContext } from 'react-hook-form';
import styles from './sorteringVelger.less';

interface OwnProps {
  valgtSakslisteId: number;
  lagreSakslisteSorteringNumerisk: (params: {sakslisteId: number, fra: number, til: number, avdelingEnhet: string}) => Promise<any>;
  valgtAvdelingEnhet: string;
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
}) => {
  const { watch } = useFormContext();
  const fraVerdi = watch('fra');
  const tilVerdi = watch('til');

  const lagreFra = (nyFraVerdi: number) => lagreSakslisteSorteringNumerisk({
    sakslisteId: valgtSakslisteId, fra: nyFraVerdi, til: tilVerdi, avdelingEnhet: valgtAvdelingEnhet,
  }).then(() => {
    hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
    hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
  });
  const lagreTil = (nyTilVerdi: number) => lagreSakslisteSorteringNumerisk({
    sakslisteId: valgtSakslisteId, fra: fraVerdi, til: nyTilVerdi, avdelingEnhet: valgtAvdelingEnhet,
  }).then(() => {
    hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
    hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
  });

  const lagreFraDebounce = useDebounce<number>('fra', lagreFra);
  const lagreTilDebounce = useDebounce<number>('til', lagreTil);

  return (
    <ArrowBox>
      <Undertekst>
        <FormattedMessage id="SorteringVelger.FiltrerPaHeltall" />
      </Undertekst>
      <FlexContainer>
        <FlexRow>
          <FlexColumn>
            <InputField
              name="fra"
              className={styles.dato}
              placeholder={intl.formatMessage({ id: 'SorteringVelger.Fra' })}
              validate={[hasValidPosOrNegInteger(intl)]}
              bredde="XS"
              onChange={lagreFraDebounce}
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
              validate={[hasValidPosOrNegInteger(intl)]}
              bredde="XS"
              onChange={lagreTilDebounce}
            />
          </FlexColumn>
          <FlexColumn>
            <Undertekst className={styles.beløp}>
              <FormattedMessage id="SorteringVelger.Valuta" />
            </Undertekst>
          </FlexColumn>
        </FlexRow>
      </FlexContainer>
      <VerticalSpacer eightPx />
    </ArrowBox>
  );
};
export default BelopSorteringValg;
