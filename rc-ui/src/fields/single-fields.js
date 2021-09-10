export function AddSingleField(props) {
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

export function DeleteSingleField(props) {
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
      <div className="col">
        {props.value}
      </div>
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

export function EditSingleField(props) {
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

export function SelectSingleField(props) {
  const options = props.options.map((option) => {
    return (
      <option key={option.value} value={option.value}>{option.text}</option>
    );
  });
  return (
    <div className="row my-2">
      <div className="col">
        <select className="form-select" aria-label={props.optionsLabel} onChange={props.onChange}>
          {options}
        </select>
      </div>
    </div>
  );
}

export function ViewSingleField(props) {
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