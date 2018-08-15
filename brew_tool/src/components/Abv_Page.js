import React, {Component} from 'react';
import {Button} from 'react-bootstrap';
import Abv_Form from './Abv_Form';
import '../App.css';

class Abv_Page extends Component {

  render() {
    return(
      <div>
        <Abv_Form orig="Original Gravity (OG): " final="Final Gravity (FG): "/>
      </div>
    );
  }

}

export default Abv_Page;
