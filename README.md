# RandoCube

### The Plan

The RandoCube project is a simple program for:
* Storing a list of to-do items
    * Items can be categorized
    * Items can be added using UI
* Requesting a set of random to-do items for consideration
* Once selected, a to-do item is removed from list

The project is composed of two modules:
* React frontend that provides the UI
* Spring boot backend that stores the data, persists the data, and sends the randomized sets to the frontend

### Technical Details

***Storage***

List data is stored as a simple JSON file. Format looks like:
```
File:
{
  "data":[<Item>],
  "history":[<Item>],
  "categories":[<Category>],
  "defaultSetSize":Integer,
  "lastSet":{<Category>:[<Item>]}
}

Item:
{
  "id":Integer,
  "title":String,
  "category":<Category>,
  "added":Timestamp,
  "completed":Timestamp,
  "priority":Integer
}
```

***API***

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

markCompleted
```
Request:
  {"id":Integer}
  
Response:
  {"error":<Error>}
```

addItem
```
Request:
  {<Item>}
  
Response:
  {"error":<Error>}
```

removeItem
```
Request:
  {"id":Integer}
  
Response:
  {"error":<Error>}
```
