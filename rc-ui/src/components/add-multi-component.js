import React from 'react';
import {AddButton} from '../fields/common-fields';
import {AddSingleField} from '../fields/single-fields';

class AddMultiComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      value: '',
      showField: false,
    }
  }

  handleChange(event) {
    this.setState({value: event.target.value});
  }

  handleClick(event) {
    const showField = this.state.showField;
    if (!showField) {
      this.setState({showField: true, value: ''});
    } else {
      const category = this.state.value;
      this.props.onAdd(category);
      this.setState({showField: false, value: ''});
    }
  }

  handleCancel(event) {
    this.setState({showField: false});
  }

  render() {
    const showField = this.state.showField;
    if (showField) {
      return <AddSingleField value={this.state.value}
                             onSubmit={(e) => this.handleClick(e)}
                             onChange={(e) => this.handleChange(e)}
                             onCancel={(e) => this.handleCancel(e)} />
    } else {
      return <AddButton onClick={() => this.handleClick()} />
    }
  }
}

export default AddMultiComponent;