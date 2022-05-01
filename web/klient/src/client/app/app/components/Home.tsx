import React, { FunctionComponent, Suspense } from 'react';
import { Route, Routes } from 'react-router-dom';

import LoadingPanel from 'sharedComponents/LoadingPanel';

import MissingPage from './MissingPage';

import styles from './home.less';

const AvdelingslederIndex = React.lazy(() => import('avdelingsleder/AvdelingslederIndex'));
const SaksbehandlerIndex = React.lazy(() => import('saksbehandler/SaksbehandlerIndex'));

interface OwnProps {
  headerHeight: number;
  valgtAvdelingEnhet?: string;
}

/**
 * Home
 *
 * Presentasjonskomponent. Wrapper for sideinnholdet som vises under header.
 */
const Home: FunctionComponent<OwnProps> = ({
  headerHeight,
  valgtAvdelingEnhet,
}) => (
  <div className={styles.content} style={{ margin: `${headerHeight + 10}px auto 0` }}>
    <Suspense fallback={<LoadingPanel />}>
      <Routes>
        <Route path="/" element={<SaksbehandlerIndex />} />
        <Route path="/avdelingsleder" element={<AvdelingslederIndex valgtAvdelingEnhet={valgtAvdelingEnhet} />} />
        <Route element={<MissingPage />} />
      </Routes>
    </Suspense>
  </div>
);

export default Home;
