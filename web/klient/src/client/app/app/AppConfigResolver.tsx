import React, { Component, ReactNode } from 'react';
import { bindActionCreators, Dispatch } from 'redux';
import { connect } from 'react-redux';

import fpLosApi from 'data/fpLosApi';
import LoadingPanel from 'sharedComponents/LoadingPanel';

interface OwnProps {
  finishedLoadingBlockers: boolean;
  children: ReactNode;
  fetchNavAnsatt: () => void;
  fetchKodeverk: () => void;
  fetchFpsakUrl: () => void;
  fetchFptilbakeUrl: () => void;
}

class AppConfigResolver extends Component<OwnProps> {
  constructor(props: OwnProps) {
    super(props);
    this.resolveAppConfig();
  }

  resolveAppConfig = () => {
    const {
      fetchNavAnsatt,
      fetchKodeverk,
      fetchFpsakUrl,
      fetchFptilbakeUrl,
    } = this.props;

    fetchNavAnsatt();
    fetchKodeverk();
    fetchFpsakUrl();
    fetchFptilbakeUrl();
  }

  render = () => {
    const { finishedLoadingBlockers, children } = this.props;
    if (!finishedLoadingBlockers) {
      return <LoadingPanel />;
    }
    return children;
  }
}

const mapStateToProps = (state: any) => {
  const blockers = [
    fpLosApi.NAV_ANSATT.getRestApiFinished()(state),
    fpLosApi.KODEVERK.getRestApiFinished()(state),
    fpLosApi.FPSAK_URL.getRestApiFinished()(state),
    fpLosApi.FPTILBAKE_URL.getRestApiFinished()(state),
  ];
  return {
    finishedLoadingBlockers: blockers.every((finished) => finished),
  };
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({
  fetchNavAnsatt: fpLosApi.NAV_ANSATT.makeRestApiRequest(),
  fetchKodeverk: fpLosApi.KODEVERK.makeRestApiRequest(),
  fetchFpsakUrl: fpLosApi.FPSAK_URL.makeRestApiRequest(),
  fetchFptilbakeUrl: fpLosApi.FPTILBAKE_URL.makeRestApiRequest(),
}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(AppConfigResolver);
