import { configure as configureEnzyme } from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import { switchOnTestMode } from 'data/rest-api';

configureEnzyme({ adapter: new Adapter() });

switchOnTestMode();
