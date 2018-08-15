import React, { Component } from 'react';
import Calendar, { TileContent } from 'react-calendar';
import '../App.css';

class My_Calendar extends Component{
  render(){
    return(
      <Calendar TileContent="hello">
        <TileContent>{({ date, view }) => view === 'month' && date.getDay() === 0 ? <p>Its Sunday!</p> : null}</TileContent>
      </Calendar>
    )
  }
}

export default Calendar;
