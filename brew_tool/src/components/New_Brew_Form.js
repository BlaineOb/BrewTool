import React, { Component } from 'react';
import '../App.css';

class New_Brew_Form extends Component {
  constructor(props) {
    super(props);
    this.state = {recName: '', date: '', data:[],};

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  componentDidMount(){

  }

  handleChange(event) {
    this.setState({[event.target.name]: event.target.value},
    () => this.props.change(this.state.recName,this.state.date));
  }

  handleSubmit(event) {
    var nm = this.state.recName;
    var dt = this.state.date;

  }

  render() {
    return (
      <form onSubmit={this.handleSubmit}>
        <label>
          {this.props.recName}
          <input type="text" name="recName" className="New-Recipe-form" onChange={this.handleChange} />
          <br/>
          <br/>
          {this.props.date}
          <input type="text" name="date" className="New-Recipe-form" onChange={this.handleChange} />
        </label>
      </form>
    );
  }
}

export default New_Brew_Form;
