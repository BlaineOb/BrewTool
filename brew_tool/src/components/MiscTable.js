import React, { Component } from 'react';
import {BootstrapTable,TableHeaderColumn} from 'react-bootstrap-table';
import '../Table.css';
import '../../node_modules/react-bootstrap-table/css/react-bootstrap-table.css'


class MiscTable extends Component {

  constructor(props) {
    super(props);
    this.onInsertRow = this.onInsertRow.bind(this);
    this.onDeleteRow = this.onDeleteRow.bind(this);
    this.state = {
      comments: [],
      rows: [],
    };
  }

  onInsertRow(row) {
    var comment = row.comment;
    if (row.comment.includes("'")){
      comment = row.comment.replace(/'/g, "''");
    }
    fetch('http://localhost:8081/brew_tool/b/home/post_misc', {
      method: 'POST',
      mode: 'no-cors',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        id: this.props.id,
        com: comment,
      })
    });
  }

  onDeleteRow(row) {
    for (let j=0; j < row.length; j++){
      var i = row[j] - 1;
      var comment = this.state.rows[i].comment;
      if (this.state.rows[i].comment.includes("'")){
        comment = this.state.rows[i].comment.replace(/'/g, "''");
      }
      if (comment.includes("%")){
        comment = comment.replace(/%/g, "$");
      }
      if (comment.includes("/")){
        comment = comment.replace(/\//g, "&");
      }
      fetch('http://localhost:8081/brew_tool/b/home/delete_misc/' + this.props.id + '/' + comment, {
          method: 'POST',
          mode: 'no-cors',
          headers: {
            'Content-Type': 'application/json',
          },
      });
    }
  }

  componentDidMount() {
    fetch('http://localhost:8081/brew_tool/b/home/get_misc/' + this.props.id)
    .then((results) => {return results.json();})
    .then((data) => {console.log("misc",data);this.setState({comments: data},
      () => {var key = 1;
        for (let i=0; i < data.length; i++){
        this.state.rows[i] = {key: key, id:this.props.id, comment: this.state.comments[i].comment};
        key++;
        }
      }
    )});
  }

  render() {

    const data = [];
    var key = 1;
    for (let i=0; i < this.state.comments.length; i++){
      data[i] = {key: key, comment: this.state.comments[i].comment};
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
        <p className='Table-title'>Miscellaneous</p>
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
          <TableHeaderColumn className='Table-header' dataField='comment'>
            Comment
          </TableHeaderColumn>
        </BootstrapTable>
      </div>
    )
  }
}

export default MiscTable;
