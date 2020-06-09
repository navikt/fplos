import React, { FunctionComponent, useCallback, useEffect } from 'react';
import { injectIntl, FormattedMessage, WrappedComponentProps } from 'react-intl';
import { Row, Column } from 'nav-frontend-grid';

import Image from 'sharedComponents/Image';
import { RestApiPathsKeys } from 'data/restApiPaths';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import pilNedUrl from 'images/pil-ned.svg';
import Saksbehandler from 'avdelingsleder/saksbehandlere/saksbehandlerTsType';
import useRestApiRunner from 'data/rest-api-hooks/useRestApiRunner';
import GjeldendeSakslisterTabell from './GjeldendeSakslisterTabell';
import SaksbehandlereForSakslisteForm from './saksbehandlerForm/SaksbehandlereForSakslisteForm';
import UtvalgskriterierForSakslisteForm from './sakslisteForm/UtvalgskriterierForSakslisteForm';

import styles from './endreSakslisterPanel.less';
import Saksliste from '../sakslisteTsType';

const EMPTY_ARRAY = [];

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
  const { data: oppgaverForAvdelingAntall, startRequest: hentOppgaverForAvdelingAntall } = useRestApiRunner<number>(RestApiPathsKeys.OPPGAVE_AVDELING_ANTALL);
  const { data: sakslister = EMPTY_ARRAY, startRequest: hentAvdelingensSl } = useRestApiRunner<Saksliste[]>(RestApiPathsKeys.SAKSLISTER_FOR_AVDELING);
  const hentAvdelingensSakslister = useCallback((params) => hentAvdelingensSl(params, true), []);
  useEffect(() => {
    hentOppgaverForAvdelingAntall({ avdelingEnhet: valgtAvdelingEnhet });
    hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
  }, [valgtAvdelingEnhet]);

  const { data: nySakslisteObject, startRequest: lagNySaksliste } = useRestApiRunner<{sakslisteId: string}>(RestApiPathsKeys.OPPRETT_NY_SAKSLISTE);
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
