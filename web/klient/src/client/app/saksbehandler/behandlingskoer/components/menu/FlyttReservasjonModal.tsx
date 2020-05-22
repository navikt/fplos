import React, { Component, ReactNode } from 'react';
import { connect } from 'react-redux';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';

import { Form } from 'react-final-form';
import { Hovedknapp, Knapp } from 'nav-frontend-knapper';
import { Normaltekst, Element } from 'nav-frontend-typografi';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { FlexContainer, FlexRow, FlexColumn } from 'sharedComponents/flexGrid';
import {
  hasValidText, maxLength, minLength, required,
} from 'utils/validation/validators';
import { TextAreaField, InputField } from 'form/FinalFields';
import Modal from 'sharedComponents/Modal';
import { getSaksbehandler, isSaksbehandlerSokStartet, isSaksbehandlerSokFerdig } from '../../duck';
import SaksbehandlerForFlytting from './saksbehandlerForFlyttingTsType';

import styles from './flyttReservasjonModal.less';

const minLength3 = minLength(3);
const maxLength500 = maxLength(500);
const minLength7 = minLength(7);
const maxLength7 = maxLength(7);

interface OwnProps {
  showModal: boolean;
  oppgaveId: number;
  closeModal: () => void;
  submit: (oppgaveId: number, brukerident: string, begrunnelse: string) => void;
  finnSaksbehandler: (brukerident: string) => void;
  resetSaksbehandler: () => void;
  saksbehandler?: SaksbehandlerForFlytting;
  erSaksbehandlerSokStartet: boolean;
  erSaksbehandlerSokFerdig: boolean;
}

/**
 * FlyttReservasjonModal
 *
 * Presentasjonskomponent. Modal som lar en søke opp en saksbehandler som saken skal flyttes til. En kan også begrunne hvorfor saken skal flyttes.
 */
export class FlyttReservasjonModal extends Component<OwnProps & WrappedComponentProps> {
   componentWillUnmount = (): void => {
     const {
       resetSaksbehandler,
     } = this.props;
     resetSaksbehandler();
   }

   formatText = (): string => {
     const {
       intl, saksbehandler, erSaksbehandlerSokFerdig,
     } = this.props;

     if (erSaksbehandlerSokFerdig && !saksbehandler) {
       return intl.formatMessage({ id: 'LeggTilSaksbehandlerForm.FinnesIkke' });
     }

     return saksbehandler
       ? `${saksbehandler.navn}, ${saksbehandler.avdelingsnavn.join(', ')}`
       : '';
   }

   render = (): ReactNode => {
     const {
       intl, showModal, closeModal, submit, oppgaveId, finnSaksbehandler, erSaksbehandlerSokStartet, erSaksbehandlerSokFerdig, saksbehandler,
     } = this.props;

     return (
       <Modal
         className={styles.modal}
         isOpen={showModal}
         closeButton={false}
         contentLabel={intl.formatMessage({ id: 'FlyttReservasjonModal.FlyttReservasjon' })}
         onRequestClose={closeModal}
       >
         <Form
           onSubmit={(values) => finnSaksbehandler(values.brukerIdent)}
           render={({
             handleSubmit, values,
           }) => (
             <form onSubmit={handleSubmit}>
               <Element>
                 <FormattedMessage id="FlyttReservasjonModal.FlyttReservasjon" />
               </Element>
               <VerticalSpacer eightPx />
               <FlexContainer>
                 <FlexRow>
                   <FlexColumn>
                     <InputField
                       name="brukerIdent"
                       label={intl.formatMessage({ id: 'FlyttReservasjonModal.Brukerident' })}
                       bredde="S"
                       validate={[required, minLength7, maxLength7]}
                       autoFocus
                     />
                   </FlexColumn>
                   <FlexColumn>
                     <Hovedknapp
                       mini
                       htmlType="submit"
                       className={styles.button}
                       spinner={erSaksbehandlerSokStartet}
                       disabled={!values.brukerIdent || erSaksbehandlerSokStartet}
                     >
                       <FormattedMessage id="FlyttReservasjonModal.Sok" />
                     </Hovedknapp>
                   </FlexColumn>
                 </FlexRow>
               </FlexContainer>
               {erSaksbehandlerSokFerdig && (
               <>
                 <Normaltekst>{this.formatText()}</Normaltekst>
                 <VerticalSpacer sixteenPx />
               </>
               )}
             </form>
           )}
         />
         <VerticalSpacer sixteenPx />
         <Form
           onSubmit={(values) => submit(oppgaveId, saksbehandler ? saksbehandler.brukerIdent : '', values.begrunnelse)}
           render={({
             handleSubmit, values,
           }) => (
             <form onSubmit={handleSubmit}>
               <TextAreaField
                 name="begrunnelse"
                 label={intl.formatMessage({ id: 'FlyttReservasjonModal.Begrunn' })}
                 validate={[required, maxLength500, minLength3, hasValidText]}
                 maxLength={500}
               />
               <Hovedknapp
                 className={styles.submitButton}
                 mini
                 htmlType="submit"
                 disabled={!saksbehandler || (!values.begrunnelse || values.begrunnelse.length < 3)}
               >
                 {intl.formatMessage({ id: 'FlyttReservasjonModal.Ok' })}
               </Hovedknapp>
               <Knapp
                 className={styles.cancelButton}
                 mini
                 htmlType="reset"
                 onClick={closeModal}
               >
                 {intl.formatMessage({ id: 'FlyttReservasjonModal.Avbryt' })}
               </Knapp>
             </form>
           )}
         />
       </Modal>
     );
   }
}


const mapStateToProps = (state) => ({
  erSaksbehandlerSokStartet: isSaksbehandlerSokStartet(state),
  erSaksbehandlerSokFerdig: isSaksbehandlerSokFerdig(state),
  saksbehandler: getSaksbehandler(state),
});

export default connect(mapStateToProps)(injectIntl(FlyttReservasjonModal));
