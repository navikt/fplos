import React, { Component, ReactNode } from 'react';
import PropTypes from 'prop-types';
import { bindActionCreators, Dispatch } from 'redux';
import { connect } from 'react-redux';

import fpLosApi from 'data/fpLosApi';
import LoadingPanel from 'sharedComponents/LoadingPanel';

type TsProps = Readonly<{
  finishedLoadingBlockers: boolean;
  children: ReactNode;
  fetchNavAnsatt: () => void;
  fetchKodeverk: () => void;
  fetchFpsakUrl: () => void;
  fetchFptilbakeUrl: () => void;
}>

class AppConfigResolver extends Component<TsProps> {
  static propTypes = {
    finishedLoadingBlockers: PropTypes.bool.isRequired,
    children: PropTypes.node.isRequired,
    fetchNavAnsatt: PropTypes.func.isRequired,
    fetchKodeverk: PropTypes.func.isRequired,
    fetchFpsakUrl: PropTypes.func.isRequired,
    fetchFptilbakeUrl: PropTypes.func.isRequired,
  };

  constructor(props: TsProps) {
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
