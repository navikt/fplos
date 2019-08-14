import React from 'react';
import PropTypes from 'prop-types';

import styles from './avdelingslederDashboard.less';

type TsProps = Readonly<{
  children: any;
}>;

/**
 * AvdelingslederDashboard
 */
const AvdelingslederDashboard = ({
  children,
}: TsProps) => (
  <div>
    <div className={styles.oppgaveContainer}>
      <div className={styles.gridContainer}>
        <div className={styles.leftColumn}>
          <div className={styles.avdelingslederContent}>
            {children}
          </div>
        </div>
      </div>
    </div>
  </div>
);

AvdelingslederDashboard.propTypes = {
  children: PropTypes.node.isRequired,
};

export default AvdelingslederDashboard;
