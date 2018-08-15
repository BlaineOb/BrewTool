import React, { Component } from 'react';
import New_Brew_Form from './New_Brew_Form';
import {Button} from 'react-bootstrap';
import Modal from 'react-bootstrap-modal';
import '../../node_modules/react-bootstrap-modal/lib/css/rbm-complete.css';


class My_Modal extends React.Component {

  constructor(props){
    super(props);
    this.handleClick = this.handleClick.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.state = {
      open: false,
      recName: '',
      date: '',
    }
  }
  handleClick(){
 		this.setState({open:true});
 	}

  handleChange(nm, dt){
    this.state.recName = nm;
    this.state.date = dt;
  }

  handleSave(){
    /*Remeber to set these to empty after they have been used*/
    fetch('http://localhost:8081/brew_tool/b/home/post_recipe', {
      method: 'POST',
      mode: 'no-cors',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        name: this.state.recName,
        date: this.state.date,
      })
    });
  }

  render(){
    let closeModal = () => this.setState({ open: false })

    let saveAndClose = () => {
      this.setState({ open: false });
      this.handleSave();
    }

    return (
      <div>
        <Button className="Recipe-button" onClick={this.handleClick} bsStyle="warning">Add New Brew</Button>

        <Modal
          show={this.state.open}
          onHide={closeModal}
          aria-labelledby="ModalHeader"
        >
          <Modal.Header>
            <Modal.Title id='ModalHeader'>Add New Brew</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <New_Brew_Form change={this.handleChange} recName="Recipe Name: " date="Date Started(MM/DD/YY): "/>
          </Modal.Body>
          <Modal.Footer>
            <Modal.Dismiss className='btn btn-default'>Cancel</Modal.Dismiss>
            <button className='btn btn-primary' onClick={saveAndClose}>
              Save
            </button>
          </Modal.Footer>
        </Modal>
      </div>
    )
  }
}

export default My_Modal;
