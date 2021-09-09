import React from 'react';
import {getCategories} from './backend';

function SelectCategoryField(props) {
  const categories = props.categories.map((category) => {
    return (
      <option key={category} value={category}>{category}</option>
    );
  });
  return (
    <div className="row my-2">
      <div className="col">
        <select className="form-select" aria-label="Select Category" onChange={props.onChange}>
          <option value="">Select Category</option>
          {categories}
        </select>
      </div>
    </div>
  );
}

class Rando extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      categories: [],
      active: '',
      errorMsg: null,
    }
    this.reloadCategories();
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

  handleCategorySelect(event) {
    this.setState({active: event.target.value});
  }

  render() {
    const categories = this.state.categories;
    const errorMsg = this.state.errorMsg;
    return (
      <div className="container-md">
        <form onSubmit={(e) => this.handleSubmit(e)}>
          {errorMsg &&
            <div className="alert alert-danger my-2" role="alert">
              {errorMsg}
            </div>
          }
          <SelectCategoryField categories={categories}
                               onChange={(e) => this.handleCategorySelect(e)} />
        </form>
      </div>
    );
  }
}

export default Rando;