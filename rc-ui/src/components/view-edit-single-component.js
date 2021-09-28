import React from 'react';
import {DeleteSingleField, EditSingleField, ViewSingleField} from '../fields/single-fields';

class ViewEditSingleComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      value: props.value,
      newValue: '',
      deleteOption: '',
      editField: false,
      deleteField: false,
    }
  }

  handleEditClick(event) {
    const value = this.state.value;
    this.setState({editField: true, newValue: value});
  }

  handleValueChange(event) {
    this.setState({newValue: event.target.value});
  }

  handleEditConfirm(event) {
    const value = this.state.value;
    const newValue = this.state.newValue;
    this.props.onEdit(value, newValue);
    this.setState({editField: false, value: newValue, newValue: ''});
  }

  handleDeleteClick(event) {
    this.setState({deleteField: true});
  }

  handleOptionChange(event) {
    this.setState({deleteOption: event.target.value});
  }

  handleDeleteConfirm(event) {
    const value = this.state.value;
    const deleteOption = this.state.deleteOption;
    this.props.onDelete(value, deleteOption);
  }

  handleCancel(event) {
    this.setState({editField: false, deleteField: false, newValue: ''});
  }

  render() {
    const editField = this.state.editField;
    const deleteField = this.state.deleteField;
    if (editField) {
      return <EditSingleField value={this.state.newValue}
                              onSubmit={(e) => this.handleEditConfirm(e)}
                              onChange={(e) => this.handleValueChange(e)}
                              onCancel={(e) => this.handleCancel(e)} />
    } else if (deleteField) {
      return <DeleteSingleField value={this.state.value}
                                options={this.props.deleteOptions}
                                optionsLabel={this.props.deleteOptionsLabel}
                                onSubmit={(e) => this.handleDeleteConfirm(e)}
                                onChange={(e) => this.handleOptionChange(e)}
                                onCancel={(e) => this.handleCancel(e)} />
    } else {
      return <ViewSingleField value={this.state.value}
                              onEdit={(e) => this.handleEditClick(e)}
                              onDelete={(e) => this.handleDeleteClick(e)} />
    }
  }
}

export default ViewEditSingleComponent;