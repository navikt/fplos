import React, { FunctionComponent } from 'react';
import { FormattedMessage, WrappedComponentProps } from 'react-intl';
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import { useFormContext } from 'react-hook-form';
import { Undertekst } from 'nav-frontend-typografi';

import { FlexColumn, FlexContainer, FlexRow } from 'sharedComponents/flexGrid';
import { hasValidDate, hasValidPosOrNegInteger } from 'utils/validation/validators';
import DateLabel from 'sharedComponents/DateLabel';
import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import { ISO_DATE_FORMAT, DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import ArrowBox from 'sharedComponents/ArrowBox';
import { InputField, CheckboxField, DatepickerField } from 'form/formIndex';

import styles from './sorteringVelger.less';

dayjs.extend(customParseFormat);

const finnDato = (antallDager: number) => dayjs().add(antallDager, 'd').format();

const getLagreDatoFn = (
  lagreSakslisteSorteringTidsintervallDato: (params?: any, keepData?: boolean | undefined) => Promise<unknown>,
  hentAntallOppgaver: (sakslisteId: number, avdelingEnhet: string) => void,
  hentAvdelingensSakslister: (params: {avdelingEnhet: string}) => void,
  valgtSakslisteId: number,
  valgtAvdelingEnhet: string,
  erFomDato: boolean,
  annenDato?: string,
) => (e: any) => {
  let dato = e.target.value;
  if (dato) {
    dato = dayjs(dato, DDMMYYYY_DATE_FORMAT, true);
  }
  if (!dato || dato.isValid()) {
    const d = dato ? dato.format(ISO_DATE_FORMAT) : dato;

    const params = erFomDato ? {
      sakslisteId: valgtSakslisteId,
      avdelingEnhet: valgtAvdelingEnhet,
      fomDato: d,
      tomDato: annenDato,
    } : {
      sakslisteId: valgtSakslisteId,
      avdelingEnhet: valgtAvdelingEnhet,
      fomDato: annenDato,
      tomDato: d,
    };

    return lagreSakslisteSorteringTidsintervallDato(params)
      .then(() => {
        hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
        hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
      });
  }
  return undefined;
};
interface OwnProps {
  valgtSakslisteId: number;
  lagreSakslisteSorteringTidsintervallDager: (params: {sakslisteId: number, fra: number, til: number, avdelingEnhet: string}) => Promise<any>;
  valgtAvdelingEnhet: string;
  erDynamiskPeriode: boolean;
  fra?: number;
  til?: number;
  fomDato?: string;
  tomDato?: string;
  hentAvdelingensSakslister: (params: {avdelingEnhet: string}) => void;
  hentAntallOppgaver: (sakslisteId: number, avdelingEnhet: string) => void;
}

export const DatoSorteringValg: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  valgtSakslisteId,
  valgtAvdelingEnhet,
  erDynamiskPeriode,
  lagreSakslisteSorteringTidsintervallDager,
  fra,
  til,
  fomDato,
  tomDato,
  hentAvdelingensSakslister,
  hentAntallOppgaver,
}) => {
  const { startRequest: lagreSakslisteSorteringErDynamiskPeriode } = restApiHooks
    .useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE);
  const { startRequest: lagreSakslisteSorteringTidsintervallDato } = restApiHooks
    .useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO);

  const { watch } = useFormContext();
  const fraVerdi = watch('fra');
  const tilVerdi = watch('til');

  const lagreFra = (nyFraVerdi: number) => lagreSakslisteSorteringTidsintervallDager({
    sakslisteId: valgtSakslisteId, fra: nyFraVerdi, til: tilVerdi, avdelingEnhet: valgtAvdelingEnhet,
  })
    .then(() => {
      hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
      hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
    });
  const lagreTil = (nyTilVerdi: number) => lagreSakslisteSorteringTidsintervallDager({
    sakslisteId: valgtSakslisteId, fra: fraVerdi, til: nyTilVerdi, avdelingEnhet: valgtAvdelingEnhet,
  })
    .then(() => {
      hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
      hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
    });

  return (
    <ArrowBox>
      <Undertekst>
        <FormattedMessage id="SorteringVelger.FiltrerPaTidsintervall" />
      </Undertekst>

      {erDynamiskPeriode && (
      <>
        <FlexContainer>
          <FlexRow>
            <FlexColumn>
              <InputField
                name="fra"
                className={styles.dato}
                label={intl.formatMessage({ id: 'SorteringVelger.Fom' })}
                validate={[hasValidPosOrNegInteger(intl)]}
                bredde="XS"
                onBlur={lagreFra}
                shouldValidateOnBlur
              />
              {(fra || fra === 0) && (
              <Undertekst>
                <DateLabel dateString={finnDato(fra)} />
              </Undertekst>
              )}
            </FlexColumn>
            <FlexColumn>
              <Undertekst className={styles.dager}>
                <FormattedMessage id="SorteringVelger.DagerMedBindestrek" />
              </Undertekst>
            </FlexColumn>
            <FlexColumn>
              <InputField
                name="til"
                className={styles.dato}
                label={intl.formatMessage({ id: 'SorteringVelger.Tom' })}
                validate={[hasValidPosOrNegInteger(intl)]}
                bredde="XS"
                onBlur={lagreTil}
                shouldValidateOnBlur
              />
              {(til || til === 0) && (
              <Undertekst>
                <DateLabel dateString={finnDato(til)} />
              </Undertekst>
              )}
            </FlexColumn>
            <FlexColumn>
              <Undertekst className={styles.dagerMedBindestrek}>
                <FormattedMessage id="SorteringVelger.Dager" />
              </Undertekst>
            </FlexColumn>
          </FlexRow>
        </FlexContainer>
      </>
      )}
      {!erDynamiskPeriode && (
      <>
        <FlexContainer>
          <FlexRow>
            <FlexColumn>
              <DatepickerField
                name="fomDato"
                label={intl.formatMessage({ id: 'SorteringVelger.Fom' })}
                shouldValidateOnBlur
                validate={[hasValidDate(intl)]}
                onBlur={getLagreDatoFn(lagreSakslisteSorteringTidsintervallDato, hentAntallOppgaver, hentAvdelingensSakslister,
                  valgtSakslisteId, valgtAvdelingEnhet, true, tomDato)}
              />
            </FlexColumn>
            <FlexColumn>
              <Undertekst className={styles.dager}>
                <FormattedMessage id="SorteringVelger.Bindestrek" />
              </Undertekst>
            </FlexColumn>
            <FlexColumn className={styles.tomDato}>
              <DatepickerField
                name="tomDato"
                label={intl.formatMessage({ id: 'SorteringVelger.Tom' })}
                shouldValidateOnBlur
                validate={[hasValidDate(intl)]}
                onBlur={getLagreDatoFn(lagreSakslisteSorteringTidsintervallDato, hentAntallOppgaver, hentAvdelingensSakslister,
                  valgtSakslisteId, valgtAvdelingEnhet, false, fomDato)}
              />
            </FlexColumn>
          </FlexRow>
        </FlexContainer>
      </>
      )}
      <CheckboxField
        name="erDynamiskPeriode"
        label={intl.formatMessage({ id: 'SorteringVelger.DynamiskPeriode' })}
        onChange={() => lagreSakslisteSorteringErDynamiskPeriode({ sakslisteId: valgtSakslisteId, avdelingEnhet: valgtAvdelingEnhet })
          .then(() => {
            hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
            hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
          })}
      />
      <VerticalSpacer eightPx />
    </ArrowBox>
  );
};

export default DatoSorteringValg;
