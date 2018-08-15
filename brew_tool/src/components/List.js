import React, { Component } from 'react';
import {ListGroup,ListGroupItem} from 'react-bootstrap';
import {Route, Switch, BrowserRouter} from 'react-router-dom';
import App from '../App';
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
          <ListGroupItem className="List" onClick={() => this.props.handler(rec.recDate)}><pre className="Lista">{rec.recName}  |  {rec.recDate}</pre></ListGroupItem>
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
