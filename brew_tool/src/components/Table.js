import React, { Component } from 'react';
import {BootstrapTable,TableHeaderColumn} from 'react-bootstrap-table';
import '../Table.css';
import '../../node_modules/react-bootstrap-table/css/react-bootstrap-table.css'

function rowClassNameFormat(row,rowIdx) {
  console.log(row)
  return row['name'] === 'George Michael' ? 'GeorgeMichael-Row' : 'Other-Row';
}

function onSelectRow(row,isSelected,e) {
  if (isSelected) {

  }
}

const selectRowProp = {
  mode: 'radio',
  clickToSelect: true,
  unselectable: [],
  selected: [],
  onSelect: onSelectRow,
  bgColor: 'gold'
};

function onInsertRow(row) {
  let newRowStr = ''

  for (const prop in row) {
  }
}

function isExpandableRow(row) {
  return row['name'];
}

function expandRow(row) {
  return (
    <p>{row['name']}
    </p>
  );
}


class Table extends Component {

  constructor(props) {
    super(props);
    this.state = {
      ingredients: []
    };
  }

  componentDidMount() {
    fetch('http://localhost:8081/brew_tool/b/home/get_ingredient')
    .then((results) => {return results.json();})
    .then((data) => {console.log("data",data);this.setState({ingredients: data},
      () => console.log("state",this.state.ingredients)
    )});
  }

  render() {

    const data = [];
    for (let i=0; i < this.state.ingredients.length; i++){
      data[i] = {id: this.state.ingredients[i].ingId, name: this.state.ingredients[i].ingName, value: i * 5};
    }
    const options = {
      page: 1,
      bgColor: 'lightblue',
      prePage:  '⟵',
      nextPage: '⟶',
      firstPage: '⟸',
      lastPage: '⟹',
      expandRowBgColor: 'gold',
      afterInsertRow: onInsertRow,
    }
    return (
      <div>
        <BootstrapTable data={data}
                        insertRow={true}
                        expandableRow={isExpandableRow}
                        expandComponent={expandRow}
                        expandColumnOptions={
                            {expandColumnVisible: true}}
                        pagination = {true}
                        options={options}
                        className='Table'>
          <TableHeaderColumn className='Table-header' isKey dataField='id'>
            ID
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='name'>
            Name
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='value'>
            Value
          </TableHeaderColumn>
        </BootstrapTable>
      </div>
    )
  }
}

export default Table;
