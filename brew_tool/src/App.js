import React, { Component } from 'react';
import './App.css';
import List from './components/List';
import Table from './components/Table';
import Table2 from './components/Table2';
import My_Calendar from './components/My_Calendar';
import Abv_Page from './components/Abv_Page';
import New_Brew_Form from './components/New_Brew_Form';
import {Button} from 'react-bootstrap';
import My_Modal from './components/My_Modal';


class App extends Component {
  constructor(props, context) {
    super(props, context);
    this.handler = this.handler.bind(this);
    this.home = this.home.bind(this);
    this.abv = this.abv.bind(this);
    this.state = {
      showList: true,
      showTable: false,
      showAddButton: true,
      showAbvPage: false,
      showCalendar: false,
      currRecDate: '',
    }
  }

  handler(recDate) {
    this.setState({showList: false, showAddButton: false, showTable: true, currRecDate: recDate});
  }

  home(e) {
    this.setState({showList: true, showAddButton: true, showTable: false, showAbvPage: false});
  }

  abv(e) {
    this.setState({showList: false, showAddButton: false, showTable: false, showAbvPage: true});
  }

  render() {

    var homeList,table,calendar,add,abv;
    if (this.state.showList) {
      homeList = <List handler={this.handler}/>
    }
    if (this.state.showTable) {
      table = <div>
                <Table recDate={this.state.currRecDate}/>
                <Table2 recDate={this.state.currRecDate}/>
              </div>;

    }
    if (this.state.showCalendar){
      calendar = <My_Calendar className="Calendar"/>
    }
    if (this.state.showAddButton){
      add = <My_Modal/>
    }
    if (this.state.showAbvPage){
      abv = <Abv_Page/>
    }

    return (
      <div>
      <div className="App">
        <p className="App-title">BrewTool</p>
        <Button className="Home-button" onClick={this.home} bsStyle="danger">Home</Button>
        <Button className="ABV-button" onClick={this.abv} bsStyle="danger">ABV Calculator</Button>

        {homeList}
        {table}
        {calendar}
        {add}
        {abv}
      </div>

      </div>
    );
  }
}

export default App;
