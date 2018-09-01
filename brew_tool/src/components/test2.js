import React, { Component } from 'react';
import {BootstrapTable,TableHeaderColumn} from 'react-bootstrap-table';
import '../Table.css';
import '../../node_modules/react-bootstrap-table/css/react-bootstrap-table.css'

function onInsertRow(row) {

}

function onDeleteRow(row) {
  row.forEach(
    function(element){
      fetch('http://localhost:8081/brew_tool/b/home/delete_misc/' + element + '/' + row.comment, {
        method: 'POST',
        mode: 'no-cors',
        headers: {
          'Content-Type': 'application/json',
        },
      });
    }
  );
}


class MiscTable extends Component {

  constructor(props) {
    super(props);
    this.state = {
      comments: []
    };
  }

  componentDidMount() {
    fetch('http://localhost:8081/brew_tool/b/home/get_misc/' + this.props.id)
    .then((results) => {return results.json();})
    .then((data) => {console.log("misc",data);this.setState({comments: data},
      () => console.log("MiscState",this.state.ingredients)
    )});
  }

  render() {

    const data = [];
    for (let i=0; i < this.state.comments.length; i++){
      data[i] = {id: this.props.id, comment: this.state.comments[i].comment};
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
      afterInsertRow: onInsertRow,
      afterDeleteRow: onDeleteRow,
    }
    return (
      <div>
        <p className='Table-title'>Miscellaneous</p>
        <p className='Recipe-id'>Recipe id is {this.props.id} (Use for ID field)</p>
        <BootstrapTable data={data}
                        insertRow={true}
                        deleteRow={true}
                        selectRow={selectRowProp}
                        pagination = {true}
                        options={options}
                        className='Table'>
          <TableHeaderColumn className='Table-header' isKey dataField='id' width='50'>
            ID
          </TableHeaderColumn>
          <TableHeaderColumn className='Table-header' dataField='comment'>
            Comment
          </TableHeaderColumn>
        </BootstrapTable>
      </div>
    )
  }
}

export default MiscTable;
