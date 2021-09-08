import React, { useState } from 'react';

import circleUrl from 'images/add-circle.svg';
import gruppeUrl from 'images/gruppe.svg';
import bubbleUrl from 'images/bubbletext.svg';
import Image from './Image';
import Modal from './Modal';

export default {
  title: 'sharedComponents/Image',
  component: Image,
};

export const Default = () => (
  <Image
    alt="Alt-tekst"
    src={circleUrl}
  />
);

export const KlikkbartIkon = () => {
  const [visModal, setVisModal] = useState(false);

  return (
    <>
      <Image
        alt="Alt-tekst"
        src={circleUrl}
        onClick={() => setVisModal(true)}
      />
      {visModal && (
        <Modal
          contentLabel="Dette er en advarsel"
          isOpen
          closeButton
          onRequestClose={() => undefined}
        />
      )}
    </>
  );
};

export const IkonMedTooltip = () => (
  <Image
    alt="Alt-tekst"
    src={circleUrl}
    tooltip={<div><b>Dette er en tooltip-tekst</b></div>}
  />
);

export const AnnetIkonVedHoover = () => (
  <Image
    alt="Alt-tekst"
    src={gruppeUrl}
    srcHover={bubbleUrl}
  />
);
