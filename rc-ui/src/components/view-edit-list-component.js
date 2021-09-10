import React from 'react';
import {getFullList} from '../backend';
import AddMultiComponent from './add-multi-component';

class ViewEditListComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      items: []
    }
    this.reloadItems(props.category);
  }

  reloadItems(category) {
    getFullList(category,
      (data) => this.setState({items: data}),
      (e) => this.props.onError(e)
    );
  }

  render() {
    const items = this.state.items;
    const renderedItems = items.map((item) => {
      return (
        <div/>
      );
    });

    return (
      <div>
        {renderedItems}
        <AddMultiComponent onAdd={(e) => this.handleAddItem(e)}/>
      </div>
    );
  }
}

export default ViewEditListComponent;