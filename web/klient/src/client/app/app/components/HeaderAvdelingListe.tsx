import React, {
  useEffect, FunctionComponent,
} from 'react';
import {
  BoxedListWithSelection, Popover, UserPanel,
} from '@navikt/fp-react-components';

import { getValueFromLocalStorage, setValueInLocalStorage, removeValueFromLocalStorage } from 'utils/localStorageHelper';
import Avdeling from 'types/avdelingsleder/avdelingTsType';
import { restApiHooks, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';

interface OwnProps {
  erLenkePanelApent: boolean;
  setLenkePanelApent: (apent: boolean) => void;
  erAvdelingerPanelApent: boolean;
  setAvdelingerPanelApent: (apent: boolean) => void;
  setValgtAvdelingEnhet: (avdelingEnhet: string) => void;
  valgtAvdelingEnhet?: string;
}

const setAvdeling = (
  setValgtAvdeling: (avdelingEnhet: string) => void,
  avdelinger?: Avdeling[],
  valgtAvdelingEnhet?: string,
) => {
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
  const { data: avdelinger } = restApiHooks.useGlobalStateRestApi(RestApiGlobalStatePathsKeys.AVDELINGER);

  const navAnsatt = restApiHooks.useGlobalStateRestApiData(RestApiGlobalStatePathsKeys.NAV_ANSATT);

  useEffect(() => {
    setAvdeling(setValgtAvdelingEnhet, avdelinger, valgtAvdelingEnhet);
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
          strategy: 'fixed',
        }}
        referenceProps={{
          // eslint-disable-next-line react/prop-types
          children: ({ ref }) => (
            <div ref={ref}>
              <UserPanel
                name={navAnsatt.navn}
                unit={`${valgtAvdelingEnhet} ${avdelinger.find((a) => a.avdelingEnhet === valgtAvdelingEnhet)?.navn}`}
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
