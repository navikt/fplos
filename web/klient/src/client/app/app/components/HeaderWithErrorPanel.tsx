import React, {
  useState, useEffect, FunctionComponent, useRef, useCallback, useMemo,
} from 'react';
import { injectIntl, WrappedComponentProps } from 'react-intl';
import Popover from '@navikt/nap-popover';
import SystemButton from '@navikt/nap-system-button';
import BoxedListWithLinks from '@navikt/boxed-list-with-links';
import Header from '@navikt/nap-header';
import UserPanel from '@navikt/nap-user-panel';

import { useRestApiError, useRestApiErrorDispatcher } from 'data/rest-api-hooks';
import { restApiHooks, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { RETTSKILDE_URL, SYSTEMRUTINE_URL } from 'data/eksterneLenker';
import NavAnsatt from 'types/navAnsattTsType';
import Driftsmelding from 'types/driftsmeldingTsType';

import { AVDELINGSLEDER_PATH, BASE_PATH } from '../paths';
import ErrorMessagePanel from './ErrorMessagePanel';
import HeaderAvdelingListe from './HeaderAvdelingListe';

import styles from './headerWithErrorPanel.less';
import ErrorFormatter from '../feilhandtering/ErrorFormatter';
import DriftsmeldingPanel from './DriftsmeldingPanel';

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

const useOutsideClickEvent = (
  erLenkepanelApent: boolean,
  erAvdelingerPanelApent: boolean,
  setLenkePanelApent: (erApent: boolean) => void,
  setAvdelingerPanelApent: (erApent: boolean) => void,
) => {
  const wrapperRef = useRef<HTMLDivElement>(null);
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

  const navAnsatt = restApiHooks.useGlobalStateRestApiData<NavAnsatt>(RestApiGlobalStatePathsKeys.NAV_ANSATT);
  const driftsmeldinger = restApiHooks.useGlobalStateRestApiData<Driftsmelding[]>(RestApiGlobalStatePathsKeys.DRIFTSMELDINGER);

  const errorMessages = useRestApiError() || [];
  const formaterteFeilmeldinger = useMemo(() => new ErrorFormatter().format(errorMessages, crashMessage), [errorMessages]);
  const { removeErrorMessages } = useRestApiErrorDispatcher();

  const wrapperRef = useOutsideClickEvent(erLenkePanelApent, erAvdelingerPanelApent, setLenkePanelApent, setAvdelingerPanelApent);

  const fixedHeaderRef = useRef<HTMLDivElement>(null);
  useEffect(() => {
    if (fixedHeaderRef?.current) {
      setSiteHeight(fixedHeaderRef.current.clientHeight);
    }
  }, [formaterteFeilmeldinger.length]);

  const kanOppgavestyre = navAnsatt && navAnsatt.kanOppgavestyre;
  const skalViseAvdelinger = kanOppgavestyre
    && locationPathname && locationPathname.includes(AVDELINGSLEDER_PATH);

  const popoverItems = useMemo(() => {
    const items = [{
      name: intl.formatMessage({ id: 'Header.Rettskilde' }),
      href: RETTSKILDE_URL,
      isExternal: true,
    }, {
      name: intl.formatMessage({ id: 'Header.Systemrutine' }),
      href: SYSTEMRUTINE_URL,
      isExternal: true,
    }];
    if (kanOppgavestyre && locationPathname && !locationPathname.includes(AVDELINGSLEDER_PATH)) {
      items.push({
        name: 'Avdelingsleder',
        href: [BASE_PATH, AVDELINGSLEDER_PATH].join('/'),
        isExternal: false,
      });
    }
    return items;
  }, [kanOppgavestyre, locationPathname]);

  return (
    <header ref={fixedHeaderRef} className={styles.container}>
      <div ref={wrapperRef}>
        <Header
          title={intl.formatMessage({ id: 'Header.Foreldrepenger' })}
          titleHref={BASE_PATH}
        >
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
                  items={popoverItems}
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
          {!skalViseAvdelinger && navAnsatt && (
            <UserPanel name={navAnsatt.navn} />
          )}
        </Header>
      </div>
      <DriftsmeldingPanel
        driftsmeldinger={driftsmeldinger}
      />
      <ErrorMessagePanel
        // @ts-ignore Fiks denne
        errorMessages={formaterteFeilmeldinger}
        queryStrings={queryStrings}
        removeErrorMessages={removeErrorMessages}
      />
    </header>
  );
};

export default injectIntl(HeaderWithErrorPanel);
