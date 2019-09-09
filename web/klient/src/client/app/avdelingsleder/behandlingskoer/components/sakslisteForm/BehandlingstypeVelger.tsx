import React from 'react';
import PropTypes from 'prop-types';
import { createSelector } from 'reselect';
import { connect } from 'react-redux';
import { FormattedMessage } from 'react-intl';
import { Undertekst } from 'nav-frontend-typografi';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { getKodeverk } from 'kodeverk/duck';
import { Kodeverk } from 'kodeverk/kodeverkTsType';
import kodeverkPropType from 'kodeverk/kodeverkPropType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import behandlingType from 'kodeverk/behandlingType';
import { CheckboxField } from 'form/FinalFields';

interface TsProps {
  behandlingTyper: Kodeverk[];
  valgtSakslisteId: number;
  lagreSakslisteBehandlingstype: (sakslisteId: number, behandlingType: Kodeverk, isChecked: boolean, avdelingEnhet: string) => void;
  valgtAvdelingEnhet: string;
}

/**
 * BehandlingstypeVelger
 */
export const BehandlingstypeVelger = ({
  behandlingTyper,
  valgtSakslisteId,
  lagreSakslisteBehandlingstype,
  valgtAvdelingEnhet,
}: TsProps) => (
  <>
    <Undertekst>
      <FormattedMessage id="BehandlingstypeVelger.Behandlingstype" />
    </Undertekst>
    <VerticalSpacer eightPx />
    {behandlingTyper.map(bt => (
      <CheckboxField
        key={bt.kode}
        name={bt.kode}
        label={bt.navn}
        onChange={isChecked => lagreSakslisteBehandlingstype(valgtSakslisteId, bt, isChecked, valgtAvdelingEnhet)}
      />
    ))
    }
  </>
);

BehandlingstypeVelger.propTypes = {
  behandlingTyper: PropTypes.arrayOf(kodeverkPropType).isRequired,
  valgtSakslisteId: PropTypes.number.isRequired,
  lagreSakslisteBehandlingstype: PropTypes.func.isRequired,
  valgtAvdelingEnhet: PropTypes.string.isRequired,
};

const behandlingstypeOrder = Object.values(behandlingType);

const getFiltrerteOgSorterteBehandlingstyper = createSelector(
  [getKodeverk(kodeverkTyper.BEHANDLING_TYPE)], behandlingsTyper => behandlingstypeOrder.map(kode => behandlingsTyper.find(bt => bt.kode === kode)),
);

const mapStateToProps = state => ({
  behandlingTyper: getFiltrerteOgSorterteBehandlingstyper(state),
});

export default connect(mapStateToProps)(BehandlingstypeVelger);
