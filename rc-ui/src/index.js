import React from 'react';
import ReactDOM from 'react-dom';
import Categories from './categories';
import './index.css';

class Rando extends React.Component {
  render() {
    return "TODO";
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