import React from 'react';
import AddMultiComponent from './add-multi-component';
import {ViewSingleField} from '../fields/single-fields';

class ViewEditListComponent extends React.Component {
  constructor(props) {
    super(props);
  }

  handleAddItem(fields) {
    console.log(fields);
  }

  render() {
    const list = this.props.list;
    const renderedList = list.map((item) => {
      return (
        <ViewSingleField key={item.id} value={item.title}/>
      );
    });

    return (
      <div>
        {renderedList}
        <AddMultiComponent fields={this.props.fieldsForAdd}
                           onAdd={(e) => this.handleAddItem(e)}/>
      </div>
    );
  }
}

export default ViewEditListComponent;