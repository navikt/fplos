import React, { FunctionComponent } from 'react';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';

import { Form } from 'react-final-form';
import { Knapp } from 'nav-frontend-knapper';
import { Undertittel } from 'nav-frontend-typografi';

import { FlexContainer, FlexRow, FlexColumn } from 'sharedComponents/flexGrid';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import Image from 'sharedComponents/Image';
import advarselIcon from 'images/advarsel.svg';
import { hasValidSaksnummerOrFodselsnummerFormat } from 'utils/validation/validators';
import { InputField, CheckboxField } from 'form/FinalFields';

import useRestApiData from 'data/useRestApiData';
import { fpLosApiKeys } from 'data/fpLosApi';
import NavAnsatt from 'app/navAnsattTsType';

import styles from './searchForm.less';

const isButtonDisabled = (searchString, searchStarted, searchResultAccessDenied) => (!searchResultAccessDenied.feilmelding && searchStarted) || !searchString;

interface OwnProps {
  onSubmit: ({ searchString: string, skalReservere: boolean }) => void;
  searchStarted: boolean;
  searchResultAccessDenied?: {
    feilmelding?: string;
  };
  resetSearch: () => void;
}

/**
 * SearchForm
 *
 * Presentasjonskomponent. Definerer søkefelt og tilhørende søkeknapp.
 */
const SearchForm: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  onSubmit,
  searchStarted,
  searchResultAccessDenied,
  resetSearch,
}) => {
  const { kanSaksbehandle } = useRestApiData<NavAnsatt>(fpLosApiKeys.NAV_ANSATT);
  return (
    <Form
      onSubmit={onSubmit}
      render={({ handleSubmit, values }) => (
        <form className={styles.container} onSubmit={handleSubmit}>
          <Undertittel>{intl.formatMessage({ id: 'Search.SearchFagsakOrPerson' })}</Undertittel>
          {kanSaksbehandle && (
          <>
            <VerticalSpacer sixteenPx />
            <CheckboxField name="skalReservere" label={intl.formatMessage({ id: 'Search.ReserverBehandling' })} onClick={resetSearch} />
          </>
          )}
          <VerticalSpacer eightPx />
          <FlexContainer>
            <FlexRow>
              <FlexColumn>
                <InputField
                  name="searchString"
                  parse={(s = '') => s.trim()}
                  label={intl.formatMessage({ id: 'Search.SaksnummerOrPersonId' })}
                  bredde="L"
                  validate={[hasValidSaksnummerOrFodselsnummerFormat]}
                />
              </FlexColumn>
              <FlexColumn>
                <Knapp
                  mini
                  htmlType="submit"
                  className={styles.button}
                  spinner={!searchResultAccessDenied.feilmelding && searchStarted}
                  disabled={isButtonDisabled(values.searchString, searchStarted, searchResultAccessDenied)}
                >
                  <FormattedMessage id="Search.Search" />
                </Knapp>
              </FlexColumn>
            </FlexRow>
            {searchResultAccessDenied.feilmelding && (
            <>
              <VerticalSpacer eightPx />
              <FlexRow>
                <FlexColumn>
                  <Image className={styles.advarselIcon} src={advarselIcon} />
                </FlexColumn>
                <FlexColumn>
                  <FormattedMessage id={searchResultAccessDenied.feilmelding} />
                </FlexColumn>
              </FlexRow>
            </>
            )}
          </FlexContainer>
        </form>
      )}
    />
  );
};

SearchForm.defaultProps = {
  searchResultAccessDenied: {
    feilmelding: undefined,
  },
};

export default injectIntl(SearchForm);
