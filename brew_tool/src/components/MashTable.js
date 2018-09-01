import React, { Component } from 'react';
import {BootstrapTable,TableHeaderColumn} from 'react-bootstrap-table';
import '../Table.css';
import '../../node_modules/react-bootstrap-table/css/react-bootstrap-table.css'

class MashTable extends Component {

  constructor(props) {
    super(props);
    this.onInsertRow = this.onInsertRow.bind(this);
    this.onDeleteRow = this.onDeleteRow.bind(this);
    this.state = {
      filled: false,
      mash: [],
    };
  }

  componentDidMount() {
    fetch('http://localhost:8081/brew_tool/b/home/get_mash/' + this.props.id)
    .then((results) => {return results.json();})
    .then((data) => {console.log("mash",data);this.setState({mash: data},
      () => {console.log("MashState",this.state.mash)
      if (this.state.mash.length > 0) this.setState({filled:true});}
    )});
  }

  onInsertRow(row) {
    if (!this.state.filled) {
      this.setState({filled:true});
      fetch('http://localhost:8081/brew_tool/b/home/post_mash', {
        method: 'POST',
        mode: 'no-cors',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          id: this.props.id,
          miStrike: row.miStrike,
          miTarg: row.miTarg,
          miAct: row.miAct,
          moStrike: row.moStrike,
          moTarg: row.moTarg,
          moAct: row.moAct,
        })
      });
    } else {
      alert('Stats already given to this brew. Delete old ones if you want to update them. This entry will appear initially, but will disappear after refreshing');
    }
  }

  onDeleteRow(row) {
    fetch('http://localhost:8081/brew_tool/b/home/delete_mash/' + this.props.id, {
      method: 'POST',
      mode: 'no-cors',
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  render() {

    const data = [];
    var key = 1;
    for (let i=0; i < this.state.mash.length; i++){
      data[i] = {key: key,
                miStrike: this.state.mash[i].miStrike,
                miTarg: this.state.mash[i].miTarget,
                miAct: this.state.mash[i].miActual,
                moStrike: this.state.mash[i].moStrike,
                moTarg: this.state.mash[i].moTarget,
                moAct: this.state.mash[i].moActual
              };
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
        <p className='Table-title'>Mash</p>
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
          <TableHeaderColumn className='Table-header' dataField='miStrike'>
            MI-Strike Temp
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='miTarg'>
            MI-Target
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='miAct'>
            MI-Actual
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='moStrike'>
            MO-Strike Temp
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='moTarg'>
            MO-Target
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='moAct'>
            MO-Actual
          </TableHeaderColumn>
        </BootstrapTable>
      </div>
    )
  }
}

export default MashTable;
