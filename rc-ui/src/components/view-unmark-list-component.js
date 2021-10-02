import React from 'react';
import AddMultiComponent from './add-multi-component';
import ViewUnmarkMultiComponent from './view-unmark-multi-component';

class ViewUnmarkListComponent extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    const list = this.props.list;
    const renderedList = list.map((item) => {
      return (
        <ViewUnmarkMultiComponent key={item.id}
                                  values={item.values}
                                  fieldsForDelete={this.props.fieldsForDelete}
                                  fieldsForView={this.props.fieldsForView}
                                  deleteOptions={this.props.deleteOptions}
                                  deleteOptionsLabel={this.props.deleteOptionsLabel}
                                  onUnmark={(e) => this.props.onUnmark(e)}
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

export default ViewUnmarkListComponent;