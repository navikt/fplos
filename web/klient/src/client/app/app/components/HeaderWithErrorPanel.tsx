import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { injectIntl, intlShape } from 'react-intl';
import BoxedListWithLinks from '@navikt/boxed-list-with-links';
import BoxedListWithSelection from '@navikt/boxed-list-with-selection';
import Header from '@navikt/nap-header';

import { getValueFromLocalStorage, setValueInLocalStorage, removeValueFromLocalStorage } from 'utils/localStorageHelper';
import { Avdeling } from 'app/avdelingTsType';
import avdelingPropType from 'app/avdelingPropType';

import { RETTSKILDE_URL, SYSTEMRUTINE_URL } from 'data/eksterneLenker';

import ErrorMessagePanel from './ErrorMessagePanel';

import styles from './headerWithErrorPanel.less';

type TsProps = Readonly<{
  intl: any;
  navAnsattName: string;
  removeErrorMessage: () => void;
  queryStrings: {
    errormessage?: string;
    errorcode?: string;
  };
  avdelinger: Avdeling[];
  setValgtAvdeling: (id: string) => void;
  valgtAvdelingEnhet?: string;
}>

/**
 * HeaderWithErrorPanel
 *
 * Presentasjonskomponent. Definerer header-linjen som alltid vises øverst nettleservinduet.
 * Denne viser lenke tilbake til hovedsiden, nettside-navnet og NAV-ansatt navn.
 * I tillegg vil den vise potensielle feilmeldinger i ErrorMessagePanel.
 */
class HeaderWithErrorPanel extends Component<TsProps> {
  static propTypes = {
    intl: intlShape.isRequired,
    queryStrings: PropTypes.shape({
      errormessage: PropTypes.string,
      errorcode: PropTypes.string,
    }).isRequired,
    navAnsattName: PropTypes.string.isRequired,
    removeErrorMessage: PropTypes.func.isRequired,
    avdelinger: PropTypes.arrayOf(avdelingPropType),
    setValgtAvdeling: PropTypes.func.isRequired,
    valgtAvdelingEnhet: PropTypes.string,
  };

  static defaultProps = {
    avdelinger: [],
    valgtAvdelingEnhet: undefined,
  };

  setAvdeling = () => {
    const {
      avdelinger,
      setValgtAvdeling,
      valgtAvdelingEnhet,
    } = this.props;

    if (avdelinger.length > 0 && !valgtAvdelingEnhet) {
      let valgtEnhet = avdelinger[0].avdelingEnhet;
      const lagretAvdelingEnhet = getValueFromLocalStorage('avdelingEnhet');
      if (lagretAvdelingEnhet) {
        if (avdelinger.some(a => a.avdelingEnhet === lagretAvdelingEnhet)) {
          valgtEnhet = lagretAvdelingEnhet;
        } else {
          removeValueFromLocalStorage('avdelingEnhet');
        }
      }
      setValgtAvdeling(valgtEnhet);
    }
  }

  componentDidMount = () => {
    this.setAvdeling();
  }

  componentDidUpdate = () => {
    this.setAvdeling();
  }

  setValgtAvdeling = (avdelingEnhet: string) => {
    const {
      setValgtAvdeling,
    } = this.props;
    setValueInLocalStorage('avdelingEnhet', avdelingEnhet);
    setValgtAvdeling(avdelingEnhet);
  }

  render = () => {
    const {
      intl,
      navAnsattName,
      removeErrorMessage,
      queryStrings,
      avdelinger,
      valgtAvdelingEnhet,
    } = this.props;

    let props = {};
    if (valgtAvdelingEnhet && avdelinger.length > 0) {
      props = {
        renderUserPopoverContent: () => (
          <BoxedListWithSelection
            onClick={index => this.setValgtAvdeling(avdelinger[index].avdelingEnhet)}
            items={avdelinger.map(avdeling => ({
              name: `${avdeling.avdelingEnhet} ${avdeling.navn}`,
              selected: valgtAvdelingEnhet === avdeling.avdelingEnhet,
            }))}
          />
        ),
        userUnit: `${valgtAvdelingEnhet} ${avdelinger.find(a => a.avdelingEnhet === valgtAvdelingEnhet).navn}`,
      };
    }

    return (
      <header className={styles.container}>
        <Header
          title={intl.formatMessage({ id: 'Header.Foreldrepenger' })}
          userName={navAnsattName}
          renderLinksPopoverContent={() => (
            <BoxedListWithLinks
              items={[{
                name: intl.formatMessage({ id: 'Header.Rettskilde' }),
                href: RETTSKILDE_URL,
                isExternal: true,
              }, {
                name: intl.formatMessage({ id: 'Header.Systemrutine' }),
                href: SYSTEMRUTINE_URL,
                isExternal: true,
              }]}
            />
          )}
          {...props}
        />
        <ErrorMessagePanel queryStrings={queryStrings} removeErrorMessage={removeErrorMessage} />
      </header>
    );
  }
}

export default injectIntl(HeaderWithErrorPanel);
