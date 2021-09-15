import React from 'react';
import {getCategories, getFullList, getCompletedList} from './backend';
import {SelectSingleField} from './fields/single-fields';
import Items from './items';

class Randomizer extends React.Component {
  render() {
    return (
      <div/>
    );
  }
}

class Rando extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      categories: [],
      activeItems: [],
      completedItems: [],
      active: '',
      errorMsg: null,
    }
    this.reloadCategories();
    this.reloadActiveItems(this.state.active);
    this.reloadCompletedItems(this.state.active);
  }

  handleError(error, keep=false) {
    const currentError = this.state.errorMsg;
    this.setState({errorMsg: keep ? currentError || error : error});
  }

  reloadCategories() {
    getCategories(
      (data) => this.setState({categories: data}),
      (e) => this.handleError(e, true)
    );
  }

  reloadActiveItems(category) {
    getFullList(category,
      (data) => this.setState({activeItems: data}),
      (e) => this.handleError(e, true)
    );
  }

  reloadCompletedItems(category) {
    getCompletedList(category,
      (data) => this.setState({completedItems: data}),
      (e) => this.handleError(e, true)
    );
  }

  getCategoryOptions() {
    const categories = this.state.categories;
    var options = [{value: '', text: 'Select Category'}];
    return options.concat(categories.map((category) => {
      return {value: category, text: category}
    }));
  }

  handleSubmit(event) {
    event.preventDefault();
  }

  handleCategorySelect(event) {
    const category = event.target.value
    this.setState({active: category});
    this.reloadActiveItems(category);
    this.reloadCompletedItems(category);
  }

  render() {
    const categories = this.state.categories;
    const active = this.state.active;
    const activeItems = this.state.activeItems;
    const completedItems = this.state.completedItems;
    const errorMsg = this.state.errorMsg;
    return (
      <div className="container-md">
        <form onSubmit={(e) => this.handleSubmit(e)}>
          {errorMsg &&
            <div className="alert alert-danger my-2" role="alert">
              {errorMsg}
            </div>
          }
          <SelectSingleField options={this.getCategoryOptions()}
                             onChange={(e) => this.handleCategorySelect(e)} />
          <Randomizer />
          <Items activeCategory={active}
                 categories={categories}
                 activeItems={activeItems}
                 completedItems={completedItems}
                 onError={(e) => this.handleError(e)} />
        </form>
      </div>
    );
  }
}

export default Rando;