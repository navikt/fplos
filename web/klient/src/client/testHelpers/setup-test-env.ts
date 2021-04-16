import { configure as configureEnzyme } from 'enzyme';
import Adapter from '@wojtekmaj/enzyme-adapter-react-17';
import { switchOnTestMode } from 'data/rest-api';

configureEnzyme({ adapter: new Adapter() });

switchOnTestMode();
