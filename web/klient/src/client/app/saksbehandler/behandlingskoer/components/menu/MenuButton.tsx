import React, { ReactNode, FunctionComponent } from 'react';
import { Undertekst } from 'nav-frontend-typografi';

import styles from './menuButton.less';

interface OwnProps {
  onClick: () => void;
  children: ReactNode;
  ref?: any;
}

/**
 * MenuButton
 *
 * Presentasjonskomponent. Lager lenker i behandlingsmeny
 */
const MenuButton: FunctionComponent<OwnProps> = React.forwardRef(({
  onClick,
  children,
}, ref: any) => (
  <button ref={ref} className={styles.button} onClick={onClick} type="button">
    <Undertekst>{children}</Undertekst>
  </button>
));

export default MenuButton;
