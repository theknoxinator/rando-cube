import React from 'react';
import {DeleteMultiField, EditMultiField, ViewMultiField} from '../fields/multi-fields';

class ViewEditMultiComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      values: props.values,
      newValues: {},
      deleteOption: '',
      editField: false,
      deleteField: false,
    }
  }

  resetValues(fields) {
    return fields.reduce((map, obj) => {
      map[obj.key] = obj.value;
      return map;
    }, {});
  }

  handleEditClick(event) {
    const values = this.state.values;
    this.setState({editField: true, newValues: values});
  }

  handleValueChange(key, event) {
    const values = this.state.newValues;
    values[key] = event.target.value;
    this.setState({newValues: values});
  }

  handleEditConfirm(event) {
    const values = this.state.values;
    const newValues = this.state.newValues;
    this.props.onEdit(values, newValues);
    this.setState({editField: false, values: newValues, newValues: {}});
  }

  handleDeleteClick(event) {
    this.setState({deleteField: true});
  }

  handleOptionChange(event) {
    this.setState({deleteOption: event.target.value});
  }

  handleDeleteConfirm(event) {
    const values = this.state.values;
    const deleteOption = this.state.deleteOption;
    this.props.onDelete(values, deleteOption);
  }

  handleCancel(event) {
    this.setState({editField: false, deleteField: false, newValues: {}});
  }

  render() {
    const editField = this.state.editField;
    const deleteField = this.state.deleteField;
    if (editField) {
      return <EditMultiField values={this.state.newValues}
                             fields={this.props.fieldsForEdit}
                             onSubmit={(e) => this.handleEditConfirm(e)}
                             onChange={(k,e) => this.handleValueChange(k,e)}
                             onCancel={(e) => this.handleCancel(e)} />
    } else if (deleteField) {
      return <DeleteMultiField values={this.state.values}
                               fields={this.props.fieldsForDelete}
                               options={this.props.deleteOptions}
                               optionsLabel={this.props.deleteOptionsLabel}
                               onSubmit={(e) => this.handleDeleteConfirm(e)}
                               onChange={(e) => this.handleOptionChange(e)}
                               onCancel={(e) => this.handleCancel(e)} />
    } else {
      return <ViewMultiField values={this.state.values}
                             fields={this.props.fieldsForView}
                             onEdit={(e) => this.handleEditClick(e)}
                             onDelete={(e) => this.handleDeleteClick(e)} />
    }
  }
}

export default ViewEditMultiComponent;