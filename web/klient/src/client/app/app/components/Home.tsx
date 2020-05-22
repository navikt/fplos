import React, { FunctionComponent, Suspense } from 'react';
import { Route, Switch } from 'react-router-dom';

import LoadingPanel from 'sharedComponents/LoadingPanel';
import MissingPage from './MissingPage';

import styles from './home.less';

const AvdelingslederIndex = React.lazy(() => import('avdelingsleder/AvdelingslederIndex'));
const SaksbehandlerIndex = React.lazy(() => import('saksbehandler/SaksbehandlerIndex'));

interface OwnProps {
  headerHeight: number;
}

/**
 * Home
 *
 * Presentasjonskomponent. Wrapper for sideinnholdet som vises under header.
 */
const Home: FunctionComponent<OwnProps> = ({
  headerHeight,
}) => (
  <div className={styles.content} style={{ margin: `${headerHeight + 10}px auto 0` }}>
    <Suspense fallback={<LoadingPanel />}>
      <Switch>
        <Route exact path="/" component={SaksbehandlerIndex} />
        <Route exact path="/avdelingsleder" component={AvdelingslederIndex} />
        <Route component={MissingPage} />
      </Switch>
    </Suspense>
  </div>
);

export default Home;
