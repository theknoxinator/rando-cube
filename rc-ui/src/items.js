import React from 'react';
import ViewEditListComponent from './components/view-edit-list-component';

class Items extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showActiveList: false,
      showCompletedList: false,
    }
  }

  handleActiveListClick(event) {
    const showActiveList = this.state.showActiveList;
    this.setState({showActiveList: !showActiveList, showCompletedList: false});
  }

  handleCompletedListClick(event) {
    const showCompletedList = this.state.showCompletedList;
    this.setState({showActiveList: false, showCompletedList: !showCompletedList});
  }

  render() {
    const category = this.props.activeCategory || 'ALL';
    const activeListClasses = this.state.showActiveList ? 'btn btn-outline-secondary m-1' : 'btn btn-secondary m-1';
    const completedListClasses = this.state.showCompletedList ? 'btn btn-outline-secondary m-1' : 'btn btn-secondary m-1';
    const activeItems = this.props.activeItems;
    const completedItems = this.props.completedItems;
    const fieldsForAdd = [
      {key: 'Title', value: '', type: 'TEXT'},
      {key: 'Category', value: this.props.activeCategory, type: 'SELECT',
        options: this.props.categories.map((cat) => {return {value: cat, text: cat}}),
        optionsLabel: 'Category',
        disabled: this.props.activeCategory !== ''
      },
      {key: 'Priority', value: 'MEDIUM', type: 'SELECT',
        options: [{value: 'HIGH', text: 'High'}, {value: 'MEDIUM', text: 'Medium'}, {value: 'LOW', text: 'Low'}],
        optionsLabel: 'Priority',
        disabled: false
      }
    ];
    let renderedList;
    if (this.state.showActiveList) {
      renderedList = <ViewEditListComponent list={activeItems}
                                            fieldsForAdd={fieldsForAdd}
                                            onError={(e) => this.props.onError(e)} />
    } else if (this.state.showCompletedList) {
      renderedList = <ViewEditListComponent list={completedItems}
                                            onError={(e) => this.props.onError(e)} />
    } else {
      renderedList = <div/>
    }

    return (
      <div>
        <div className="row my-2">
          <div className="col">
            <button className={activeListClasses} onClick={(e) => this.handleActiveListClick(e)}>Show Active Items ({category})</button>
            <button className={completedListClasses} onClick={(e) => this.handleCompletedListClick(e)}>Show Completed Items ({category})</button>
          </div>
        </div>
        {renderedList}
      </div>
    );
  }
}

export default Items;