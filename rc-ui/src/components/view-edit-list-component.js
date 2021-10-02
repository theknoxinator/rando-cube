import React from 'react';
import AddMultiComponent from './add-multi-component';
import ViewEditMultiComponent from './view-edit-multi-component';

class ViewEditListComponent extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    const list = this.props.list;
    const renderedList = list.map((item) => {
      return (
        <ViewEditMultiComponent key={item.id}
                                values={item.values}
                                fieldsForDelete={this.props.fieldsForDelete}
                                fieldsForEdit={this.props.fieldsForEdit}
                                fieldsForView={this.props.fieldsForView}
                                deleteOptions={this.props.deleteOptions}
                                deleteOptionsLabel={this.props.deleteOptionsLabel}
                                onEdit={(e,f) => this.props.onEdit(e,f)}
                                onDelete={(e,f) => this.props.onDelete(e,f)} />
      );
    });

    return (
      <div>
        {renderedList}
        {this.props.fieldsForAdd &&
          <AddMultiComponent fields={this.props.fieldsForAdd}
                             onAdd={(e) => this.props.onAdd(e)} />
        }
      </div>
    );
  }
}

export default ViewEditListComponent;