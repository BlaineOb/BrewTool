import React, { Component } from 'react';
import {ListGroup,ListGroupItem} from 'react-bootstrap';
import '../App.css';



class List extends Component {
  constructor() {
    super();
    this.state = {
      recipes: []
    }
  }

  componentDidMount() {
    fetch('http://localhost:8081/brew_tool/b/home/get_recipe')
    .then((results) => {return results.json();})
    .then((data) => {console.log("data",data);
      let recipes = data.map((rec) => {
        return (
          <ListGroupItem key={rec.recDate} className="List" onClick={() => this.props.handler(rec.recDate,rec.recID)}><pre className="Lista">{rec.recName}  |  {rec.recDate}</pre></ListGroupItem>
        )
      })
      this.setState({recipes: recipes});
    });
  }

  render() {
    return(
        <div>
          <ListGroup className="List">
            {this.state.recipes}
          </ListGroup>
        </div>
    )
  }
}

export default List;
