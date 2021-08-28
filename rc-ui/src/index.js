import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';

class Rando extends React.Component {
  render() {
    return "TODO";
  }
}

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
    <div className="row">
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
      return <AddSingleField
               value={this.state.value}
               onSubmit={(e) => this.handleClick(e)}
               onChange={(e) => this.handleChange(e)}
               onCancel={(e) => this.handleCancel(e)} />
    } else {
      return <AddButton onClick={() => this.handleClick()} />
    }
  }
}

class Categories extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      categories: ['Board Games', 'Books', 'Movies/TV', 'Video Games'],
    }
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

  render() {
    const categories = this.state.categories;
    const renderedCategories = categories.map((category) => {
      return (
        <div key={category} className="row my-2">
          <div className="col">{category}</div>
          <div className="col-4">
            <button className="btn btn-primary mx-1"><i className="bi-pencil"></i></button>
            <button className="btn btn-danger mx-1"><i className="bi-x-square"></i></button>
          </div>
        </div>
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

class Main extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tabs: [{
        name: 'Rando',
        content: <Rando/>,
      }, {
        name: 'Categories',
        content: <Categories/>,
      }],
      active: 'Rando',
    }
  }

  changeTab(name) {
    this.setState({
      active: name,
    });
  }

  render() {
    const tabs = this.state.tabs;
    const active = this.state.active;
    const renderedTabs = tabs.map((tab) => {
      const classes = tab.name === active ? 'nav-link active' : 'nav-link';
      return (
        <li key={tab.name} className="nav-item">
          <button className={classes} onClick={() => this.changeTab(tab.name)}>{tab.name}</button>
        </li>
      );
    });
    let renderedContent;
    for (const tab of tabs) {
      if (tab.name === active) {
        renderedContent = tab.content;
      }
    }

    return (
      <div className="main">
        <div className="main-nav">
          <ul className="nav nav-pills justify-content-center">
            {renderedTabs}
          </ul>
        </div>
        <div className="main-content">
          {renderedContent}
        </div>
      </div>
    );
  }
}

ReactDOM.render(
  <Main />,
  document.getElementById('root')
);