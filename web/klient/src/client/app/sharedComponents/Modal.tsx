import React, { FunctionComponent } from 'react';
import NavModal from 'nav-frontend-modal';

interface OwnProps {
  className: string;
  closeButton: boolean;
  isOpen: boolean;
  contentLabel: string;
  onRequestClose: () => void;
  shouldCloseOnOverlayClick?: boolean;
}

/**
 * Modal
 *
 * Presentasjonskomponent. Wrapper Modal-komponenten fra nav-frontend-biblioteket, men tillater ikke bruk av propertien 'shouldCloseOnOverlayClick'.
 */
export const Modal: FunctionComponent<OwnProps> = ({ children, ...otherProps }) => {
  NavModal.setAppElement(process.env.NODE_ENV !== 'test' ? 'div#app' : 'div');
  return (
    <NavModal {...otherProps}>
      {children}
    </NavModal>
  );
};

Modal.defaultProps = {
  shouldCloseOnOverlayClick: false,
};

export default Modal;
