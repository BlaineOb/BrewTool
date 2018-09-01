import React, { Component } from 'react';
import NewBrewForm from './New_Brew_Form';
import {Button} from 'react-bootstrap';
import Modal from 'react-bootstrap-modal';
import '../../node_modules/react-bootstrap-modal/lib/css/rbm-complete.css';


class Delete_Modal extends Component {

  constructor(props){
    super(props);
    this.handleClick = this.handleClick.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.state = {
      recipes: [],
      open: false,
      recName: '',
      date: '',
    }
  }

  componentDidMount() {
    fetch('http://localhost:8081/brew_tool/b/home/get_recipe')
    .then((results) => {return results.json();})
    .then((data) => {console.log("RECDdata",data);
      this.setState({recipes: data});
    });
  }

  handleClick(){
 		this.setState({open:true});
 	}

  handleChange(nm, dt){
    this.setState({recName:nm,date:dt});
  }

  handleSave(){
    var correctDate = this.state.date.replace(/\//g , "@");
    var found = false;
    for (let i=0; i < this.state.recipes.length; i++){
      if (this.state.recipes[i].recName == this.state.recName && this.state.recipes[i].recDate.replace(/\s/g, '') == this.state.date){
        found = true;
        break;
      }
    }
    if (found) {
      fetch('http://localhost:8081/brew_tool/b/home/delete_recipe/' + this.state.recName + '/' + correctDate, {
        method: 'POST',
        mode: 'no-cors',
        headers: {
          'Content-Type': 'application/json',
        },
      }).then(() => {this.setState({recName: '',date: ''})});
    } else {
      alert('Given recipe/date does not exist')
    }

  }

  render(){
    let closeModal = () => this.setState({ open: false })

    let saveAndClose = () => {
      this.setState({ open: false });
      this.handleSave();
    }

    return (
      <div>
        <Button className="Delete-button" onClick={this.handleClick} bsStyle="warning">Delete Brew</Button>

        <Modal
          show={this.state.open}
          onHide={closeModal}
          aria-labelledby="ModalHeader"
        >
          <Modal.Header>
            <Modal.Title id='ModalHeader'>Delete Brew</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <NewBrewForm change={this.handleChange} recName="Recipe Name: " date="Date Started(MM/DD/YY): "/>
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

export default Delete_Modal;
