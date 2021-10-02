import React from 'react';
import {DeleteMultiField, MarkMultiField, ViewMultiField} from '../fields/multi-fields';

class ViewUnmarkMultiComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      values: props.values,
      deleteOption: '',
      deleteField: false,
      unmarkField: false,
    }
  }

  handleUnmarkClick(event) {
    this.setState({unmarkField: true});
  }

  handleUnmarkConfirm(event) {
    const values = this.state.values;
    this.props.onUnmark(values);
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
    this.setState({deleteField: false, unmarkField: false});
  }

  render() {
    const unmarkField = this.state.unmarkField;
    const deleteField = this.state.deleteField;
    if (unmarkField) {
      return <MarkMultiField values={this.state.values}
                             fields={this.props.fieldsForView}
                             onSubmit={(e) => this.handleUnmarkConfirm(e)}
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
                             editIcon="bi-recycle"
                             onEdit={(e) => this.handleUnmarkClick(e)}
                             onDelete={(e) => this.handleDeleteClick(e)} />
    }
  }
}

export default ViewUnmarkMultiComponent;