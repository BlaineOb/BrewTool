import React, { Component } from 'react';
import '../App.css';

class Strike_Form extends Component {
  constructor(props) {
    super(props);
    this.state = {stemp: 0, volume: '',weight: '', gtemp: '', tmtemp: '', tltemp: ''};

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event) {
    this.setState({[event.target.name]: event.target.value});
  }

  handleSubmit(event) {
    var vol = parseFloat(this.state.volume);
    var wgt = parseFloat(this.state.weight);
    var gtmp = parseFloat(this.state.gtemp);
    var tmtmp = parseFloat(this.state.tmtemp);
    var tltmp = parseFloat(this.state.tltemp);
    var GrainHeat =  wgt*.05;
    var MashHeat = GrainHeat + vol;
    var result = ((MashHeat*(tmtmp + tltmp))-(GrainHeat*gtmp))/vol;
    if (isNaN(result)) {
      alert('Fill in all fields before submitting.');
    } else {
      this.setState({stemp: result});
    }
    event.preventDefault();
  }

  render() {
    return (
      <form onSubmit={this.handleSubmit}>
        <p className='Strike-title'>Strike Water Temperature Calculator</p>
        <label className='ABV-label'>
          {"Strike Water Volume (Gallons)"}
          <input type="text" name="volume" className="ABV-form" onChange={this.handleChange} />
          {"Total Grain Weight (Pounds)"}
          <input type="text" name="weight" className="ABV-form" onChange={this.handleChange} />
          {"Grain Temperature (°F)"}
          <input type="text" name="gtemp" className="ABV-form" onChange={this.handleChange} />
          {"Target Mash Temperature (°F)"}
          <input type="text" name="tmtemp" className="ABV-form" onChange={this.handleChange} />
          {"Thermal Loss Temperature (°F)"}
          <input type="text" name="tltemp" className="ABV-form" onChange={this.handleChange} />
          <input type="submit" value="Submit" name="sub" onClick={this.handleSubmit}/>
          <p>{"Strike Water Temperature: " + this.state.stemp.toFixed(2) + "°F"}</p>
        </label>
      </form>
    );
  }
}

export default Strike_Form;
