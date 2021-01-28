import React, { Component } from 'react';
import BigCalendar from 'react-big-calendar';
import moment from 'moment';
import Modal from 'react-bootstrap-modal';
import NewEventForm from './NewEventForm';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import '../../node_modules/react-bootstrap-modal/lib/css/rbm-complete.css';
import '../App.css';

BigCalendar.momentLocalizer(moment);

/*SEND THE EVENT INPUT BACK THROUGH A PROP FUNCTION FROM NewEventForm*/
class My_Calendar extends Component{

  constructor(props){
    super(props);
    this.handleChange = this.handleChange.bind(this);
    this.onSelectSlot = this.onSelectSlot.bind(this);
    this.onSelectEvent = this.onSelectEvent.bind(this);
    this.deleteEvent = this.deleteEvent.bind(this);
    this.state = {
      open: false,
      open2: false,
      event_name: '',
      start_date: '',
      end_date: '',
      curr_event: null,
      events: [],
    }
  }

  componentWillMount(){
    fetch('http://localhost:8081/brew_tool/b/home/get_event')
      .then((results) => {return results.json();})
      .then((data) => {console.log("EVENTS",data);this.setState({events_tmp: data},
        () => { var built = [];
          for (let i=0; i < this.state.events_tmp.length; i++){
            let sd = this.state.events_tmp[i].start.split('/');
            let ed = this.state.events_tmp[i].end.split('/');
            for (let i=0; i < sd.length; i++){
              sd[i] = parseInt(sd[i],10);
              ed[i] = parseInt(ed[i],10);
            }
            built[i] = {id: this.state.events_tmp[i].id,
                        title: this.state.events_tmp[i].title,
                        start: new Date(sd[2], sd[0]-1, sd[1], 0, 0, 0),
                        end: new Date(ed[2], ed[0]-1, ed[1], 0, 0, 0)};
            this.setState({events: built}, console.log("ESTATE",this.state.events));
          }
        }
    )});
  }

  onSelectSlot() {
    this.setState({open: true});
  }

  onSelectEvent(event) {
    this.setState({open2: true, curr_event:event});
  }

  deleteEvent(){
    this.setState({open2: false});
    fetch('http://localhost:8081/brew_tool/b/home/delete_event/' + this.state.curr_event.id, {
      method: 'POST',
      mode: 'no-cors',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    var index = -1;
    for (let i=0; i < this.state.events.length; i++){
      if (this.state.events[i].id === this.state.curr_event.id) {
        index = i;
        break;
      }
    }
    this.state.events.splice(index,1);
  }

  handleChange(ev_nm,st_dt,e_dt){
    this.setState({event_name:ev_nm,start_date:st_dt,end_date:e_dt});
  }

  findUnusedId(){
    console.log("EVENTS",this.state.events);
    for (let i=0; i < this.state.events.length; i++){
      if (!(this.state.events[i].id === i)){
        return i;
      }
    }
    return this.state.events.length;
  }

  handleSave(){

    if (this.state.start_date.length !== 10 || this.state.end_date.length !== 10 ||
        (this.state.start_date.match(/\//g)||[]).length !== 2 || (this.state.end_date.match(/\//g)||[]).length !== 2) {
        alert("Use correct date format");
        return;
    }

    var sd = this.state.start_date.split('/');
    var ed = this.state.end_date.split('/');

    for (let i=0; i < sd.length; i++){
      sd[i] = parseInt(sd[i],10);
      ed[i] = parseInt(ed[i],10);
    }

    var curr_id = this.findUnusedId();
    const ev = {
      id: curr_id,
      title: this.state.event_name,
      start: new Date(sd[2], sd[0]-1, sd[1], 0, 0, 0),
      end: new Date(ed[2], ed[0]-1, ed[1], 0, 0, 0),
    };

    fetch('http://localhost:8081/brew_tool/b/home/post_event', {
      method: 'POST',
      mode: 'no-cors',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        id: curr_id,
        title: this.state.event_name,
        start: this.state.start_date,
        end: this.state.end_date,
      })
    });

    this.setState({events: [...this.state.events, ev]});
  }

    render() {
      let closeModal = () => this.setState({ open: false, open2: false})

      let saveAndClose = () => {
        this.setState({ open: false});
        this.handleSave();
      }

      return (
        <div className='Calendar'>
          <BigCalendar
            events={this.state.events}
            startAccessor='start'
            endAccessor='end'
            selectable = {true}
            onSelectSlot = {this.onSelectSlot}
            onSelectEvent = {this.onSelectEvent}
          />
          <Modal
            id="modal1"
            show={this.state.open}
            onHide={closeModal}
            aria-labelledby="ModalHeader"
          >
            <Modal.Header>
              <Modal.Title id='ModalHeader'>Add Event</Modal.Title>
            </Modal.Header>
            <Modal.Body>
              <p>Dates are MM/DD/YYYY</p>
              <NewEventForm change={this.handleChange}/>
            </Modal.Body>
            <Modal.Footer>
              <Modal.Dismiss className='btn btn-default'>Cancel</Modal.Dismiss>
              <button className='btn btn-primary' onClick={saveAndClose}>
                Save
              </button>
            </Modal.Footer>
          </Modal>

          <Modal
            id="modal2"
            show={this.state.open2}
            onHide={closeModal}
            aria-labelledby="ModalHeader"
          >
            <Modal.Header>
            </Modal.Header>
            <Modal.Body>
              Delete Event?
            </Modal.Body>
            <Modal.Footer>
              <Modal.Dismiss className='btn btn-default'>No</Modal.Dismiss>
              <button className='btn btn-primary' onClick={this.deleteEvent}>
                Yes
              </button>
            </Modal.Footer>
          </Modal>
        </div>
      );
    }
}

export default My_Calendar;
