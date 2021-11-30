import React, {
  useState, KeyboardEvent, ReactNode, FunctionComponent, useEffect, useRef, useCallback,
} from 'react';
import { FormattedMessage } from 'react-intl';
import {
  Normaltekst, Undertekst, Element, Undertittel,
} from 'nav-frontend-typografi';
import { Column, Row } from 'nav-frontend-grid';

import Kodeverk from 'types/kodeverkTsType';
import Saksliste from 'types/avdelingsleder/sakslisteAvdelingTsType';
import KodeverkType from 'kodeverk/kodeverkTyper';
import Image from 'sharedComponents/Image';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import Table from 'sharedComponents/table/Table';
import TableRow from 'sharedComponents/table/TableRow';
import TableColumn from 'sharedComponents/table/TableColumn';
import DateLabel from 'sharedComponents/DateLabel';
import addCircleIcon from 'images/add-circle.svg';
import removeIcon from 'images/remove.svg';
import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import useKodeverk from 'data/useKodeverk';
import SletteSakslisteModal from './SletteSakslisteModal';

import styles from './gjeldendeSakslisterTabell.less';

const headerTextCodes = [
  'GjeldendeSakslisterTabell.Listenavn',
  'GjeldendeSakslisterTabell.Stonadstype',
  'GjeldendeSakslisterTabell.Behandlingtype',
  'GjeldendeSakslisterTabell.AntallSaksbehandlere',
  'GjeldendeSakslisterTabell.AntallBehandlinger',
  'GjeldendeSakslisterTabell.SistEndret',
  'EMPTY_1',
];

const formatStonadstyper = (fagsakYtelseTyper: Kodeverk[], valgteFagsakYtelseTyper?: Kodeverk[]): string | ReactNode => {
  if (!valgteFagsakYtelseTyper || valgteFagsakYtelseTyper.length === 0) {
    return <FormattedMessage id="GjeldendeSakslisterTabell.Alle" />;
  }

  return valgteFagsakYtelseTyper.map((fyt) => {
    const type = fagsakYtelseTyper.find((def) => def.kode === fyt.kode);
    return type ? type.navn : '';
  }).join(', ');
};

const formatBehandlingstyper = (behandlingTyper: Kodeverk[], valgteBehandlingTyper?: Kodeverk[]): string | ReactNode => {
  if (!valgteBehandlingTyper || valgteBehandlingTyper.length === 0
    || valgteBehandlingTyper.length === behandlingTyper.length) {
    return <FormattedMessage id="GjeldendeSakslisterTabell.Alle" />;
  }

  return valgteBehandlingTyper.map((bt) => {
    const type = behandlingTyper.find((def) => def.kode === bt.kode);
    return type ? type.navn : '';
  }).join(', ');
};

interface OwnProps {
  sakslister: Saksliste[];
  setValgtSakslisteId: (sakslisteId: number) => void;
  valgtSakslisteId?: number;
  valgtAvdelingEnhet: string;
  oppgaverForAvdelingAntall?: number;
  lagNySaksliste: (avdelingEnhet: string) => void;
  resetValgtSakslisteId: () => void;
  hentAvdelingensSakslister: (params: {avdelingEnhet: string}) => void;
}

const wait = (ms: number): Promise<any> => new Promise((resolve) => {
  setTimeout(resolve, ms);
});

/**
 * GjeldendeSakslisterTabell
 */
