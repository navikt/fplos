import React, {
  useState, useEffect, FunctionComponent, useRef, useCallback, useMemo,
} from 'react';
import { injectIntl, WrappedComponentProps } from 'react-intl';
import Popover from '@navikt/nap-popover';
import SystemButton from '@navikt/nap-system-button';
import BoxedListWithLinks from '@navikt/boxed-list-with-links';
import Header from '@navikt/nap-header';
import UserPanel from '@navikt/nap-user-panel';

import useRestApiData from 'data/rest-api-hooks/useGlobalStateRestApiData';
import useRestApiError from 'data/rest-api-hooks/useRestApiError';
import useRestApiErrorDispatcher from 'data/rest-api-hooks/useRestApiErrorDispatcher';
import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';
import { RETTSKILDE_URL, SYSTEMRUTINE_URL } from 'data/eksterneLenker';
import NavAnsatt from 'app/navAnsattTsType';

import { AVDELINGSLEDER_PATH } from '../paths';
import ErrorMessagePanel from './ErrorMessagePanel';
import HeaderAvdelingListe from './HeaderAvdelingListe';

import styles from './headerWithErrorPanel.less';
import ErrorFormatter from '../feilhandtering/ErrorFormatter';

interface OwnProps {
  queryStrings: {
    errormessage?: string;
    errorcode?: string;
  };
  setSiteHeight: (clientHeight: number) => void;
  locationPathname?: string;
  setValgtAvdelingEnhet: (avdelingEnhet: string) => void;
  valgtAvdelingEnhet?: string;
  crashMessage?: string;
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

/**
 * HeaderWithErrorPanel
 *
 * Presentasjonskomponent. Definerer header-linjen som alltid vises øverst nettleservinduet.
 * Denne viser lenke tilbake til hovedsiden, nettside-navnet og NAV-ansatt navn.
 * I tillegg vil den vise potensielle feilmeldinger i ErrorMessagePanel.
 */
const HeaderWithErrorPanel: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  queryStrings,
  setSiteHeight,
  locationPathname,
  setValgtAvdelingEnhet,
  valgtAvdelingEnhet,
  crashMessage,
}) => {
  const [erLenkePanelApent, setLenkePanelApent] = useState(false);
  const [erAvdelingerPanelApent, setAvdelingerPanelApent] = useState(false);

  const navAnsatt = useRestApiData<NavAnsatt>(RestApiGlobalStatePathsKeys.NAV_ANSATT);

  const errorMessages = useRestApiError() || [];
  const formaterteFeilmeldinger = useMemo(() => new ErrorFormatter().format(errorMessages, crashMessage), [errorMessages]);
  const errorDispatcher = useRestApiErrorDispatcher();
  const removeErrorMessage = useCallback(() => errorDispatcher({ type: 'remove' }), []);

  const wrapperRef = useOutsideClickEvent(erLenkePanelApent, erAvdelingerPanelApent, setLenkePanelApent, setAvdelingerPanelApent);

  const fixedHeaderRef = useRef(null);
  useEffect(() => {
    setSiteHeight(fixedHeaderRef.current.clientHeight);
  }, [formaterteFeilmeldinger.length]);

  const skalViseAvdelinger = navAnsatt.kanOppgavestyre
    && locationPathname && locationPathname.includes(AVDELINGSLEDER_PATH);

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
          {skalViseAvdelinger && (
            <HeaderAvdelingListe
              erLenkePanelApent={erLenkePanelApent}
              setLenkePanelApent={setLenkePanelApent}
              erAvdelingerPanelApent={erAvdelingerPanelApent}
              setAvdelingerPanelApent={setAvdelingerPanelApent}
              valgtAvdelingEnhet={valgtAvdelingEnhet}
              setValgtAvdelingEnhet={setValgtAvdelingEnhet}
            />
          )}
          {!skalViseAvdelinger && (
            <UserPanel name={navAnsatt.navn} />
          )}
        </Header>
      </div>
      <ErrorMessagePanel errorMessages={formaterteFeilmeldinger} queryStrings={queryStrings} removeErrorMessage={removeErrorMessage} />
    </header>
  );
};

export default injectIntl(HeaderWithErrorPanel);
