import React, { FunctionComponent, useCallback, useEffect } from 'react';
import { injectIntl, FormattedMessage, WrappedComponentProps } from 'react-intl';
import { Row, Column } from 'nav-frontend-grid';

import { Image, VerticalSpacer } from '@navikt/ft-ui-komponenter';
import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import pilNedUrl from 'images/pil-ned.svg';
import Saksliste from 'types/avdelingsleder/sakslisteAvdelingTsType';
import Saksbehandler from 'types/avdelingsleder/saksbehandlerAvdelingTsType';
import GjeldendeSakslisterTabell from './GjeldendeSakslisterTabell';
import SaksbehandlereForSakslisteForm from './saksbehandlerForm/SaksbehandlereForSakslisteForm';
import UtvalgskriterierForSakslisteForm from './sakslisteForm/UtvalgskriterierForSakslisteForm';

import styles from './endreSakslisterPanel.less';

const EMPTY_ARRAY: Saksliste[] = [];

interface OwnProps {
  setValgtSakslisteId: (sakslisteId: number) => void;
  valgtSakslisteId?: number;
  valgtAvdelingEnhet: string;
  avdelingensSaksbehandlere: Saksbehandler[];
  resetValgtSakslisteId: () => void;
}

/**
 * EndreSakslisterPanel
 */
const EndreSakslisterPanel: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  setValgtSakslisteId,
  valgtSakslisteId,
  intl,
  valgtAvdelingEnhet,
  avdelingensSaksbehandlere,
  resetValgtSakslisteId,
}) => {
  const { data: oppgaverForAvdelingAntall, startRequest: hentOppgaverForAvdelingAntall } = restApiHooks
    .useRestApiRunner(RestApiPathsKeys.OPPGAVE_AVDELING_ANTALL);
  const { data: sakslister = EMPTY_ARRAY, startRequest: hentAvdelingensSl } = restApiHooks
    .useRestApiRunner(RestApiPathsKeys.SAKSLISTER_FOR_AVDELING);
  const hentAvdelingensSakslister = useCallback((params: { avdelingEnhet: string }) => hentAvdelingensSl(params, true), []);
  useEffect(() => {
    hentOppgaverForAvdelingAntall({ avdelingEnhet: valgtAvdelingEnhet });
    hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
  }, [valgtAvdelingEnhet]);

  const { data: nySakslisteObject, startRequest: lagNySaksliste } = restApiHooks
    .useRestApiRunner(RestApiPathsKeys.OPPRETT_NY_SAKSLISTE);
  const lagNySakslisteOgHentAvdelingensSakslisterPåNytt = useCallback((avdelingEnhet) => {
    lagNySaksliste({ avdelingEnhet }).then(() => {
      resetValgtSakslisteId();
      hentAvdelingensSakslister({ avdelingEnhet });
    });
  }, []);
  const nyId = nySakslisteObject ? parseInt(nySakslisteObject.sakslisteId, 10) : undefined;
  const valgtSakId = valgtSakslisteId !== undefined ? valgtSakslisteId : nyId;

  const valgtSaksliste = sakslister.find((s) => s.sakslisteId === valgtSakId);

  return (
    <>
      <GjeldendeSakslisterTabell
        sakslister={sakslister}
        setValgtSakslisteId={setValgtSakslisteId}
        valgtSakslisteId={valgtSakId}
        valgtAvdelingEnhet={valgtAvdelingEnhet}
        oppgaverForAvdelingAntall={oppgaverForAvdelingAntall}
        lagNySaksliste={lagNySakslisteOgHentAvdelingensSakslisterPåNytt}
        resetValgtSakslisteId={resetValgtSakslisteId}
        hentAvdelingensSakslister={hentAvdelingensSakslister}
      />
      <VerticalSpacer sixteenPx />
      {valgtSakId && valgtSaksliste && (
        <>
          <UtvalgskriterierForSakslisteForm
            valgtSaksliste={valgtSaksliste}
            valgtAvdelingEnhet={valgtAvdelingEnhet}
            hentAvdelingensSakslister={hentAvdelingensSakslister}
            hentOppgaverForAvdelingAntall={hentOppgaverForAvdelingAntall}
          />
          <Row>
            <Column xs="5" />
            <Column xs="1">
              <Image
                alt={intl.formatMessage({ id: 'EndreSakslisterPanel.Saksbehandlere' })}
                src={pilNedUrl}
              />
            </Column>
            <Column xs="5" className={styles.text}>
              <FormattedMessage id="EndreSakslisterPanel.KnyttetMotSaksbehandlere" />
            </Column>
          </Row>
          <SaksbehandlereForSakslisteForm
            valgtSaksliste={valgtSaksliste}
            valgtAvdelingEnhet={valgtAvdelingEnhet}
            avdelingensSaksbehandlere={avdelingensSaksbehandlere}
            hentAvdelingensSakslister={hentAvdelingensSakslister}
          />
        </>
      )}
    </>
  );
};

export default injectIntl(EndreSakslisterPanel);
