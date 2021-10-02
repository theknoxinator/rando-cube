const baseUrl = 'http://192.168.1.192:8080';

export async function getCategories(handleResult, handleError) {
  return fetch(baseUrl + '/getCategories')
    .then(response => response.json())
    .then(result => {
      handleResult(result.categories);
      handleError(result.error);
    });
}

export async function addCategory(category, handleError) {
  var request = {'category': category};
  return fetch(baseUrl + '/addCategory', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  }).then(response => response.json())
    .then(result => handleError(result.error));
}

export async function editCategory(oldCategory, newCategory, handleError) {
  var request = {'oldCategory': oldCategory, 'newCategory': newCategory};
  return fetch(baseUrl + '/editCategory', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  }).then(response => response.json())
    .then(result => handleError(result.error));
}

export async function removeCategory(category, migrateTo, handleError) {
  var request = {'category': category, 'migrateTo': migrateTo};
  return fetch(baseUrl + '/removeCategory', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  }).then(response => response.json())
    .then(result => handleError(result.error));
}

export async function getRandomSet(category, useLast, handleResult, handleError) {
  return fetch(baseUrl + '/getRandomSet?category=' + category + '&useLast=' + useLast)
    .then(response => response.json())
    .then(result => {
      handleResult(result.items);
      handleError(result.error);
    });
}

export async function getFullList(category, handleResult, handleError) {
  return fetch(baseUrl + '/getFullList?category=' + category)
    .then(response => response.json())
    .then(result => {
      handleResult(result.items);
      handleError(result.error);
    });
}

export async function getCompletedList(category, handleResult, handleError) {
  return fetch(baseUrl + '/getCompletedList?category=' + category)
    .then(response => response.json())
    .then(result => {
      handleResult(result.items);
      handleError(result.error);
    });
}

export async function saveItem(item, handleError) {
  var request = {'item': item};
  return fetch(baseUrl + '/saveItem', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  }).then(response => response.json())
    .then(result => handleError(result.error));
}

export async function removeItem(id, handleError) {
  var request = {'id': id};
  return fetch(baseUrl + '/removeItem', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  }).then(response => response.json())
    .then(result => handleError(result.error));
}

export async function markCompleted(id, unmark, handleError) {
  var request = {'id': id, 'unmark': unmark};
  return fetch(baseUrl + '/markCompleted', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  }).then(response => response.json())
    .then(result => handleError(result.error));
}
