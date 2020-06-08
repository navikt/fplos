import React, { FunctionComponent } from 'react';
import { connect } from 'react-redux';

import { getValgtSakslisteId } from '../behandlingskoer/duck';
import SaksstottePaneler from './components/SaksstottePaneler';


interface TsProps {
  valgtSakslisteId?: number;
}

/**
 * SaksstotteIndex
 */
const SaksstotteIndex: FunctionComponent<TsProps> = ({
  valgtSakslisteId,
}) => (
  <SaksstottePaneler valgtSakslisteId={valgtSakslisteId} />
);

const mapStateToProps = (state) => ({
  valgtSakslisteId: getValgtSakslisteId(state),
});


export default connect(mapStateToProps)(SaksstotteIndex);
