import React, { Component } from 'react';
import '../App.css';

class NewEventForm extends Component {
  constructor(props) {
    super(props);
    this.handleChange = this.handleChange.bind(this);
    this.state = {
      event_name: '',
      start_date: '',
      end_date: '',
    };
  }

  handleChange(event) {
    this.setState({[event.target.name]: event.target.value},
    () => this.props.change(this.state.event_name,this.state.start_date,this.state.end_date));
  }

  render() {
    return (
      <form onSubmit={this.handleSubmit}>
        <label>
          {"Event Name:"}
          <input type="text" name="event_name" className="New-Recipe-form" onChange={this.handleChange} />
          <br/>
          <br/>
          {"Start Date"}
          <input type="text" name="start_date" className="New-Recipe-form" onChange={this.handleChange} />
          <br/>
          <br/>
          {"End Date"}
          <input type="text" name="end_date" className="New-Recipe-form" onChange={this.handleChange} />
        </label>
      </form>
    );
  }
}

export default NewEventForm;
