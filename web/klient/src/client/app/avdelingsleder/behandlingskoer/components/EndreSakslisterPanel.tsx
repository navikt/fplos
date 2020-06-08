import React, { FunctionComponent } from 'react';
import { injectIntl, FormattedMessage, WrappedComponentProps } from 'react-intl';
import { Row, Column } from 'nav-frontend-grid';

import Image from 'sharedComponents/Image';
import useRestApi from 'data/rest-api-hooks/useRestApi';
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
}) => {
  const { data: oppgaverForAvdelingAntall } = useRestApi<number>(RestApiPathsKeys.OPPGAVE_AVDELING_ANTALL,
    { avdelingEnhet: valgtAvdelingEnhet }, [valgtAvdelingEnhet]);

  const { data: sakslister = EMPTY_ARRAY } = useRestApi<Saksliste[]>(RestApiPathsKeys.SAKSLISTER_FOR_AVDELING,
    { avdelingEnhet: valgtAvdelingEnhet }, [valgtAvdelingEnhet]);

  const { data: nySakslisteObject, startRequest: lagNySaksliste } = useRestApiRunner<{sakslisteId: string}>(RestApiPathsKeys.OPPRETT_NY_SAKSLISTE);
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
        lagNySaksliste={lagNySaksliste}
      />
      <VerticalSpacer sixteenPx />
      {valgtSakId && valgtSaksliste && (
        <>
          <UtvalgskriterierForSakslisteForm
            valgtSaksliste={valgtSaksliste}
            valgtAvdelingEnhet={valgtAvdelingEnhet}
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
          />
        </>
      )}
    </>
  );
};

export default injectIntl(EndreSakslisterPanel);
