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

The main view, or Rando view, will always show a selector for choosing the active category at the top, with no category
selected by default. If no category is selected, the randomizer section is not rendered. The list buttons are below the
randomizer section and are always rendered.

**Category Select**
* If the "Select Category" option is chosen from the selector, the randomizer section is rendered invisible if it was
  visible before, and the list buttons are modified to show (ALL) as the category
* If a category is chosen from the selector, the randomizer section is rendered and immediately shows the last set of
  choices available from the backend
  
**Randomizer**
* Will always show the last set of items for a given category by default on render
* The items are shown in a list with the title, priority, and date added shown (category is assumed)
* Each item in the list has Complete and Delete buttons next to the info text
* Clicking the Complete button next to the item will change the buttons to Confirm and Cancel buttons (in that order)
  * Clicking the Confirm button will send the request to mark the complete and re-render the set of items still using
    the last set flag so that the other items remain
  * Clicking the Cancel button will re-render the list, putting the buttons back to their default state
* Clicking the Delete button next to the item will change the buttons to Confirm and Cancel buttons (in that order)
  * Clicking the Confirm button will send the request to remove the item and re-render the set of items still using the
    last set flag so that the other items remain
  * Clicking the Cancel button will re-render the list, putting the buttons back to their default state
* Below the set of items there is a separate "New Choices" button that will send the request to re-render the set of
  items without the last set flag so that it will get a completely random set (weighted by priority)
* Any errors in responses will be displayed in red at the top of the randomizer section

**Full/Completed Lists**
* There are two buttons side by side, the left one says "To-Do (CATEGORY)" and the right one says "Completed (CATEGORY)"
  where CATEGORY is either the selected category from the selector or ALL if no category is selected
* Clicking on the To-Do button will render the list of active items for the selected category, and clicking on the
  Completed button will render the list of completed items for the selected category
* Each item in the active list will show the title, category, priority, and date added, as well as Edit and Delete
  buttons
* Each item in the completed list will show the title, category, priority, and date completed, as well Unmark and Delete
  buttons
* At the bottom of the active list only, there is an Add button that allows a new item to be added to the list

**Add**
* If the boxes next to Add are not visible when the Add button is clicked, the boxes will appear, pushing the Add 
  button to the right of the boxes
  * The boxes will be Title (input box), Category (selector), and Priority (selector)
  * If a category is already selected prior to clicking Add, the Category selector will be locked to that category
* If the boxes are visible and have values in them, clicking the Add button will send the request and re-render the list
  if successful, which will also make the boxes not visible by default
* Any errors in responses will be displayed in red at the top of the randomizer section

**Edit**
* Clicking the Edit button next to an item will change the text to boxes with the text already inside them, and also 
  change the Edit and Delete buttons to Confirm and Cancel buttons
* Clicking the Confirm button will send the request and re-render the list if successful, putting the buttons back to
  their default Edit and Delete state
* Clicking the Cancel button will re-render the list, putting the buttons back to their default Edit and Delete state
* Note that multiple items can be in edit mode at the same time but the Confirm button only applies to the given item
* Any errors in responses will be displayed in red at the top of the randomizer section

**Delete**
* Clicking the Delete button next to an item will change the Edit/Unmark and Delete buttons to Confirm and Cancel 
  buttons (in that order)
* Clicking the Confirm button will send the request and re-render the list if successful
* Clicking the Cancel button will re-render the list, putting the buttons back to their default state
* Any errors in responses will be displayed in red at the top of the randomizer section

**Unmark**
* Clicking the Unmark button next to a completed item will change the Unmark and Delete buttons to Confirm and Cancel 
  buttons (in that order)
* Clicking the Confirm button will send the request and re-render the list if successful
* Clicking the Cancel button will re-render the list, putting the buttons back to their default state
* Any errors in responses will be displayed in red at the top of the randomizer section


### Categories View

By default, the categories view will always show the list of categories in the system. Each category has edit and delete 
buttons next to the category text. There is an add button at the bottom of the list that allows a new category to be
added to the list.

**Add**
* If the text box next to Add is not visible when the Add button is clicked, the box will appear, pushing the Add button 
  to the right of the box
* If the text box is visible and has a value in it, clicking the Add button will send the request and re-render the list
  if successful, which will also make the text box not visible by default
* Any errors in responses will be displayed in red at the top of the categories list
  
**Edit**
* Clicking the Edit button next to a category will change the text to a text box with the text already inside it, and
  also change the Edit and Delete buttons to Confirm and Cancel buttons
* Clicking the Confirm button will send the request and re-render the list if successful, putting the buttons back to
  their default Edit and Delete state
* Clicking the Cancel button will re-render the list, putting the buttons back to their default Edit and Delete state
* Note that multiple categories can be in edit mode at the same time but the Confirm button only applies to the given
  category
* Any errors in responses will be displayed in red at the top of the categories list
  
**Delete**
* Clicking the Delete button next to a category will change the Edit and Delete buttons to Confirm and Cancel buttons 
  (in that order), and will display a selector box with the list of categories to migrate to, with "do not migrate" as 
  the default selected option (if the category is the last one, the list only has the "do not migrate" option)
* Clicking the Confirm button will send the request and re-render the normal view if successful
* Clicking the Cancel button will re-render the list, putting all buttons back to their default state
* Any errors in responses will be displayed in red at the top of the categories list

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
