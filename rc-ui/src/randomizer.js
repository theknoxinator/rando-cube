import React from 'react';
import ViewMarkMultiComponent from './components/view-mark-multi-component';

class Randomizer extends React.Component {
  constructor(props) {
    super(props);
  }

  handleMarkItem(values) {
    this.props.onMark(values['ID']);
  }

  handleDeleteItem(values, migrateTo) {
    this.props.onDelete(values['ID']);
  }

  handleRandomizeClick(event) {
    this.props.onRandomize(false);
  }

  convertItem(item) {
      return {
        id: item.id,
        values: {
          'ID': item.id,
          'Title': item.title,
          'Category': item.category,
          'Priority': item.priority,
          'Added': item.added,
          'Completed': item.completed,
        }
      };
    }

  render() {
    const list = this.props.randomSet.map((item) => this.convertItem(item));
    const fieldsForView = ['Title', 'Priority'];
    const renderedList = list.map((item) => {
      return (
        <ViewMarkMultiComponent key={item.id}
                                values={item.values}
                                fieldsForDelete={fieldsForView}
                                fieldsForView={fieldsForView}
                                onMark={(e) => this.handleMarkItem(e)}
                                onDelete={(e,f) => this.handleDeleteItem(e,f)} />
      );
    });

    return (
      <div>
        <div className="row my-2">
          <div className="col">
            <button className="btn btn-secondary m-1" onClick={(e) => this.handleRandomizeClick(e)}>New Random Set</button>
          </div>
        </div>
        {renderedList}
        {(!renderedList || !renderedList.length) &&
          <div className="row my-2">
            <div className="col">
              Nothing to choose
            </div>
          </div>
        }
      </div>
    );
  }
}

export default Randomizer;