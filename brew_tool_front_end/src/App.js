import React, { Component } from 'react';
import './App.css';
import List from './components/List';
import IngTable from './components/IngTable';
import BoilTable from './components/BoilTable';
import StatsTable from './components/StatsTable';
import MiscTable from './components/MiscTable';
import MashTable from './components/MashTable';
import MyCalendar from './components/My_Calendar';
import AbvPage from './components/Abv_Page';
import {Button} from 'react-bootstrap';
import MyModal from './components/My_Modal';
import DeleteModal from './components/Delete_Modal';


class App extends Component {
  constructor(props, context) {
    super(props, context);
    this.handler = this.handler.bind(this);
    this.home = this.home.bind(this);
    this.abv = this.abv.bind(this);
    this.calendar = this.calendar.bind(this);
    this.state = {
      showList: true,
      showTable: false,
      showAddButton: true,
      showDeleteButton: true,
      showAbvPage: false,
      showCalendar: false,
      currRecDate: '',
      currRecID: -1,
    }
  }

  handler(recDate,recID) {
    var recDateForPath = recDate.replace(/\//g , "@"); /*Switch forward slashes to @ to use as a path param*/
    this.setState({showList: false, showAddButton: false, showDeleteButton: false, showTable: true, showCalendar: false, currRecDate: recDateForPath, currRecID: recID});
  }

  home(e) {
    this.setState({showList: true, showAddButton: true, showDeleteButton: true, showTable: false, showAbvPage: false, showCalendar: false});
  }

  abv(e) {
    this.setState({showList: false, showAddButton: false, showDeleteButton: false, showTable: false, showAbvPage: true, showCalendar: false});
  }

  calendar(e){
    this.setState({showList: false, showAddButton: false, showDeleteButton: false, showTable: false, showAbvPage: false, showCalendar: true});
  }

  render() {

    var homeList,table,calendar,add,del,abv;
    if (this.state.showList) {
      homeList = <List handler={this.handler}/>
    }
    if (this.state.showTable) {
      table = <div>
                <IngTable recDate={this.state.currRecDate} id={this.state.currRecID}/>
                <MashTable recDate={this.state.currRecDate} id={this.state.currRecID}/>
                <BoilTable recDate={this.state.currRecDate} id={this.state.currRecID}/>
                <StatsTable recDate={this.state.currRecDate} id={this.state.currRecID}/>
                <MiscTable recDate={this.state.currRecDate} id={this.state.currRecID}/>
              </div>;

    }
    if (this.state.showCalendar){
      calendar = <MyCalendar/>
    }
    if (this.state.showAddButton){
      add = <MyModal/>
    }
    if (this.state.showDeleteButton){
      del = <DeleteModal/>
    }
    if (this.state.showAbvPage){
      abv = <AbvPage/>
    }

    return (
      <div>
      <div className="App">
        <p className="App-title">BrewTool</p>
        <Button className="Home-button" onClick={this.home} bsStyle="danger">Home</Button>
        <Button className="Calculators-button" onClick={this.abv} bsStyle="danger">Calculators</Button>
        <Button className="Calendar-button" onClick={this.calendar} bsStyle="danger">Calendar</Button>

        {homeList}
        {table}
        {calendar}
        {add}
        {del}
        {abv}
      </div>

      </div>
    );
  }
}

export default App;
