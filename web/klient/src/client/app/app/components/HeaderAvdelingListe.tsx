import React, {
  useEffect, FunctionComponent,
} from 'react';
import Popover from '@navikt/nap-popover';
import UserPanel from '@navikt/nap-user-panel';
import BoxedListWithSelection from '@navikt/boxed-list-with-selection';

import { getValueFromLocalStorage, setValueInLocalStorage, removeValueFromLocalStorage } from 'utils/localStorageHelper';
import Avdeling from 'app/avdelingTsType';
import useRestApiData from 'data/rest-api-hooks/useGlobalStateRestApiData';
import useGlobalStateRestApi from 'data/rest-api-hooks/useGlobalStateRestApi';
import { RestApiGlobalStatePathsKeys } from 'data/restApiPaths';

import NavAnsatt from 'app/navAnsattTsType';

interface OwnProps {
  erLenkePanelApent: boolean;
  setLenkePanelApent: (apent: boolean) => void;
  erAvdelingerPanelApent: boolean;
  setAvdelingerPanelApent: (apent: boolean) => void;
  setValgtAvdelingEnhet: (avdelingEnhet: string) => void;
  valgtAvdelingEnhet?: string;
}

const setAvdeling = (avdelinger, setValgtAvdeling, valgtAvdelingEnhet) => {
  if (avdelinger && avdelinger.length > 0 && !valgtAvdelingEnhet) {
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

const HeaderAvdelingListe: FunctionComponent<OwnProps> = ({
  erLenkePanelApent,
  setLenkePanelApent,
  erAvdelingerPanelApent,
  setAvdelingerPanelApent,
  setValgtAvdelingEnhet,
  valgtAvdelingEnhet,
}) => {
  const { data: avdelinger } = useGlobalStateRestApi<Avdeling[]>(RestApiGlobalStatePathsKeys.AVDELINGER);

  const navAnsatt = useRestApiData<NavAnsatt>(RestApiGlobalStatePathsKeys.NAV_ANSATT);

  useEffect(() => {
    setAvdeling(avdelinger, setValgtAvdelingEnhet, valgtAvdelingEnhet);
  }, [avdelinger]);

  if (valgtAvdelingEnhet && avdelinger && avdelinger.length > 0) {
    return (
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
                setValgtAvdelingEnhet(avdelinger[index].avdelingEnhet);
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
                name={navAnsatt.navn}
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
  return null;
};

export default HeaderAvdelingListe;
