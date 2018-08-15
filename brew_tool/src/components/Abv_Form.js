import React, { Component } from 'react';
import '../App.css';

class Abv_Form extends Component {
  constructor(props) {
    super(props);
    this.state = {abv: 0, attenuation: 0,orig: '', final: ''};

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event) {
    this.setState({[event.target.name]: event.target.value});
  }

  handleSubmit(event) {
    var og = parseFloat(this.state.orig);
    var fg = parseFloat(this.state.final);
    var result = (og - fg) * 131.25;
    var result2 = (og - fg)/(og - 1.0) * 100;
    this.setState({abv: result, attenuation: result2});
    event.preventDefault();
  }

  render() {
    return (
      <form onSubmit={this.handleSubmit}>
        <label>
          {this.props.orig}
          <input type="text" name="orig" className="ABV-form" onChange={this.handleChange} />
          {this.props.final}
          <input type="text" name="final" className="ABV-form" onChange={this.handleChange} />
          <input type="submit" value="Submit" name="sub" onClick={this.handleSubmit}/>
          <p>{"ABV: " + this.state.abv.toFixed(2) + "%  Attenuation: " + this.state.attenuation}</p>
        </label>
      </form>
    );
  }
}

export default Abv_Form;
