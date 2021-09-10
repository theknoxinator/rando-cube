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
    const category = this.props.category || 'ALL';
    const activeListClasses = this.state.showActiveList ? 'btn btn-outline-secondary m-1' : 'btn btn-secondary m-1';
    const completedListClasses = this.state.showCompletedList ? 'btn btn-outline-secondary m-1' : 'btn btn-secondary m-1';
    let renderedList;
    if (this.state.showActiveList) {
      renderedList = <ViewEditListComponent category={this.props.category}
                                            onError={(e) => this.props.onError(e)} />
    } else if (this.state.showCompletedList) {
      renderedList = <div/>
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