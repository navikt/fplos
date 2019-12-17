
import React from 'react';
import PropTypes from 'prop-types';
import { FormattedMessage } from 'react-intl';
import { Row, Column } from 'nav-frontend-grid';

import Image from 'sharedComponents/Image';
import { Kodeverk } from 'kodeverk/kodeverkTsType';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import pilNedUrl from 'images/pil-ned.svg';
import GjeldendeSakslisterTabell from './GjeldendeSakslisterTabell';
import SaksbehandlereForSakslisteForm from './saksbehandlerForm/SaksbehandlereForSakslisteForm';
import UtvalgskriterierForSakslisteForm from './sakslisteForm/UtvalgskriterierForSakslisteForm';
import { Saksliste } from '../sakslisteTsType';
import sakslistePropType from '../sakslistePropType';

import styles from './endreSakslisterPanel.less';

interface TsProps {
  sakslister: Saksliste[];
  setValgtSakslisteId: (sakslisteId: number) => void;
  lagNySaksliste: (avdelingEnhet: string) => void;
  fjernSaksliste: (sakslisteId: number, avdelingEnhet: string) => void;
  lagreSakslisteNavn: (saksliste: {sakslisteId: number; navn: string}, avdelingEnhet: string) => void;
  lagreSakslisteBehandlingstype: (sakslisteId: number, behandlingType: Kodeverk, isChecked: boolean, avdelingEnhet: string) => void;
  lagreSakslisteFagsakYtelseType: (sakslisteId: number, fagsakYtelseType: string, avdelingEnhet: string) => void;
  lagreSakslisteAndreKriterier: (sakslisteId: number, andreKriterierType: Kodeverk, isChecked: boolean, skalInkludere: boolean, avdelingEnhet: string) => void;
  knyttSaksbehandlerTilSaksliste: (sakslisteId: number, brukerIdent: string, isChecked: boolean, avdelingEnhet: string) => void;
  valgtSakslisteId?: number;
  hentAvdelingensSakslister: (avdelingEnhet: string) => Saksliste[];
  hentAntallOppgaverForSaksliste: (sakslisteId: number, avdelingEnhet: string) => Promise<string>;
  hentAntallOppgaverForAvdeling: (avdelingEnhet: string) => Promise<string>;
}

/**
 * EndreSakslisterPanel
 */
const EndreSakslisterPanel = ({
  sakslister,
  setValgtSakslisteId,
  valgtSakslisteId,
  lagNySaksliste,
  fjernSaksliste,
  lagreSakslisteNavn,
  lagreSakslisteBehandlingstype,
  lagreSakslisteFagsakYtelseType,
  lagreSakslisteAndreKriterier,
  knyttSaksbehandlerTilSaksliste,
  hentAvdelingensSakslister,
  hentAntallOppgaverForSaksliste,
  hentAntallOppgaverForAvdeling,
}: TsProps) => {
  const valgtSaksliste = sakslister.find(s => s.sakslisteId === valgtSakslisteId);
  return (
    <>
      <GjeldendeSakslisterTabell
        sakslister={sakslister}
        setValgtSakslisteId={setValgtSakslisteId}
        valgtSakslisteId={valgtSakslisteId}
        lagNySaksliste={lagNySaksliste}
        fjernSaksliste={fjernSaksliste}
        hentAvdelingensSakslister={hentAvdelingensSakslister}
        hentAntallOppgaverForAvdeling={hentAntallOppgaverForAvdeling}
      />
      <VerticalSpacer sixteenPx />
      {valgtSakslisteId && valgtSaksliste && (
        <>
          <UtvalgskriterierForSakslisteForm
            valgtSaksliste={valgtSaksliste}
            lagreSakslisteNavn={lagreSakslisteNavn}
            lagreSakslisteBehandlingstype={lagreSakslisteBehandlingstype}
            lagreSakslisteFagsakYtelseType={lagreSakslisteFagsakYtelseType}
            lagreSakslisteAndreKriterier={lagreSakslisteAndreKriterier}
            hentAntallOppgaverForSaksliste={hentAntallOppgaverForSaksliste}
          />
          <Row>
            <Column xs="5" />
            <Column xs="1">
              <Image altCode="EndreSakslisterPanel.Saksbehandlere" src={pilNedUrl} />
            </Column>
            <Column xs="5" className={styles.text}>
              <FormattedMessage id="EndreSakslisterPanel.KnyttetMotSaksbehandlere" />
            </Column>
          </Row>
          <SaksbehandlereForSakslisteForm
            valgtSaksliste={valgtSaksliste}
            knyttSaksbehandlerTilSaksliste={knyttSaksbehandlerTilSaksliste}
          />
        </>
      )}
    </>
  );
};

EndreSakslisterPanel.propTypes = {
  sakslister: PropTypes.arrayOf(sakslistePropType).isRequired,
  setValgtSakslisteId: PropTypes.func.isRequired,
  lagNySaksliste: PropTypes.func.isRequired,
  fjernSaksliste: PropTypes.func.isRequired,
  lagreSakslisteNavn: PropTypes.func.isRequired,
  lagreSakslisteBehandlingstype: PropTypes.func.isRequired,
  lagreSakslisteFagsakYtelseType: PropTypes.func.isRequired,
  knyttSaksbehandlerTilSaksliste: PropTypes.func.isRequired,
  lagreSakslisteAndreKriterier: PropTypes.func.isRequired,
  valgtSakslisteId: PropTypes.number,
  hentAntallOppgaverForSaksliste: PropTypes.func.isRequired,
  hentAntallOppgaverForAvdeling: PropTypes.func.isRequired,
};

EndreSakslisterPanel.defaultProps = {
  valgtSakslisteId: undefined,
};

export default EndreSakslisterPanel;
