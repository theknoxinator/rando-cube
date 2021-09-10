import React from 'react';
import {getCategories, addCategory, editCategory, removeCategory} from './backend';
import AddSingleComponent from './components/add-single-component';
import ViewEditSingleComponent from './components/view-edit-single-component';

class Categories extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      categories: [],
      errorMsg: null,
    }
    this.reloadCategories();
  }

  getMigrateOptions(category) {
    const categories = this.state.categories;
    var migrateOptions = [];
    if (categories.length > 1) {
      migrateOptions.push({value: '', text: 'Do not migrate'});
    }
    return migrateOptions.concat(categories.filter(c => c !== category).map((category) => {
      return {value: category, text: "Migrate to " + category}
    }));
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

  handleSubmit(event) {
    event.preventDefault();
  }

  handleAddCategory(category) {
    addCategory(category, (e) => this.handleError(e))
      .then(() => this.reloadCategories());
  }

  handleEditCategory(oldCategory, newCategory) {
    editCategory(oldCategory, newCategory, (e) => this.handleError(e))
      .then(() => this.reloadCategories());
  }

  handleDeleteCategory(category, migrateTo) {
    removeCategory(category, migrateTo, (e) => this.handleError(e))
      .then(() => this.reloadCategories());
  }

  render() {
    const categories = this.state.categories;
    const errorMsg = this.state.errorMsg;
    const renderedCategories = categories.map((category) => {
      return (
        <ViewEditSingleComponent key={category}
                                 value={category}
                                 deleteOptions={this.getMigrateOptions(category)}
                                 deleteOptionsLabel="Migrate To"
                                 onEdit={(e,f) => this.handleEditCategory(e,f)}
                                 onDelete={(e,f) => this.handleDeleteCategory(e,f)} />
      );
    });
    return (
      <div className="container-md">
        <form onSubmit={(e) => this.handleSubmit(e)}>
          {errorMsg &&
            <div className="alert alert-danger my-2" role="alert">
              {errorMsg}
            </div>
          }
          {renderedCategories}
          <AddSingleComponent onAdd={(e) => this.handleAddCategory(e)}/>
        </form>
      </div>
    );
  }
}

export default Categories;