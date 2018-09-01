import React, {Component} from 'react';
import AbvForm from './Abv_Form';
import StrikeForm from './Strike_Form';
import '../App.css';

class Abv_Page extends Component {

  render() {
    return(
      <div>
        <AbvForm/>
        <StrikeForm/>
      </div>
    );
  }

}

export default Abv_Page;
