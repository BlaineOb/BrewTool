import React, { Component } from 'react';
import {BootstrapTable,TableHeaderColumn} from 'react-bootstrap-table';
import '../Table.css';
import '../../node_modules/react-bootstrap-table/css/react-bootstrap-table.css'

function onInsertRow(row) {

}

function onDeleteRow(row) {
  alert("HELLO");
  row.forEach(
    function(element){
      fetch('http://localhost:8081/brew_tool/b/home/delete_stats/' + element, {
        method: 'POST',
        mode: 'no-cors',
        headers: {
          'Content-Type': 'application/json',
        },
      });
    }
  );
}

function a(row) {
  alert("EEE");
}


class StatsTable extends Component {

  constructor(props) {
    super(props);
    this.state = {
      stats: [],
    };
  }

  componentDidMount() {
    fetch('http://localhost:8081/brew_tool/b/home/get_stats/' + this.props.recDate)
      .then((results) => {return results.json();})
      .then((data) => {console.log("stats",data);this.setState({stats: [data]},
        () => console.log("StatsState",this.state.stats)
    )});
  }

  render() {


    const data = [];

    /*
    if (this.state.stats.length > 0 && this.state.stats[0].length != 0) {
      data[0] = {id: this.state.stats[0].id,
                og: this.state.stats[0].og,
                fg: this.state.stats[0].fg,
                abv: this.state.stats[0].abv,
                atten: this.state.stats[0].atten};
    }
    */


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
      afterInsertRow: onInsertRow,
      afterDeleteRow: a,
    }
    return (
      <div>
        <p className='Table-title'>Stats</p>
        <BootstrapTable data={data}
                        insertRow={true}
                        deleteRow={true}
                        selectRow={selectRowProp}
                        pagination = {true}
                        className='Table'>
          <TableHeaderColumn className='Table-header' isKey dataField='id'>
            ID
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='og'>
            OG
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='fg'>
            FG
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='abv'>
            ABV
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='atten'>
            Attenuation
          </TableHeaderColumn>
        </BootstrapTable>
      </div>
    )
  }
}

export default StatsTable;
