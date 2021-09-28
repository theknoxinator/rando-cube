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

  handleAddItem(values) {
    var item = {
      id: values['ID'],
      title: values['Title'],
      category: values['Category'],
      priority: values['Priority'],
    };
    this.props.onAdd(item);
  }

  handleEditItem(values, newValues) {
    var item = {
      id: values['ID'],
      title: newValues['Title'],
      category: newValues['Category'],
      priority: newValues['Priority'],
    };
    this.props.onEdit(item);
  }

  handleDeleteItem(values, migrateTo) {
    this.props.onDelete(values['ID']);
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

  getEditFields(allowDisable) {
    return [
      {key: 'Title', value: '', type: 'TEXT', disabled: false},
      {key: 'Category', value: this.props.activeCategory ? this.props.activeCategory : this.props.categories[0],
        type: 'SELECT',
        options: this.props.categories.map((cat) => {return {value: cat, text: cat}}),
        optionsLabel: 'Category',
        disabled: allowDisable && this.props.activeCategory !== ''
      },
      {key: 'Priority', value: 'MEDIUM', type: 'SELECT',
        options: [{value: 'HIGH', text: 'High'}, {value: 'MEDIUM', text: 'Medium'}, {value: 'LOW', text: 'Low'}],
        optionsLabel: 'Priority',
        disabled: false
      }
    ];
  }

  render() {
    const category = this.props.activeCategory || 'ALL';
    const activeListClasses = this.state.showActiveList ? 'btn btn-outline-secondary m-1' : 'btn btn-secondary m-1';
    const completedListClasses = this.state.showCompletedList ? 'btn btn-outline-secondary m-1' : 'btn btn-secondary m-1';
    const activeItems = this.props.activeItems.map((item) => this.convertItem(item));
    const fieldsForActiveView = ['Title', 'Category', 'Priority', 'Added'];
    const completedItems = this.props.completedItems.map((item) => this.convertItem(item));
    const fieldsForCompletedView = ['Title', 'Category', 'Completed'];
    const fieldsForAdd = this.getEditFields(true);
    const fieldsForEdit = this.getEditFields(false);
    let renderedList;
    if (this.state.showActiveList) {
      renderedList = <ViewEditListComponent list={activeItems}
                                            fieldsForAdd={fieldsForAdd}
                                            fieldsForDelete={fieldsForActiveView}
                                            fieldsForEdit={fieldsForEdit}
                                            fieldsForView={fieldsForActiveView}
                                            onAdd={(e) => this.handleAddItem(e)}
                                            onEdit={(e,f) => this.handleEditItem(e,f)}
                                            onDelete={(e,f) => this.handleDeleteItem(e,f)}
                                            onError={(e) => this.props.onError(e)} />
    } else if (this.state.showCompletedList) {
      renderedList = <ViewEditListComponent list={completedItems}
                                            fieldsForView={fieldsForCompletedView}
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