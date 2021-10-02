export function AddMultiField(props) {
  const fields = props.fields.map((field) => {
    let options;
    if (field.type === 'SELECT') {
      options = field.options.map((option) => {
        return (
          <option key={option.value} value={option.value}>{option.text}</option>
        );
      });
    }

    return (
      <div key={field.key} className="col my-1">
        {field.type === 'TEXT' &&
          <input type="text" className="form-control" value={props.values[field.key]} placeholder={field.key}
                 disabled={field.disabled} onChange={(e) => props.onChange(field.key, e)} />
        }
        {field.type === 'SELECT' &&
          <select className="form-select" value={props.values[field.key]} aria-label={field.optionsLabel}
                  disabled={field.disabled} onChange={(e) => props.onChange(field.key, e)}>
            {options}
          </select>
        }
      </div>
    );
  });

  return (
    <div className="row my-2">
      {fields}
      <div className="col-4">
        <button className="btn btn-primary mx-1" onClick={props.onSubmit}><i className="bi-plus-square"></i></button>
        <button className="btn btn-outline-danger mx-1" onClick={props.onCancel}><i className="bi-x-square"></i></button>
      </div>
    </div>
  );
}

export function DeleteMultiField(props) {
  const values = props.fields.map((field) => {
    return (
      <div key={field} className="col my-1">
        {props.values[field]}
      </div>
    );
  });
  let options;
  if (props.options && props.options.length) {
    options = props.options.map((option) => {
      return (
        <option key={option.value} value={option.value}>{option.text}</option>
      );
    });
  }

  return (
    <div className="row my-2">
      {values}
      {options &&
        <div className="col">
          <select className="form-select" aria-label={props.optionsLabel} onChange={props.onChange}>
            {options}
          </select>
        </div>
      }
      <div className="col-4">
        <button className="btn btn-danger mx-1" onClick={props.onSubmit}><i className="bi-check-square"></i></button>
        <button className="btn btn-outline-primary mx-1" onClick={props.onCancel}><i className="bi-x-square"></i></button>
      </div>
    </div>
  );
}

export function EditMultiField(props) {
  const fields = props.fields.map((field) => {
    let options;
    if (field.type === 'SELECT') {
      options = field.options.map((option) => {
        return (
          <option key={option.value} value={option.value}>{option.text}</option>
        );
      });
    }

    return (
      <div key={field.key} className="col my-1">
        {field.type === 'TEXT' &&
          <input type="text" className="form-control" value={props.values[field.key]} placeholder={field.key}
                 disabled={field.disabled} onChange={(e) => props.onChange(field.key, e)} />
        }
        {field.type === 'SELECT' &&
          <select className="form-select" value={props.values[field.key]} aria-label={field.optionsLabel}
                  disabled={field.disabled} onChange={(e) => props.onChange(field.key, e)}>
            {options}
          </select>
        }
      </div>
    );
  });

  return (
    <div className="row my-2">
      {fields}
      <div className="col-4">
        <button className="btn btn-primary mx-1" onClick={props.onSubmit}><i className="bi-check-square"></i></button>
        <button className="btn btn-outline-danger mx-1" onClick={props.onCancel}><i className="bi-x-square"></i></button>
      </div>
    </div>
  );
}

export function MarkMultiField(props) {
  const values = props.fields.map((field) => {
    return (
      <div key={field} className="col my-1">
        {props.values[field]}
      </div>
    );
  });

  return (
    <div className="row my-2">
      {values}
      <div className="col-4">
        <button className="btn btn-outline-danger mx-1" onClick={props.onCancel}><i className="bi-x-square"></i></button>
        <button className="btn btn-primary mx-1" onClick={props.onSubmit}><i className="bi-check-square"></i></button>
      </div>
    </div>
  );
}

export function ViewMultiField(props) {
  const values = props.fields.map((field) => {
    return (
      <div key={field} className="col my-1">
        {props.values[field]}
      </div>
    );
  });
  const editIcon = props.editIcon ? props.editIcon : "bi-pencil";

  return (
    <div className="row my-2">
      {values}
      <div className="col-4">
        <button className="btn btn-primary mx-1" onClick={props.onEdit}><i className={editIcon}></i></button>
        <button className="btn btn-danger mx-1" onClick={props.onDelete}><i className="bi-trash"></i></button>
      </div>
    </div>
  );
}