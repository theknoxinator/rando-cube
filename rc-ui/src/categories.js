import React from 'react';

function AddButton(props) {
  return (
    <div className="row">
      <div className="col">
        <button className="btn btn-primary" onClick={props.onClick}><i className="bi-plus-square"></i></button>
      </div>
    </div>
  );
}

function AddSingleField(props) {
  return (
    <div className="row my-2">
      <div className="col">
        <input type="text" value={props.value} onChange={props.onChange} />
      </div>
      <div className="col-4">
        <button className="btn btn-primary mx-1" onClick={props.onSubmit}><i className="bi-plus-square"></i></button>
        <button className="btn btn-outline-danger mx-1" onClick={props.onCancel}><i className="bi-x-square"></i></button>
      </div>
    </div>
  );
}

function EditSingleField(props) {
  return (
    <div className="row my-2">
      <div className="col">
        <input type="text" value={props.value} onChange={props.onChange} />
      </div>
      <div className="col-4">
        <button className="btn btn-primary mx-1" onClick={props.onSubmit}><i className="bi-check-square"></i></button>
        <button className="btn btn-outline-danger mx-1" onClick={props.onCancel}><i className="bi-x-square"></i></button>
      </div>
    </div>
  );
}

function DeleteSingleField(props) {
  const migrateOptions = props.migrateOptions.map((category) => {
    return (
      <option key={category} value={category}>Migrate to {category}</option>
    );
  });
  return (
    <div className="row my-2">
      <div className="col">
        {props.value}
      </div>
      <div className="col">
        <select className="form-select" aria-label="Migrate to" onChange={props.onChange}>
          <option selected>Do not migrate</option>
          {migrateOptions}
        </select>
      </div>
      <div className="col-4">
        <button className="btn btn-danger mx-1" onClick={props.onSubmit}><i className="bi-check-square"></i></button>
        <button className="btn btn-outline-primary mx-1" onClick={props.onCancel}><i className="bi-x-square"></i></button>
      </div>
    </div>
  );
}

function ViewSingleField(props) {
  return (
    <div className="row my-2">
      <div className="col">{props.value}</div>
      <div className="col-4">
        <button className="btn btn-primary mx-1" onClick={props.onEdit}><i className="bi-pencil"></i></button>
        <button className="btn btn-danger mx-1" onClick={props.onDelete}><i className="bi-trash"></i></button>
      </div>
    </div>
  );
}

class AddCategory extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      value: '',
      showField: false,
    }
  }

  handleChange(event) {
    this.setState({value: event.target.value});
  }

  handleClick(event) {
    const showField = this.state.showField;
    if (!showField) {
      this.setState({showField: true, value: ''});
    } else {
      const category = this.state.value;
      this.props.onAdd(category);
      this.setState({showField: false, value: ''});
    }
  }

  handleCancel(event) {
    this.setState({showField: false});
  }

  render() {
    const showField = this.state.showField;
    if (showField) {
      return <AddSingleField value={this.state.value}
                             onSubmit={(e) => this.handleClick(e)}
                             onChange={(e) => this.handleChange(e)}
                             onCancel={(e) => this.handleCancel(e)} />
    } else {
      return <AddButton onClick={() => this.handleClick()} />
    }
  }
}

class ViewEditCategory extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      value: props.value,
      newValue: '',
      migrateOptions: props.migrateOptions,
      migrateTo: '',
      editField: false,
      deleteField: false,
    }
  }

  handleChange(event) {
    this.setState({newValue: event.target.value});
  }

  handleEditClick(event) {
    const value = this.state.value;
    this.setState({editField: true, newValue: value});
  }

  handleEditConfirm(event) {
    const oldCategory = this.state.value;
    const newCategory = this.state.newValue;
    this.props.onEdit(oldCategory, newCategory);
    this.setState({editField: false, value: this.props.value, newValue: ''});
  }

  handleDeleteClick(event) {
    this.setState({deleteField: true});
  }

  handleDeleteConfirm(event) {
    const category = this.state.value;
    const migrateTo = this.state.migrateTo;
    this.props.onDelete(category, migrateTo);
  }

  handleMigrateChange(event) {
    this.setState({migrateTo: event.target.value});
  }

  handleCancel(event) {
    this.setState({editField: false, deleteField: false, newValue: ''});
  }

  render() {
    const editField = this.state.editField;
    const deleteField = this.state.deleteField;
    if (editField) {
      return <EditSingleField value={this.state.newValue}
                              onSubmit={(e) => this.handleEditConfirm(e)}
                              onChange={(e) => this.handleChange(e)}
                              onCancel={(e) => this.handleCancel(e)} />
    } else if (deleteField) {
      return <DeleteSingleField value={this.state.value}
                                migrateOptions={this.state.migrateOptions}
                                onSubmit={(e) => this.handleDeleteConfirm(e)}
                                onChange={(e) => this.handleMigrateChange(e)}
                                onCancel={(e) => this.handleCancel(e)} />
    } else {
      return <ViewSingleField value={this.state.value}
                              onEdit={(e) => this.handleEditClick(e)}
                              onDelete={(e) => this.handleDeleteClick(e)} />
    }
  }
}

const getCategoriesAsync = async (setCategories) => {
  fetch('http://localhost:8080/getCategories')
    .then(response => response.json())
    .then(result => {
      setCategories(result.categories);
    });
}

class Categories extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      categories: [],
    }
    getCategoriesAsync((data) => this.setState({categories: data}));
  }

  getMigrateOptions(category) {
    const categories = this.state.categories;
    return categories.filter(c => c !== category);
  }

  handleSubmit(event) {
    event.preventDefault();
  }

  handleAddCategory(category) {
    const categories = this.state.categories;
    this.setState({
      categories: categories.concat([category]),
    });
  }

  handleEditCategory(oldCategory, newCategory) {
    const categories = this.state.categories;
    this.setState({
      categories: categories.map(c => c === oldCategory ? newCategory : c)
    });
  }

  handleDeleteCategory(category, migrateTo) {
    const categories = this.state.categories;
    this.setState({
      categories : categories.filter(c => c !== category)
    });
  }

  render() {
    const categories = this.state.categories;
    const renderedCategories = categories.map((category) => {
      return (
        <ViewEditCategory key={category}
                          value={category}
                          migrateOptions={this.getMigrateOptions(category)}
                          onEdit={(e,f) => this.handleEditCategory(e,f)}
                          onDelete={(e,f) => this.handleDeleteCategory(e,f)} />
      );
    });
    return (
      <div className="container-md">
        <form onSubmit={(e) => this.handleSubmit(e)}>
          {renderedCategories}
          <AddCategory onAdd={(e) => this.handleAddCategory(e)}/>
        </form>
      </div>
    );
  }
}

export default Categories;