import React, { Component } from 'react';
import {BootstrapTable,TableHeaderColumn} from 'react-bootstrap-table';
import '../Table.css';
import '../../node_modules/react-bootstrap-table/css/react-bootstrap-table.css'


class BoilTable extends Component {

  constructor(props) {
    super(props);
    this.onInsertRow = this.onInsertRow.bind(this);
    this.onDeleteRow = this.onDeleteRow.bind(this);
    this.state = {
      boil: [],
      rows: [],
    };
  }

  componentDidMount() {
    fetch('http://localhost:8081/brew_tool/b/home/get_boil/' + this.props.id)
      .then((results) => {return results.json();})
      .then((data) => {console.log("boil",data);this.setState({boil: data},
        () => {var key = 1;
          for (let i=0; i < data.length; i++){
          this.state.rows[i] = {key: key, id:this.props.id, time: this.state.boil[i].time, action: this.state.boil[i].action};
          key++;
        }console.log("ROWS",this.state.rows);}
    )});
  }

  onInsertRow(row) {
    var time = row.time;
    if (row.time.includes("'")){
      time = row.time.replace(/'/g, "''");
    }
    var action = row.action;
    if (row.action.includes("'")){
      action = row.action.replace(/'/g, "''");
    }
    fetch('http://localhost:8081/brew_tool/b/home/post_boil', {
      method: 'POST',
      mode: 'no-cors',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        id: this.props.id,
        time: time,
        action: action,
      })
    });
  }

  onDeleteRow(row) {
    for (let j=0; j < row.length; j++){
      var i = row[j] - 1;
      var time = this.state.rows[i].time;
      if (this.state.rows[i].time.includes("%")){
        time = this.state.rows[i].time.replace(/%/g, "$");
      }
      if (time.includes("/")){
        time = time.replace(/\//g, "&");
      }
      if (time.includes("'")){
        time = time.replace(/'/g, "''");
      }
      var action = this.state.rows[i].action;
      if (this.state.rows[i].action.includes("%")){
        action = this.state.rows[i].action.replace(/%/g, "$");
      }
      if (action.includes("/")){
        action = action.replace(/\//g, "&");
      }
      if (action.includes("'")){
        action = action.replace(/'/g, "''");
      }
      alert(time + " " + action);
      fetch('http://localhost:8081/brew_tool/b/home/delete_boil/' + this.state.rows[i].id + '/' + time + '/' + action, {
        method: 'POST',
        mode: 'no-cors',
        headers: {
          'Content-Type': 'application/json',
        },
      });
    }
  }

  render() {

    var data = [];
    var key = 1;
    for (let i=0; i < this.state.boil.length; i++){
      data[i] = {key: key, time: this.state.boil[i].time, action: this.state.boil[i].action};
      key++;
    }
    const selectRowProp = {
      mode: 'checkbox'
    };
    const options = {
      page: 1,
      bgColor: 'lightblue',
      prePage:  '⟵',
      nextPage: '⟶',
      firstPage: '⟸',
      lastPage: '⟹',
      expandRowBgColor: 'lightblue',
      afterInsertRow: this.onInsertRow,
      afterDeleteRow: this.onDeleteRow,
    }
    return (
      <div>
        <p className='Table-title'>Boil</p>
        <BootstrapTable data={data}
                        insertRow={true}
                        deleteRow={true}
                        selectRow={selectRowProp}
                        pagination = {true}
                        options={options}
                        keyField = 'key'
                        className='Table'>
          <TableHeaderColumn className='Table-header' hidden hiddenOnInsert autoValue dataField='key' width='60'>
            Key
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='time'>
            Time
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='action'>
            Action
          </TableHeaderColumn>
        </BootstrapTable>
      </div>
    )
  }
}

export default BoilTable;
