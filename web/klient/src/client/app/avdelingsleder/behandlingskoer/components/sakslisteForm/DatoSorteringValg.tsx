import React, { FunctionComponent } from 'react';
import { FormattedMessage, WrappedComponentProps } from 'react-intl';
import moment from 'moment';
import { Undertekst } from 'nav-frontend-typografi';

import { FlexColumn, FlexContainer, FlexRow } from 'sharedComponents/flexGrid';
import { hasValidDate, hasValidPosOrNegInteger } from 'utils/validation/validators';
import DateLabel from 'sharedComponents/DateLabel';
import useRestApiRunner from 'data/rest-api-hooks/useRestApiRunner';
import { RestApiPathsKeys } from 'data/restApiPaths';
import {
  InputField, CheckboxField, DatepickerField,
} from 'form/FinalFields';
import { ISO_DATE_FORMAT, DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import ArrowBox from 'sharedComponents/ArrowBox';

import AutoLagringVedBlur from './AutoLagringVedBlur';

import styles from './sorteringVelger.less';

const finnDato = (antallDager) => moment().add(antallDager, 'd').format();
const getLagreDatoFn = (lagreSakslisteSorteringTidsintervallDato, hentAntallOppgaver, hentAvdelingensSakslister,
  valgtSakslisteId, valgtAvdelingEnhet, annenDato, erFomDato) => (e) => {
  let dato = e.target.value;
  if (dato) {
    dato = moment(dato, DDMMYYYY_DATE_FORMAT, true);
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
    fra: number;
    til: number;
    fomDato: string;
    tomDato: string;
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
  const { startRequest: lagreSakslisteSorteringErDynamiskPeriode } = useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE);
  const { startRequest: lagreSakslisteSorteringTidsintervallDato } = useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO);
  return (
    <ArrowBox>
      <Undertekst>
        <FormattedMessage id="SorteringVelger.FiltrerPaTidsintervall" />
      </Undertekst>

      {erDynamiskPeriode && (
      <>
        <AutoLagringVedBlur
          lagre={(values) => lagreSakslisteSorteringTidsintervallDager({
            sakslisteId: valgtSakslisteId, fra: values.fra, til: values.til, avdelingEnhet: valgtAvdelingEnhet,
          })
            .then(() => {
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
                label={intl.formatMessage({ id: 'SorteringVelger.Fom' })}
                validate={[hasValidPosOrNegInteger]}
                onBlurValidation
                bredde="XS"
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
                validate={[hasValidPosOrNegInteger]}
                onBlurValidation
                bredde="XS"
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
                label={{ id: 'SorteringVelger.Fom' }}
                onBlurValidation
                validate={[hasValidDate]}
                onBlur={getLagreDatoFn(lagreSakslisteSorteringTidsintervallDato, hentAntallOppgaver, hentAvdelingensSakslister,
                  valgtSakslisteId, valgtAvdelingEnhet, tomDato, true)}
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
                label={{ id: 'SorteringVelger.Tom' }}
                onBlurValidation
                validate={[hasValidDate]}
                onBlur={getLagreDatoFn(lagreSakslisteSorteringTidsintervallDato, hentAntallOppgaver, hentAvdelingensSakslister,
                  valgtSakslisteId, valgtAvdelingEnhet, fomDato, false)}
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
