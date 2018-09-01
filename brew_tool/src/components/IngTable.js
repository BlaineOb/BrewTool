import React, { Component } from 'react';
import {BootstrapTable,TableHeaderColumn} from 'react-bootstrap-table';
import '../Table.css';
import '../../node_modules/react-bootstrap-table/css/react-bootstrap-table.css'

class IngTable extends Component {

  constructor(props) {
    super(props);
    this.onInsertRow = this.onInsertRow.bind(this);
    this.onDeleteRow = this.onDeleteRow.bind(this);
    this.state = {
      ingredients: [],
      rows: [],
    };
  }

  componentDidMount() {
    fetch('http://localhost:8081/brew_tool/b/home/get_ingredient/' + this.props.id)
    .then((results) => {return results.json();})
    .then((data) => {console.log("data",data);this.setState({ingredients: data},
      () => {var key = 1;
        for (let i=0; i < data.length; i++){
        this.state.rows[i] = {key: key, id:this.props.id, name: this.state.ingredients[i].name, amount: this.state.ingredients[i].amount, type: this.state.ingredients[i].type};
        key++;
        }
      }
    )});
  }

  onInsertRow(row) {
    var name = row.name;
    if (row.name.includes("'")){
      name = row.name.replace(/'/g, "''");
    }
    fetch('http://localhost:8081/brew_tool/b/home/post_ingredient', {
      method: 'POST',
      mode: 'no-cors',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        recId: this.props.id,
        name: name,
        amount: row.amount,
        type: row.type,
      })
    });
  }

  onDeleteRow(row) {
    for (let j=0; j < row.length; j++){
      var i = row[j] - 1;
      var name = this.state.rows[i].name;
      if (this.state.rows[i].name.includes("%")){
        name = this.state.rows[i].name.replace(/%/g, "$");
      }
      if (name.includes("/")){
        name = name.replace(/\//g, "&");
      }
      if (name.includes("'")){
        name = name.replace(/'/g, "''");
      }
      fetch('http://localhost:8081/brew_tool/b/home/delete_ingredient/' + this.props.id + '/' + name + '/' + this.state.rows[i].amount, {
        method: 'POST',
        mode: 'no-cors',
        headers: {
          'Content-Type': 'application/json',
        },
      });
    }
  }

  render() {

    const data = [];
    var key = 1;
    for (let i=0; i < this.state.ingredients.length; i++){
      data[i] = {key: key, name: this.state.ingredients[i].name, amount: this.state.ingredients[i].amount, type: this.state.ingredients[i].type};
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
        <p className='Table-title'>Ingredients</p>
        <BootstrapTable data={data}
                        insertRow={true}
                        deleteRow={true}
                        selectRow={selectRowProp}
                        pagination = {true}
                        autovalue = {true}
                        options={options}
                        keyField = 'key'
                        className='Table'>
          <TableHeaderColumn className='Table-header' hidden hiddenOnInsert autoValue dataField='key' width='200'>
            Key
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='name'>
            Name
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='amount'>
            Amount
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='type'>
            Type
          </TableHeaderColumn>
        </BootstrapTable>
      </div>
    )
  }
}

export default IngTable;