export const GjeldendeSakslisterTabell: FunctionComponent<OwnProps> = ({
  sakslister,
  valgtAvdelingEnhet,
  setValgtSakslisteId,
  valgtSakslisteId,
  oppgaverForAvdelingAntall,
  lagNySaksliste,
  resetValgtSakslisteId,
  hentAvdelingensSakslister,
}) => {
  const [valgtSaksliste, setValgtSakslisteTemp] = useState<Saksliste>();
  const tabRef = useRef<(HTMLDivElement | null)[]>([]);

  const behandlingTyper = useKodeverk(KodeverkType.BEHANDLING_TYPE);
  const fagsakYtelseTyper = useKodeverk(KodeverkType.FAGSAK_YTELSE_TYPE);

  const { startRequest: fjernSaksliste } = restApiHooks.useRestApiRunner(RestApiPathsKeys.SLETT_SAKSLISTE);

  useEffect(() => {
    tabRef.current = tabRef.current.slice(0, sakslister.length);
  }, [sakslister]);

  const setValgtSaksliste = async (event: React.MouseEvent | React.KeyboardEvent, id: number): Promise<string | undefined> => {
    // @ts-ignore Fiks
    if (tabRef.current.some((node) => node && node.contains(event.target))) {
      return;
    }

    // Må vente 100 ms før en byttar behandlingskø i tabell. Dette fordi lagring av navn skjer som blur-event. Så i tilfellet
    // der en endrer navn og så trykker direkte på en annen behandlingskø vil ikke lagringen skje før etter at ny kø er valgt.
    await wait(100);

    setValgtSakslisteId(id);
  };

  const lagNySakslisteFn = useCallback((event: KeyboardEvent): void => {
    if (event.keyCode === 13) {
      lagNySaksliste(valgtAvdelingEnhet);
    }
  }, [valgtAvdelingEnhet]);

  const visFjernSakslisteModal = (nyValgtSaksliste: Saksliste): void => {
    setValgtSakslisteTemp(nyValgtSaksliste);
  };

  const closeSletteModal = useCallback((): void => {
    setValgtSakslisteTemp(undefined);
  }, []);

  const fjernSakslisteFn = useCallback((saksliste: Saksliste): void => {
    closeSletteModal();
    fjernSaksliste({ sakslisteId: saksliste.sakslisteId, avdelingEnhet: valgtAvdelingEnhet })
      .then(() => {
        resetValgtSakslisteId();
        hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
      });
  }, [valgtAvdelingEnhet]);

  return (
    <>
      <Row>
        <Column xs="9">
          <Element>
            <FormattedMessage id="GjeldendeSakslisterTabell.GjeldendeLister" />
          </Element>
        </Column>
        <Column xs="3">
          <div className={styles.grayBox}>
            <Normaltekst>
              <FormattedMessage id="GjeldendeSakslisterTabell.OppgaverForAvdeling" />
            </Normaltekst>
            <Undertittel>{oppgaverForAvdelingAntall || '0'}</Undertittel>
          </div>
        </Column>
      </Row>
      {sakslister.length === 0 && (
        <>
          <VerticalSpacer eightPx />
          <Normaltekst><FormattedMessage id="GjeldendeSakslisterTabell.IngenLister" /></Normaltekst>
          <VerticalSpacer eightPx />
        </>
      )}
      {sakslister.length > 0 && (
      <Table headerTextCodes={headerTextCodes}>
        {sakslister.map((saksliste, index) => (
          <TableRow
            key={saksliste.sakslisteId}
            className={saksliste.sakslisteId === valgtSakslisteId ? styles.isSelected : undefined}
            id={saksliste.sakslisteId}
            onMouseDown={setValgtSaksliste}
            onKeyDown={setValgtSaksliste}
          >
            <TableColumn>{saksliste.navn}</TableColumn>
            <TableColumn>{formatStonadstyper(fagsakYtelseTyper, saksliste.fagsakYtelseTyper)}</TableColumn>
            <TableColumn>{formatBehandlingstyper(behandlingTyper, saksliste.behandlingTyper)}</TableColumn>
            <TableColumn>{saksliste.saksbehandlerIdenter.length > 0 ? saksliste.saksbehandlerIdenter.length : ''}</TableColumn>
            <TableColumn>{saksliste.antallBehandlinger}</TableColumn>
            <TableColumn>
              <DateLabel dateString={saksliste.sistEndret} />
            </TableColumn>
            <TableColumn>
              <div ref={(el) => { tabRef.current[index] = el; }}>
                <Image
                  src={removeIcon}
                  className={styles.removeImage}
                  onMouseDown={() => visFjernSakslisteModal(saksliste)}
                  onKeyDown={() => visFjernSakslisteModal(saksliste)}
                />
              </div>
            </TableColumn>
          </TableRow>
        ))}
      </Table>
      )}
      <div
        id="leggTilListe"
        role="button"
        tabIndex={0}
        className={styles.addPeriode}
        onClick={() => lagNySaksliste(valgtAvdelingEnhet)}
        onKeyDown={lagNySakslisteFn}
      >
        <Image className={styles.addCircleIcon} src={addCircleIcon} />
        <Undertekst className={styles.imageText}>
          <FormattedMessage id="GjeldendeSakslisterTabell.LeggTilListe" />
        </Undertekst>
      </div>
      {valgtSaksliste && (
        <SletteSakslisteModal
          valgtSaksliste={valgtSaksliste}
          cancel={closeSletteModal}
          submit={fjernSakslisteFn}
        />
      )}
    </>
  );
};

export default GjeldendeSakslisterTabell;
