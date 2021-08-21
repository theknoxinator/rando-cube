# RandoCube

## The Plan

The RandoCube project is a simple program for:
* Storing a list of to-do items
    * Items can be categorized
    * Items can be added using UI
* Requesting a set of random to-do items for consideration
* Once selected, a to-do item is removed from list

The project is composed of two modules:
* React frontend that provides the UI
* Spring boot backend that stores the data, persists the data, and sends the randomized sets to the frontend

## UX Design

The website uses a single-page React design to keep it as simple as possible. Within the single page, there are two 
views: the main view and a categories view. Since categories are intended to be changed rarely, there is no need for it 
to be on the main page.

### Main View

TODO

### Categories View

By default, the categories view will always show the list of categories in the system. Each category has edit and remove 
buttons next to the category text. There is an add button at the bottom of the list that allows a new category to be
added to the list.

**Add**
* If the text box next to Add is not visible when the Add button is clicked, the box will appear, pushing the Add button 
  to the right of the box
* If the text box is visible and there is no value in it, clicking the Add button does nothing
* If the text box is visible and has a value in it, clicking the Add button will send the request and re-render the list
  if successful, which will also make the text box not visible by default
* Any errors in responses will be displayed in red beneath the text box and Add button
  
**Edit**
* Clicking the Edit button next to a category will change the text to a text box with the text already inside it, and
  also change the Edit and Remove buttons to Confirm and Cancel buttons
* Clicking the Confirm button will send the request and re-render the list if successful, putting the buttons back to
  their default Edit and Remove state
* Clicking the Cancel button will re-render the list, putting the buttons back to their default Edit and Remove state
* Note that multiple categories can be in edit mode at the same time, but once a Confirm or Cancel action is taken on
  one category in edit mode, all others are taken out of edit mode due to the whole list re-rendering
* Any errors in responses will be displayed in red beneath the text box and Confirm/Cancel buttons
  
**Remove**
* Clicking the Remove button next to a category will change the category text color to red, and will change the Edit and
  Remove buttons to Confirm and Cancel buttons (in that order), and will display a warning message next to the buttons
  "Are you sure" also in red text
* Clicking the Confirm button will bring up a new (migrate) view that shows the list of categories, minus the selected 
  category to remove, with radio buttons next to each, and two buttons at the bottom of the list for Migrate and Skip 
  Migration
  * Clicking the Migrate button when no radio button is selected will display and error message indicating a selection
    is required
  * Clicking the Migrate button when a radio button is select will send the request and re-render the normal view if
    successful
  * Clicking the Skip Migration button will send the request and re-render the normal view if successful
  * If the category being removed is the last one, this view is skipped entirely and follows the Skip Migration request
* Clicking the Cancel button will re-render the list, putting all buttons back to their default state
* Any errors in responses will be displayed at the top of the current view (either categories view or migrate view)

## Technical Details

### Storage

List data is stored as a simple JSON file. Format looks like:
```
File:
{
  "data":[<Item>],
  "history":[<Item>],
  "categories":[<Category>],
  "lastSets":{<Category>:[<Item>]},
  "defaultSetSize":Integer,
  "nextId":Integer
}

Item:
{
  "id":Integer,
  "title":String,
  "category":<Category>,
  "priority":<Priority>,
  "added":Timestamp,
  "completed":Timestamp
}
```

### API

***Categories***

getCategories
```
Request:
  N/A
  
Response:
  {
    "categories":[<Category>],
    "error":<Error>
  }
```

addCategory
```
Request:
  {"category":String} (required)
  
Response:
  {"error":<Error>}
```

editCategory
```
Request:
  {
    "oldCategory":String, (required)
    "newCategory":String (required)
  }
  
Response:
  {"error":<Error>}
```

removeCategory
```
Request:
  {
    "category":String, (required)
    "migrateTo":String (optional)
  }
  
Response:
  {"error":<Error>}
```

***Items***

getRandomSet
```
Request:
  ?category=<string> (required)
  &useLast=<boolean> (default true)
  
Response:
  {
    "items":[<Item>],
    "error":<Error>
  }
```

getFullList
```
Request:
  ?category=<string> (optional)
  
Response:
  {
    "items":[<Item>],
    "error":<Error>
  }
```

getCompletedList
```
Request:
  ?category=<string> (optional)
  
Response:
  {
    "items":[<Item>],
    "error":<Error>
  }
```

saveItem
```
Request:
  {
    "item":<Item>, (required)
    "ignoreDuplicate":Boolean (optional)
  }
  
Response:
  {"error":<Error>}
```

removeItem
```
Request:
  {"id":Integer} (required)
  
Response:
  {"error":<Error>}
```

markCompleted
```
Request:
  {
    "id":Integer, (required)
    "unmark":Boolean (optional)
  }
  
Response:
  {"error":<Error>}
```
