import React from 'react';
import {AddButton} from '../fields/common-fields';
import {AddMultiField} from '../fields/multi-fields';

class AddMultiComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      values: this.resetValues(props.fields),
      showField: false,
    }
  }

  resetValues(fields) {
    return fields.reduce((map, obj) => {
      map[obj.key] = obj.value;
      return map;
    }, {});
  }

  handleChange(key, event) {
    const values = this.state.values;
    values[key] = event.target.value;
    this.setState({values: values});
  }

  handleClick(event) {
    const showField = this.state.showField;
    if (!showField) {
      this.setState({showField: true, values: this.resetValues(this.props.fields)});
    } else {
      const values = this.state.values;
      this.props.onAdd(values);
      this.setState({showField: false, values: this.resetValues(this.props.fields)});
    }
  }

  handleCancel(event) {
    this.setState({showField: false});
  }

  render() {
    const showField = this.state.showField;
    const fields = this.props.fields;
    const values = this.state.values;
    if (showField) {
      return <AddMultiField fields={fields}
                            values={values}
                            onSubmit={(e) => this.handleClick(e)}
                            onChange={(k,e) => this.handleChange(k,e)}
                            onCancel={(e) => this.handleCancel(e)} />
    } else {
      return <AddButton onClick={() => this.handleClick()} />
    }
  }
}

export default AddMultiComponent;