import React from 'react';
import {getCategories, getRandomSet, getFullList, getCompletedList, saveItem, removeItem, markCompleted} from './backend';
import {SelectSingleField} from './fields/single-fields';
import Randomizer from './randomizer';
import Items from './items';

class Rando extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      categories: [],
      randomSet: [],
      activeItems: [],
      completedItems: [],
      active: '',
      errorMsg: null,
    }
    this.reloadCategories();
    this.reloadRandomizer(this.state.active, true);
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

  reloadRandomizer(category, useLast) {
    if (category) {
      getRandomSet(category, useLast,
        (data) => this.setState({randomSet: data}),
        (e) => this.handleError(e, true)
      );
    } else {
      this.setState({randomSet: []});
    }
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
    this.reloadRandomizer(category, true);
    this.reloadActiveItems(category);
    this.reloadCompletedItems(category);
  }

  handleAddItem(item) {
    saveItem(item, (e) => this.handleError(e))
      .then(() => this.reloadActiveItems(this.state.active));
  }

  handleDeleteItem(id) {
    removeItem(id, (e) => this.handleError(e))
      .then(() => {
        this.reloadRandomizer(this.state.active, true);
        this.reloadActiveItems(this.state.active);
        this.reloadCompletedItems(this.state.active);
      });
  }

  handleEditItem(item) {
    saveItem(item, (e) => this.handleError(e))
      .then(() => this.reloadActiveItems(this.state.active));
  }

  handleMarkItem(id) {
    markCompleted(id, false, (e) => this.handleError(e))
      .then(() => {
        this.reloadRandomizer(this.state.active, true);
        this.reloadActiveItems(this.state.active);
        this.reloadCompletedItems(this.state.active);
      });
  }

  handleUnmarkItem(id) {
    markCompleted(id, true, (e) => this.handleError(e))
      .then(() => {
        this.reloadActiveItems(this.state.active);
        this.reloadCompletedItems(this.state.active);
      });
  }

  handleNewRandomSet(useLast) {
    this.reloadRandomizer(this.state.active, useLast);
  }

  render() {
    const categories = this.state.categories;
    const active = this.state.active;
    const randomSet = this.state.randomSet;
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
          <Randomizer randomSet={randomSet}
                      onRandomize={(e) => this.handleNewRandomSet(e)}
                      onMark={(e) => this.handleMarkItem(e)}
                      onDelete={(e) => this.handleDeleteItem(e)} />
          <Items activeCategory={active}
                 categories={categories}
                 activeItems={activeItems}
                 completedItems={completedItems}
                 onAdd={(e) => this.handleAddItem(e)}
                 onDelete={(e) => this.handleDeleteItem(e)}
                 onEdit={(e) => this.handleEditItem(e)}
                 onUnmark={(e) => this.handleUnmarkItem(e)}
                 onError={(e) => this.handleError(e)} />
        </form>
      </div>
    );
  }
}

export default Rando;