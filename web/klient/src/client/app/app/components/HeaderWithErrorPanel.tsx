import React, {
  useState, useEffect, FunctionComponent, useRef, useCallback,
} from 'react';
import { injectIntl, WrappedComponentProps } from 'react-intl';
import Popover from '@navikt/nap-popover';
import SystemButton from '@navikt/nap-system-button';
import UserPanel from '@navikt/nap-user-panel';
import BoxedListWithSelection from '@navikt/boxed-list-with-selection';
import BoxedListWithLinks from '@navikt/boxed-list-with-links';
import Header from '@navikt/nap-header';

import { getValueFromLocalStorage, setValueInLocalStorage, removeValueFromLocalStorage } from 'utils/localStorageHelper';
import { Avdeling } from 'app/avdelingTsType';

import { RETTSKILDE_URL, SYSTEMRUTINE_URL } from 'data/eksterneLenker';

import ErrorMessagePanel from './ErrorMessagePanel';

import styles from './headerWithErrorPanel.less';

interface OwnProps {
  navAnsattName: string;
  removeErrorMessage: () => void;
  queryStrings: {
    errormessage?: string;
    errorcode?: string;
  };
  avdelinger: Avdeling[];
  setValgtAvdeling: (id: string) => void;
  valgtAvdelingEnhet?: string;
  errorMessages?: {
    type: EventType;
    code?: string;
    params?: {
      errorDetails?: string;
    };
    text?: string;
  }[];
  setSiteHeight: (clientHeight: number) => number;
}

const useOutsideClickEvent = (erLenkepanelApent, erAvdelingerPanelApent, setLenkePanelApent, setAvdelingerPanelApent) => {
  const wrapperRef = useRef(null);
  const handleClickOutside = useCallback((event) => {
    if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
      setLenkePanelApent(false);
      setAvdelingerPanelApent(false);
    }
  }, [wrapperRef.current]);

  useEffect(() => {
    if (erLenkepanelApent || erAvdelingerPanelApent) {
      document.addEventListener('mousedown', handleClickOutside);
    } else {
      document.removeEventListener('mousedown', handleClickOutside);
    }
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [erLenkepanelApent, erAvdelingerPanelApent]);

  return wrapperRef;
};

const setAvdeling = (avdelinger, setValgtAvdeling, valgtAvdelingEnhet) => {
  if (avdelinger.length > 0 && !valgtAvdelingEnhet) {
    let valgtEnhet = avdelinger[0].avdelingEnhet;
    const lagretAvdelingEnhet = getValueFromLocalStorage('avdelingEnhet');
    if (lagretAvdelingEnhet) {
      if (avdelinger.some((a) => a.avdelingEnhet === lagretAvdelingEnhet)) {
        valgtEnhet = lagretAvdelingEnhet;
      } else {
        removeValueFromLocalStorage('avdelingEnhet');
      }
    }
    setValgtAvdeling(valgtEnhet);
  }
};

/**
 * HeaderWithErrorPanel
 *
 * Presentasjonskomponent. Definerer header-linjen som alltid vises øverst nettleservinduet.
 * Denne viser lenke tilbake til hovedsiden, nettside-navnet og NAV-ansatt navn.
 * I tillegg vil den vise potensielle feilmeldinger i ErrorMessagePanel.
 */
const HeaderWithErrorPanel: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  navAnsattName,
  removeErrorMessage,
  queryStrings,
  avdelinger,
  valgtAvdelingEnhet,
  setValgtAvdeling,
  errorMessages,
  setSiteHeight,
}) => {
  const [erLenkePanelApent, setLenkePanelApent] = useState(false);
  const [erAvdelingerPanelApent, setAvdelingerPanelApent] = useState(false);

  const wrapperRef = useOutsideClickEvent(erLenkePanelApent, erAvdelingerPanelApent, setLenkePanelApent, setAvdelingerPanelApent);

  const fixedHeaderRef = useRef(null);
  useEffect(() => {
    setSiteHeight(fixedHeaderRef.current.clientHeight);
  }, [errorMessages.length]);

  useEffect(() => {
    setAvdeling(avdelinger, setValgtAvdeling, valgtAvdelingEnhet);
  }, [avdelinger]);

  let brukerPanel = <UserPanel name={navAnsattName} />;

  if (valgtAvdelingEnhet && avdelinger.length > 0) {
    brukerPanel = (
      <Popover
        popperIsVisible={erAvdelingerPanelApent}
        renderArrowElement
        customPopperStyles={{ top: '11px', zIndex: 1 }}
        arrowProps={{ style: { right: '17px' } }}
        popperProps={{
          children: () => (
            <BoxedListWithSelection
              onClick={(index) => {
                setValueInLocalStorage('avdelingEnhet', avdelinger[index].avdelingEnhet);
                setValgtAvdeling(avdelinger[index].avdelingEnhet);
                setAvdelingerPanelApent(false);
              }}
              items={avdelinger.map((avdeling) => ({
                name: `${avdeling.avdelingEnhet} ${avdeling.navn}`,
                selected: valgtAvdelingEnhet === avdeling.avdelingEnhet,
              }))}
            />
          ),
          placement: 'bottom-start',
          positionFixed: true,
        }}
        referenceProps={{
          // eslint-disable-next-line react/prop-types
          children: ({ ref }) => (
            <div ref={ref}>
              <UserPanel
                name={navAnsattName}
                unit={`${valgtAvdelingEnhet} ${avdelinger.find((a) => a.avdelingEnhet === valgtAvdelingEnhet).navn}`}
                onClick={() => {
                  if (erLenkePanelApent) {
                    setLenkePanelApent(false);
                  }
                  setAvdelingerPanelApent(!erAvdelingerPanelApent);
                }}
              />
            </div>
          ),
        }}
      />
    );
  }

  return (
    <header ref={fixedHeaderRef} className={styles.container}>
      <div ref={wrapperRef}>
        <Header title={intl.formatMessage({ id: 'Header.Foreldrepenger' })}>
          <Popover
            popperIsVisible={erLenkePanelApent}
            renderArrowElement
            customPopperStyles={{ top: '11px', zIndex: 1 }}
            popperProps={{
              children: () => (
                <BoxedListWithLinks
                  onClick={() => {
                    setLenkePanelApent(false);
                  }}
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
              ),
              placement: 'bottom-start',
              positionFixed: true,
            }}
            referenceProps={{
              // eslint-disable-next-line react/prop-types
              children: ({ ref }) => (
                <div ref={ref}>
                  <SystemButton
                    onClick={() => {
                      if (erAvdelingerPanelApent) {
                        setAvdelingerPanelApent(false);
                      }
                      setLenkePanelApent(!erLenkePanelApent);
                    }}
                    isToggled={erLenkePanelApent}
                  />
                </div>
              ),
            }}
          />
          {brukerPanel}
        </Header>
      </div>
      <ErrorMessagePanel queryStrings={queryStrings} removeErrorMessage={removeErrorMessage} />
    </header>
  );
};

HeaderWithErrorPanel.defaultProps = {
  avdelinger: [],
  valgtAvdelingEnhet: undefined,
};


export default injectIntl(HeaderWithErrorPanel);
