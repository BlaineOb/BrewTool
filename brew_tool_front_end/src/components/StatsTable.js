import React, { Component } from 'react';
import {BootstrapTable,TableHeaderColumn} from 'react-bootstrap-table';
import '../Table.css';
import '../../node_modules/react-bootstrap-table/css/react-bootstrap-table.css'

class StatsTable extends Component {

  constructor(props) {
    super(props);
    this.onInsertRow = this.onInsertRow.bind(this);
    this.onDeleteRow = this.onDeleteRow.bind(this);
    this.state = {
      filled: false,
      stats: [],

    };
  }

  onDeleteRow(row) {
    fetch('http://localhost:8081/brew_tool/b/home/delete_stats/' + this.props.id, {
      method: 'POST',
      mode: 'no-cors',
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  onInsertRow(row) {
    if (!this.state.filled){
      this.setState({filled:true});
      fetch('http://localhost:8081/brew_tool/b/home/post_stats', {
        method: 'POST',
        mode: 'no-cors',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          id: this.props.id,
          og: row.og,
          fg: row.fg,
          abv: row.abv,
          atten: row.atten,
        })
      });

    } else {
      alert('Stats already given to this brew. Delete old ones if you want to update them. This entry will appear initially, but will disappear after refreshing');
    }
  }

  componentDidMount() {
    fetch('http://localhost:8081/brew_tool/b/home/get_stats/' + this.props.id)
      .then((results) => {return results.json();})
      .then((data) => {console.log("stats",data);this.setState({stats: [data]},
        () => {console.log("StatsState",this.state.stats);
        if (!Array.isArray(data)) this.setState({filled:true});}
    )});
  }

  render() {

    const data = [];
    var key = 1;
    if (this.state.stats.length > 0 && this.state.stats[0].length !== 0) {
      data[0] = {key: key,
                og: this.state.stats[0].og,
                fg: this.state.stats[0].fg,
                abv: this.state.stats[0].abv,
                atten: this.state.stats[0].atten};
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
      expandRowBgColor: 'gold',
      afterInsertRow: this.onInsertRow,
      afterDeleteRow: this.onDeleteRow,
    }
    return (
      <div>
        <p className='Table-title'>Stats</p>
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
           <TableHeaderColumn className='Table-header'  dataField='og'>
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
